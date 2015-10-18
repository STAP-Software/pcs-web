/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Lock(LockType.READ)
public class UserPromptMgmt implements Serializable {

	@EJB
	ProcedureExecutionState procedureExecutionState;
	
	Logger logger = Logger.getLogger(this.getClass());

	private UserPrompt pendingPrompt;
	private Integer returnState;
	private int waitingForSecs;
	
	
	public int getWaitingForSecs() {
		return waitingForSecs;
	}

	@Lock(LockType.READ)
	public UserPrompt getPendingPrompt() {
		return pendingPrompt;
	}

	@Lock(LockType.READ)
	public void setPendingPrompt(UserPrompt pendingPrompt) {
		this.pendingPrompt = pendingPrompt;
	}

	@Lock(LockType.READ)
	public Integer getReturnState() {
		return returnState;
	}

	@Lock(LockType.READ)
	public void setReturnState(Integer returnState) {
		this.returnState = returnState;
	}

	@Abortable
	public void displayInfoDialog(String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayInfoDialog"));

		// change \n to <br/>
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_INFO, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayInfoDialog"));		
		
	}
	
	@Abortable
	public boolean displayYesNoDialog(String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayYesNoDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_YES_NO, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayYesNoDialog"));
		
		return (returnState.intValue() == 1) ? true : false;
	}
	
	@Abortable
	public int displayFlowControlTriFlowDialog(String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayFlowControlTriFlowDialog"));

		// change \n to <br/>
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_FLOW_CONTROL_TRIFLOW, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayFlowControlTriFlowDialog"));

		return returnState.intValue();
	}
	
	@Abortable
	public int displayFlowControlBiFlowDialog(String text) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayFlowControlBiFlowDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_FLOW_CONTROL_BIFLOW, text.replace("\n", "<br/>"));
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayFlowControlBiFlowDialog"));
		
		return returnState.intValue();
	}
	
	@Abortable
	public int displayGenericMultiChoiceDialog(String text, String[] choicesText, int[] choicesValues) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayGenericThreeChoiceDialog"));
		
		// change \n to <br/>
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_GENERIC_MULTI_CHOICE, text.replace("\n", "<br/>"), choicesText, choicesValues); 
		
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
