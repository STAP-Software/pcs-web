/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;
import org.tmt.aps.peas.visualization.ui.VisualizationController;

@Named
@SessionScoped
public class AsyncController implements Serializable {

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
	FrameController frameController;
	@Inject
	StatusLogController statusLogController;
	@Inject
	ProcedureController procedureController;
	@Inject
	VisualizationController visualizationController;
	@Inject
	BreadcrumbMenuBean breadcrumbMenuBean;
	
	
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

	public boolean getAbortRequested() {
		return procedureExecutionState.getAbortRequested();
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
		
		checkIsWaiting();
		
		checkSubProcedureStart();
		
		checkSubProcedureEnd();
		
		// will execute if on the last time through
		if (!procedureExecutionState.getExecutionStatus() && !isOnCompletePerformed()) {
			logger.info("CALLING ONCOMPLETE");
			onComplete();
		}
		
	}
	
	private void checkUserPrompt() {
	
		// ask user prompt display manager for any pending user prompts
		
		if (userPromptMgmt.getPendingPrompt() != null) {
			procedureController.setCurrentPrompt(userPromptMgmt.getPendingPrompt());
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext..." + procedureController.getCurrentPrompt().getMessage());
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			requestContext.update("promptDialog"); 
			requestContext.update("promptDialogForm"); 
			requestContext.execute("userPromptDialog.show()");
			
			userPromptMgmt.setPendingPrompt(null);
		}

	}
	
