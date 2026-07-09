/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * Singleton EJB state machine for User Prompt display, user interaction waiting and return states.
 * @author smichaels
 */
@Singleton
@Lock(LockType.READ)
public class UserPromptMgmt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2985883383809789534L;

	@EJB
	ProcedureExecutionState procedureExecutionState;
	
	Logger logger = Logger.getLogger(this.getClass());

	private UserPrompt pendingPrompt;
	private Integer returnState;
	private int waitingForSecs;
	
	
	public int getWaitingForSecs() {
		return waitingForSecs;
	}

	/** 
	 * @return the user prompt pending display, if any
	 */
	@Lock(LockType.READ)
	public UserPrompt getPendingPrompt() {
		return pendingPrompt;
	}

	/**
	 * Sets a user prompt to be displayed.  This works in concert with {@link org.tmt.aps.peas.procedure.ui.AsyncController} which reads the state and performs the display.
	 */
	@Lock(LockType.READ)
	public void setPendingPrompt(UserPrompt pendingPrompt) {
		this.pendingPrompt = pendingPrompt;
	}

	/**
	 * @return the return state that the user chose from the prompt.
	 */
	@Lock(LockType.READ)
	public Integer getReturnState() {
		return returnState;
	}

	/**
	 * Sets the return state of the prompt.  
	 * @param returnState the return state to set
	 */
	@Lock(LockType.READ)
	public void setReturnState(Integer returnState) {
		this.returnState = returnState;
	}

	/**
	 * Displays an 'info' dialog with the passed header and text.  
	 * This method waits for display and the user response.
	 */
	@Abortable
	public void displayInfoDialog(String header, String text) {
		
		displayInfoDialog(header, text, false);		
		
	}
	
	/**
	 * Displays an 'info' dialog with the passed header and text.  
	 * This method waits for display and the user response.
	 * @param supressAbort if true, no 'Abort' button will be rendered on the prompt
	 */
	@Abortable
	public void displayInfoDialog(String header, String text, boolean supressAbort) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayInfoDialog"));

		// change \n to <br/>
		pendingPrompt = new UserPrompt(header, UserPrompt.PROMPT_TYPE_INFO, text.replace("\n", "<br/>"), supressAbort);
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayInfoDialog"));		
		
	}
	
	/**
	 * Displays a 'yes/no' dialog with the passed header and text.  
	 * This method waits for display and the user response.
	 * @return true if the user clicked "Yes", false otherwise
	 */
	@Abortable
	public boolean displayYesNoDialog(String header, String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayYesNoDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(header, UserPrompt.PROMPT_TYPE_YES_NO, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayYesNoDialog"));
		
		return (returnState.intValue() == 1) ? true : false;
	}
	
	/**
	 * Displays a 3 state flow related dialog with the passed header and text.  
	 * A 3 state flow dialog contains "Retry, Continue and Abort"
	 * This method waits for display and the user response.
	 * @return value corresponding to the button that was clicked
	 */
	@Abortable
	public int displayFlowControlTriFlowDialog(String header, String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayFlowControlTriFlowDialog"));

		// change \n to <br/>
		pendingPrompt = new UserPrompt(header, UserPrompt.PROMPT_TYPE_FLOW_CONTROL_TRIFLOW, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayFlowControlTriFlowDialog"));

		return returnState.intValue();
	}

	/**
	 * Displays a 2 state flow related dialog with the passed header and text.  
	 * A 2 state flow dialog contains "Retry and Abort"
	 * This method waits for display and the user response.
	 * @return value corresponding to the button that was clicked
	 */
	@Abortable
	public int displayFlowControlBiFlowDialog(String header, String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayFlowControlBiFlowDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(header, UserPrompt.PROMPT_TYPE_FLOW_CONTROL_BIFLOW, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayFlowControlBiFlowDialog"));
		
		return returnState.intValue();
	}
	
	/**
	 * Displays a dialog with the passed header and text, and arrays of button text and associated return values.
	 * This method waits for display and the user response.
	 * @param choicesText array of strings indicating what will be displayed on the dialog buttons
	 * @param choicesValues array of strings indicating the value that will be returned when the respective button is clicked
	 * @return value corresponding to the button that was clicked
	 */
	@Abortable
	public int displayGenericMultiChoiceDialog(String header, String text, String[] choicesText, int[] choicesValues) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayGenericThreeChoiceDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(header, UserPrompt.PROMPT_TYPE_GENERIC_MULTI_CHOICE, text.replace("\n", "<br/>"), choicesText, choicesValues); 
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayGenericThreeChoiceDialog"));
		
		return returnState.intValue();
	}
	
	private void waitForReturnState() {
		returnState = null;
		// here we wait until the return state changes
		while(returnState == null) {
			
			waitingForSecs++;
			
			Utils.waitFor(1000);

		}
		
		waitingForSecs = 0;

	}

}
