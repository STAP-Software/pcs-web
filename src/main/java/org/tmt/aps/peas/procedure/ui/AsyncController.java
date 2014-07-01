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
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
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
	ProcedureExecutionState procedureExecutionMgmt;
	@Inject
	SessionController sessionController;
	@Inject
	StatusLogController statusLogController;
	@Inject
	ProcedureController procedureController;
	
	UserPrompt currentPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_YES_NO, "Default Text");
	
	
	
	public UserPrompt getCurrentPrompt() {
		return currentPrompt;
	}

	public void setCurrentPrompt(UserPrompt currentPrompt) {
		this.currentPrompt = currentPrompt;
	}
	
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



	public void asyncListener() {

		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Polling...");
		RequestContext requestContext = RequestContext.getCurrentInstance();
		// ask user prompt display manager for any pending user prompts
		// ask graphic display manager for any pending displays
		if (userPromptMgmt.getPendingPrompt() != null) {
			currentPrompt = userPromptMgmt.getPendingPrompt();
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext..." + currentPrompt.getMessage());
			requestContext.update("promptDialogForm"); 
			requestContext.execute("userPromptDialog.show()");
			userPromptMgmt.setPendingPrompt(null);
		}

		// ask graphic display manager for any pending displays
		VisualizationDisplay visualizationDisplay = graphicDisplayMgmt.getPendingDisplay();
		if (visualizationDisplay != null) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			
			// get data into form
			procedureController.doUpdateDisplays();
			// update form values 
			// TODO: update other visualization displays once developed
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
		
		sessionController.setProcedureExecuting(procedureExecutionMgmt.getExecutionStatus());

		// refresh the controller from the logger to get it to the display
		statusLogController.refreshCurrentProcedureStatusLog();
		
		// can we do this to update the frame?
		System.out.println("updating form");
		requestContext.update("procedureDetailForm:miscPanel");
		requestContext.update("procedureDetailForm:controlPanel");
		if (getDisplayNewFrame() || getMarkNewFrame()) {
			requestContext.update("procedureDetailForm:framePanel");
			requestContext.execute("drawFrame()");
			setDisplayNewFrame(false);
		}
		if (getMarkNewFrame()) {
			requestContext.execute("markFrame()");
			setMarkNewFrame(false);
		}
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
	
	public void doCloseUserPrompt() {
		userPromptMgmt.setReturnState(1);
	}


	public boolean isExecutionStatus() {
		return procedureExecutionMgmt.getExecutionStatus();
	}


	public int getPercentComplete() {
		logger.debug("getPercentComplete::" + procedureExecutionMgmt.getPercentComplete());
		return procedureExecutionMgmt.getPercentComplete();
	}


	
	
	
}