	private void checkVisualizationDisplays() {
		
		if (getAbortRequested()) {
			// just in case a new display got called after an abort, we don't want to display it
			doCloseGraphicsDisplay();
			graphicDisplayMgmt.setPendingDisplay(null);
		}
		
		// ask graphic display manager for any pending displays
		VisualizationDisplay visualizationDisplay = graphicDisplayMgmt.getPendingDisplay();

		
		if (visualizationDisplay != null) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			
			visualizationController.setCurrentDisplay(visualizationDisplay);
			
			// get data into form
			//visualizationController.doUpdateDisplays();
			
			// update form values 
			// TODO: update other visualization displays once developed
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			requestContext.update("offsetsForm");
			requestContext.update("avgPtOffsetsForm");
			requestContext.update("avgFsOffsetsForm");
			requestContext.update("spotsForm");
			requestContext.update("actDeltasForm");
			requestContext.update("edgeHeightsForm");
			requestContext.update("edgeResidualsForm");
			requestContext.update("sufsOffsetsForm");
			
			
			if (visualizationDisplay.isDisplayTypeCentroids()) {
				requestContext.execute("runDrawSpots(); centroidsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeCentroidOffsets()) {
				requestContext.execute("runDrawOffsets(); centroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeAvgPtCentroidOffsets()) {
				requestContext.execute("runDrawAvgPtOffsets(); avgPtCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeAvgFsCentroidOffsets()) {
				requestContext.execute("runDrawAvgFsOffsets(); avgFsCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeActuatorDeltas()) {
				requestContext.execute("runDrawActDeltas(); actuatorDeltasDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeEdgeHeights()) {
				requestContext.execute("runDrawEdgeHeights(); edgeHeightsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeEdgeResiduals()) {
				requestContext.execute("runDrawEdgeResiduals(); edgeResidualsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeSufsCentroidOffsets()) {
				requestContext.execute("runDrawSufsOffsets(); sufsCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeAvgSufsCentroidOffsets()) {
				requestContext.execute("runDrawAvgSufsOffsets(); avgSufsCentroidOffsetDisplayDialog.show()");
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
			
			// setup the selected frame
			int selectedFrameNumber = frameDisplayMgmt.getFrameNumber();
			procedureController.setSelectedFrameNumber(selectedFrameNumber);
			ProcedureCcdFrame selectedFrame = procedureController.getProcedure().getProcedureCcdFrameList().get(selectedFrameNumber);
			procedureController.setSelectedFrame(selectedFrame);
			
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

	private void checkSubProcedureStart() {

		if (procedureExecutionState.isSubProcedureStartRequested()) {
			
			// get subprocedure into the controller, move super procedure to the stack
			Procedure procedure = procedureExecutionState.transferControlToSubProcedure();
			procedureController.setProcedure(procedure);
			sessionController.addNewProcedure(procedure);

			// refresh the controller from the logger to get it to the display
			statusLogController.refreshCurrentProcedureStatusLog();
			
			breadcrumbMenuBean.addItem(procedure.getProcedureType().getProcedureTypeName() + " - EMBEDDED SUBPROCEDURE RUNNING",
					"/modules/procedure/procedurePerspective.xhtml");

			
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("procedureDetailForm:miscPanel");
			requestContext.update("procedureDetailForm:controlPanel");
			requestContext.update("breadcrumbForm");
		}
	}
	
	private void checkSubProcedureEnd() {

		if (procedureExecutionState.isSubProcedureEndRequested()) {
	
			RequestContext requestContext = RequestContext.getCurrentInstance();

			
			// we captured it, so reset it for next time, if any
			procedureExecutionState.resetSubProcedureEndRequested();
			
			// get procedure into the controller
			Procedure procedure = procedureExecutionState.getCurrentProcedure();
			
			procedureController.setProcedure(procedure);

			// refresh the controller from the logger to get it to the display
			statusLogController.refreshCurrentProcedureStatusLog();
			
			breadcrumbMenuBean.removeLast();

			
			requestContext.update("procedureDetailForm:miscPanel");
			requestContext.update("procedureDetailForm:controlPanel");
			requestContext.update("breadcrumbForm");

		}
	}

	private void checkCameraDisplay() {

		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.update("procedureDetailForm:miscPanel:cameraStatusPanel");
	}
	
	private void checkMessages() {
		if (procedureExecutionState.getProcedureException() != null) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.procedureFailedMessage(procedureExecutionState.getProcedureException()));
		}
	}
	
	private void checkIsWaiting() {
		// check to see if we are waiting on user input, and for how long
		
		int waitForUserThreshold = 30;
		
		int secs = graphicDisplayMgmt.getWaitingForSecs() + userPromptMgmt.getWaitingForSecs();
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (secs > waitForUserThreshold) {
			requestContext.execute("play_wake_up_sound();");
		} else {
			requestContext.execute("stop_wake_up_sound();");
		}
	}
	
	public void onComplete() {
		
		try {
			// update camera state to be the one associated with the first frame.  We do it here because it is the 'last' asynchronous thing we do
			CameraState cameraState = procedureController.getProcedure().getProcedureCcdFrameList().get(0).getCcdFrame().getCameraState();
			procedureController.loadCameraState(cameraState);
			statusLogController.refreshProcedureStatusLog();
			
			// update the fits frames to be available to the rest of the application
			frameController.reloadFits();
			
			if (procedureExecutionState.getProcedureException() == null) {
				// put up completion notice if there was no exception
				FacesContext.getCurrentInstance().addMessage(null, Utils.procedureSuccessfulMessage(procedureController.getProcedure().getProcedureType().getProcedureTypeName()));
				RequestContext.getCurrentInstance().update("procedureDetailForm");
			}
			
			// display frame
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("procedureDetailForm:framePanel");
			requestContext.execute("markFrame()");


			// update the breadcrumb to associate the current session as the first link
			Session session = sessionController.getCurrentSession();
			breadcrumbMenuBean.insertFirst("Session: " + session.getTelescope().getTelescopeName() + " - (" + session.getSessionDateFormatted() + ")", "/modules/session/sessionDetail.xhtml");
			RequestContext.getCurrentInstance().update("breadcrumbForm");
			
			// update the currentSessionPersisted for use in the UI
			sessionController.updateCurrentSessionPersisted();
			
		} catch (Throwable e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		} finally {
			setOnCompletePerformed(true);
			logger.info("ONCOMPLETE COMPLTETED");
		}
	}

	public String doAbortProcedure() {
		return null;
	}
	
	public void doCloseGraphicsDisplay() {
		graphicDisplayMgmt.setReturnState(1);
	}
	
	public void doCloseGraphicsDisplay1() {
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_INFO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_YES);
		}
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_YES);
		}
	}
	
	public void doCloseGraphicsDisplay2() {
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_NO);
		}
	}
	
	public void doCloseGraphicsDisplayAbort() {
		procedureExecutionState.setAbortRequested(true);
		graphicDisplayMgmt.setReturnState(1);
	}

	
	public void doCloseUserPrompt1() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton1Value());
	}

	public void doCloseUserPrompt2() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton2Value());
	}

	public void doCloseUserPrompt3() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton3Value());
	}

	public void doCloseUserPromptAbort() {
		procedureExecutionState.setAbortRequested(true);
		userPromptMgmt.setReturnState(-1);
	}


	public boolean isExecutionStatus() {
		return procedureExecutionState.getExecutionStatus();
	}

	public boolean isOnCompletePerformed() {
		return procedureExecutionState.getOnCompletePerformed();
	}
	public void setOnCompletePerformed(boolean state) {
		procedureExecutionState.setOnCompletePerformed(state);
	}

	public int getPercentComplete() {
		logger.debug("getPercentComplete::" + procedureExecutionState.getPercentComplete());
		return procedureExecutionState.getPercentComplete();
	}

	
}
