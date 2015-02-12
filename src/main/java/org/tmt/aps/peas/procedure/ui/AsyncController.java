/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Named
@SessionScoped
public class AsyncController {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	UserPromptMgmt userPromptMgmt;
	@EJB 
	CameraMgmt cameraMgmt;
	@EJB 
	ProcedureExecutionState procedureExecutionState;
	@Inject
	SessionController sessionController;
	@Inject
	StatusLogController statusLogController;
	@Inject
	ProcedureController procedureController;
	

	
	public boolean getDisplayNewFrame() {
		return frameDisplayMgmt.getPendingDisplay();
	}
	public void setDisplayNewFrame(boolean value) {
		frameDisplayMgmt.setPendingDisplay(false);
	}
	public boolean getMarkNewFrame() {
		return frameDisplayMgmt.getPendingMarkedDisplay();
	}
	public void setMarkNewFrame(boolean value) {
		frameDisplayMgmt.setPendingMarkedDisplay(false);
	}

	public boolean getFrameMarkActionPending() {
		return frameDisplayMgmt.getPendingMarkAction();
	}


	public void asyncListener() {

		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Polling...");
		
		checkUserPrompt();
		
		checkVisualizationDisplays();
		
		checkProcedureStatus();
		
		checkFrameDisplay();

		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Update camera display...");

		checkCameraDisplay();
		
		checkMessages();
		
		// will execute if on the last time through
		if (!procedureExecutionState.getExecutionStatus()) {
			onComplete();
		}
		
	}
	
	private void checkUserPrompt() {
	
		// ask user prompt display manager for any pending user prompts
		
		if (userPromptMgmt.getPendingPrompt() != null) {
			procedureController.setCurrentPrompt(userPromptMgmt.getPendingPrompt());
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext..." + procedureController.getCurrentPrompt().getMessage());
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			requestContext.update("promptDialogForm"); 
			requestContext.execute("userPromptDialog.show()");
			
			userPromptMgmt.setPendingPrompt(null);
		}

	}
	
	private void checkVisualizationDisplays() {
		// ask graphic display manager for any pending displays
		VisualizationDisplay visualizationDisplay = graphicDisplayMgmt.getPendingDisplay();
		if (visualizationDisplay != null) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			
			// get data into form
			//visualizationController.doUpdateDisplays();
			
			// update form values 
			// TODO: update other visualization displays once developed
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			requestContext.update("offsetsForm");
			requestContext.update("spotsForm");
			requestContext.update("actDeltasForm");
			
			
			if (visualizationDisplay.isDisplayTypeCentroids()) {
				requestContext.execute("drawSpots(); centroidsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeCentroidOffsets()) {
				requestContext.execute("runDrawOffsets(); centroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeActuatorDeltas()) {
				requestContext.execute("runDrawActDeltas(); actuatorDeltasDisplayDialog.show()");
			}
			
			graphicDisplayMgmt.setPendingDisplay(null);
		}
	}
	
	private void checkProcedureStatus() {

		// refresh the controller from the logger to get it to the display
		statusLogController.refreshCurrentProcedureStatusLog();
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("procedureDetailForm:miscPanel");
		requestContext.update("procedureDetailForm:controlPanel");
	}
	
	private void checkFrameDisplay() {
		
		if (getDisplayNewFrame() || getMarkNewFrame()) {
			
			// get the marking to the procedure
			String xList = FloatPointListEncoder.encodeXList(frameDisplayMgmt.getMarkList());
			String yList = FloatPointListEncoder.encodeYList(frameDisplayMgmt.getMarkList());
			
			procedureController.setFrameCentroidXs(xList);
			procedureController.setFrameCentroidYs(yList);
			
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("procedureDetailForm:framePanel");
			requestContext.update("frameHiddenForm");
			requestContext.execute("drawFrame()");
			
			if (frameDisplayMgmt.getFrameInstructions() != null) {
				requestContext.update("instructionDialogForm");
				requestContext.execute("instructionDialog.show()");
			}
			
			setDisplayNewFrame(false);
		}
		if (getMarkNewFrame()) {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("markFrame()");
			setMarkNewFrame(false);
		}
	}

	private void checkCameraDisplay() {

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("procedureDetailForm:miscPanel:cameraStatusPanel");
	}
	
	private void checkMessages() {
		if (procedureExecutionState.getProcedureException() != null) {
			String message = procedureExecutionState.getProcedureException().getMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Procedure Error: ", message));
		}
	}
	
	public void onComplete() {
		
		// update camera state to be the one associated with the first frame.  We do it here because it is the 'last' asynchronous thing we do
		CameraState cameraState = procedureController.getProcedure().getProcedureCcdFrameList().get(0).getCcdFrame().getCameraState();
		procedureController.loadCameraState(cameraState);
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Procedure Completed", "Progress Completed"));
		RequestContext.getCurrentInstance().update("procedureDetailForm");
	}

	public String doAbortProcedure() {
		return null;
	}
	
	public void doCloseGraphicsDisplay() {
		graphicDisplayMgmt.setReturnState(1);
	}
	
	public void doCloseUserPrompt1() {
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_YES);
		}
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE);
		}
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_FLOW_CONTROL_BIFLOW) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY);
		}
	}

	public void doCloseUserPrompt2() {
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_NO);
		}
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY);
		}
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_FLOW_CONTROL_BIFLOW) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT);
		}
	}

	public void doCloseUserPrompt3() {
		if (procedureController.getCurrentPrompt().getPromptType() == UserPrompt.PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) {
			userPromptMgmt.setReturnState(UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT);
		}
	}


	public boolean isExecutionStatus() {
		return procedureExecutionState.getExecutionStatus();
	}


	public int getPercentComplete() {
		logger.debug("getPercentComplete::" + procedureExecutionState.getPercentComplete());
		return procedureExecutionState.getPercentComplete();
	}


	
	
	
}
