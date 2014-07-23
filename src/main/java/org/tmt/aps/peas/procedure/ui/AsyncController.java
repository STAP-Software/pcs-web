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
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
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
	ProcedureExecutionState procedureExecutionMgmt;
	@EJB 
	CameraMgmt cameraMgmt;
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

		checkCameraDisplay();
		
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
			procedureController.doUpdateDisplays();
			// update form values 
			// TODO: update other visualization displays once developed
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			requestContext.update("offsetsForm");
			requestContext.update("spotsForm");
			
			if (visualizationDisplay.isDisplayTypeCentroids()) {
				requestContext.execute("runDrawSpots(); centroidsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeCentroidOffsets()) {
				requestContext.execute("runDrawOffsets(); centroidOffsetDisplayDialog.show()");
			}
			
			graphicDisplayMgmt.setPendingDisplay(null);
		}
	}
	
	private void checkProcedureStatus() {
		sessionController.setProcedureExecuting(procedureExecutionMgmt.getExecutionStatus());

		// refresh the controller from the logger to get it to the display
		statusLogController.refreshCurrentProcedureStatusLog();
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("procedureDetailForm:miscPanel");
		requestContext.update("procedureDetailForm:controlPanel");
	}
	
	private void checkFrameDisplay() {
		
		if (getDisplayNewFrame() || getMarkNewFrame()) {
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("procedureDetailForm:framePanel");
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
		if (procedureExecutionMgmt.getExecutionStatus()) {
			// check to see if camera status query is complete, and if so kick off another one
			try {
				logger.debug("CheckCameraDisplay::refreshing status");
				cameraMgmt.refreshStatus();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("procedureDetailForm:miscPanel:cameraStatusPanel");
	}
	
	
	
	public void onComplete() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Procedure Completed", "Progress Completed"));
		RequestContext.getCurrentInstance().update("procedureDetailForm");
	}

	public String doAbortProcedure() {
		return null;
	}
	
	public void doCloseGraphicsDisplay() {
		graphicDisplayMgmt.setReturnState(1);
	}
	
	public void doCloseUserPromptYes() {
		userPromptMgmt.setReturnState(1);
	}

	public void doCloseUserPromptNo() {
		userPromptMgmt.setReturnState(0);
	}


	public boolean isExecutionStatus() {
		return procedureExecutionMgmt.getExecutionStatus();
	}


	public int getPercentComplete() {
		logger.debug("getPercentComplete::" + procedureExecutionMgmt.getPercentComplete());
		return procedureExecutionMgmt.getPercentComplete();
	}


	
	
	
}
