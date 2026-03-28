/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.CcdState;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.BadDarkMedianValueException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;
import org.tmt.aps.peas.visualization.ui.VisualizationController;

/**
 * JSF Controller class continuously polled during a procedure execution; checks all display queues and user reponses and sets states accordingly.
 * 
 * @author smichaels
 *
 */
@Named("asyncController")
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
	
	/**
	 * This method is called through ajax by the procedureExecutionPoller component within procedurePerspective.xhtml
	 * This is the event loop for asynchronously initiated events
	 * This method calls 
	 * <ul>
	 * <li>{@link #checkUserPrompt()} - checks {@link UserPromptMgmt} for pending prompts, and displays them if required</li>
	 * <li>{@link #checkVisualizationDisplays()} - checks {@link GraphicDisplayMgmt} for pending visualization displays, and displays them if required </li>
	 * <li>{@link #checkProcedureStatus()} - checks for additions to the status log {@link org.tmt.aps.peas.statusLog.ui.StatusLogController} and appends these as required</li> 
	 * <li>{@link #checkFrameDisplay()} - checks for new frames to display, marking instructions, etc</li> 
	 * <li>{@link #checkCameraDisplay} - updates the camera display with most recent data</li>
	 * <li>{@link #checkMessages()} - if a procedure exception is detected, displays the procedure abort exeception</li> 
	 * <li>{@link #checkIsWaiting()} - waits for user input to a dialog and if exceeds threshold, begins to play annoying reminder sound</li> 
	 * <li>{@link #checkSubProcedureStart()} - checks the {@link ProcedureExecutionState} if a subprocedure start is requested and sets up user interface to reflect new subprocedure data</li> 
	 * <li>{@link #checkSubProcedureEnd()} - checks the {@link ProcedureExecutionState} if a subprocedure end is requested and sets up user interface to reflect previous super-procedure data</li>
	 * <li>{@link #onComplete} - sets up display and state to render the procedure as completed</li>
	 * </ul>
	 */
	public void asyncListener() {

		try {
		
		
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>> Polling...");
			
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
			
			if (!procedureExecutionState.getExecutionStatus() && isOnCompletePerformed()) {
				checkCompleteMessage();
			}
		
		} catch (Exception e) {
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>> Polling ERROR", e);
		}
				
	}
	
	private void checkUserPrompt() {
	
		// ask user prompt display manager for any pending user prompts
		
		if (userPromptMgmt.getPendingPrompt() != null) {
			procedureController.setCurrentPrompt(userPromptMgmt.getPendingPrompt());
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext..." + procedureController.getCurrentPrompt().getMessage());
					
			
			PrimeFaces.current().ajax().update("promptDialog", "promptDialogForm");
			
			PrimeFaces.current().executeScript("PF('userPromptDialog').show();");

	
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
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to update Forms...");
			
			visualizationController.setCurrentDisplay(visualizationDisplay);
			
			// get data into form
			//visualizationController.doUpdateDisplays();
			
			// update form values 
	
			
			PrimeFaces.current().ajax().update("offsetsForm");
			PrimeFaces.current().ajax().update("avgPtOffsetsForm");
			PrimeFaces.current().ajax().update("avgFsOffsetsForm");
			PrimeFaces.current().ajax().update("spotsForm");
			PrimeFaces.current().ajax().update("actDeltasForm");
			PrimeFaces.current().ajax().update("edgeHeightsForm");
			PrimeFaces.current().ajax().update("singleFilterEdgeHeightsForm");
			PrimeFaces.current().ajax().update("edgeResidualsForm");
			PrimeFaces.current().ajax().update("sufsOffsetsForm");
			PrimeFaces.current().ajax().update("avgSufsOffsetsForm");
			
			
			if (visualizationDisplay.isDisplayTypeCentroids()) {
				
				PrimeFaces.current().ajax().update("centroidsDialog", "spotsForm");
				
				PrimeFaces.current().executeScript("runDrawSpots(); PF('centroidsDisplayDialog').show();");
			}
			
			if (visualizationDisplay.isDisplayTypeCentroidOffsets()) {
				
				PrimeFaces.current().ajax().update("centroidOffsetDialog", "offsetsForm");
				
				PrimeFaces.current().executeScript("runDrawOffsets(); PF('centroidOffsetDisplayDialog').show();");

			}
			if (visualizationDisplay.isDisplayTypeAvgPtCentroidOffsets()) {
				PrimeFaces.current().executeScript("runDrawAvgPtOffsets(); avgPtCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeAvgFsCentroidOffsets()) {
				PrimeFaces.current().executeScript("runDrawAvgFsOffsets(); avgFsCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeActuatorDeltas()) {
				PrimeFaces.current().executeScript("runDrawActDeltas(); actuatorDeltasDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeEdgeHeights()) {
				PrimeFaces.current().executeScript("runDrawEdgeHeights(); edgeHeightsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeSingleFilterEdgeHeights()) {
				PrimeFaces.current().executeScript("runDrawSingleFilterEdgeHeights(); singleFilterEdgeHeightsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeEdgeResiduals()) {
				PrimeFaces.current().executeScript("runDrawEdgeResiduals(); edgeResidualsDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeSufsCentroidOffsets()) {
				PrimeFaces.current().executeScript("runDrawSufsOffsets(); sufsCentroidOffsetDisplayDialog.show()");
			}
			if (visualizationDisplay.isDisplayTypeAvgSufsCentroidOffsets()) {
				PrimeFaces.current().executeScript("runDrawAvgSufsOffsets(); avgSufsCentroidOffsetDisplayDialog.show()");
			}
			
			graphicDisplayMgmt.setPendingDisplay(null);
		}
	}
	
	private void checkProcedureStatus() {

		// refresh the controller from the logger to get it to the display
		statusLogController.refreshCurrentProcedureStatusLog();

		//PrimeFaces.current().ajax().update("procedureDetailForm:miscPanel");
		PrimeFaces.current().ajax().update("procedureDetailForm:controlPanel");
	}
	
	private void checkFrameDisplay() {
		
		
		if (getDisplayNewFrame() || getMarkNewFrame()) {
			
			// setup the selected frame
			int selectedFrameNumber = frameDisplayMgmt.getFrameNumber();
			
			// if frame from file, and going from create ref map and back, we can loose procedure context, 
			// so don't display if the procedure does not have that frame number
			
			// V3.0 there is no list of ProcedureCcdFrames that persist in the Procedure EJB across transaction boundaries (New Hibernate rule)
			// so we store the current ProcedureCcdFrame in the ProcedureExecutionState and get it from there
			//if (procedureController.getProcedure().getProcedureCcdFrameList().size() > selectedFrameNumber) {

				procedureController.setSelectedFrameNumber(selectedFrameNumber);
				//ProcedureCcdFrame selectedFrame = procedureController.getProcedure().getProcedureCcdFrameList().get(selectedFrameNumber);
				ProcedureCcdFrame selectedFrame = procedureExecutionState.getCurrentProcedureCcdFrame();
				procedureController.setSelectedFrame(selectedFrame);
				procedureController.setBlankImage(false); // hack so that new images are never considered overwritten
				
				logger.info("##########################  CHECK FRAME DISPLAY: SelectedFrame = " + selectedFrame);
				
				// get the marking to the procedure
				String xList = FloatPointListEncoder.encodeXList(frameDisplayMgmt.getMarkList());
				String yList = FloatPointListEncoder.encodeYList(frameDisplayMgmt.getMarkList());
				
				procedureController.setFrameCentroidXs(xList);
				procedureController.setFrameCentroidYs(yList);
				

				PrimeFaces.current().ajax().update("procedureDetailForm:framePanel");
				PrimeFaces.current().ajax().update("frameHiddenForm");
							
				PrimeFaces.current().executeScript("drawFrame()");
				
				if (frameDisplayMgmt.getFrameInstructions() != null) {
					PrimeFaces.current().ajax().update("instructionDialogForm");
					PrimeFaces.current().executeScript("instructionDialog.show()");
				}
				
			//}
			
			setDisplayNewFrame(false);
		}
		
		if (getMarkNewFrame()) {

			PrimeFaces.current().executeScript("markFrame()");
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

			

			//PrimeFaces.current().ajax().update("procedureDetailForm:miscPanel");
			PrimeFaces.current().ajax().update("procedureDetailForm:controlPanel");
			PrimeFaces.current().ajax().update("breadcrumbForm");
		}
	}
	
	private void checkSubProcedureEnd() {

		if (procedureExecutionState.isSubProcedureEndRequested()) {
	


			
			// we captured it, so reset it for next time, if any
			procedureExecutionState.resetSubProcedureEndRequested();
			
			// get procedure into the controller
			Procedure procedure = procedureExecutionState.getCurrentProcedure();
			
			procedureController.setProcedure(procedure);

			// refresh the controller from the logger to get it to the display
			statusLogController.refreshCurrentProcedureStatusLog();
			
			breadcrumbMenuBean.removeLast();

			
			//PrimeFaces.current().ajax().update("procedureDetailForm:miscPanel");
			PrimeFaces.current().ajax().update("procedureDetailForm:controlPanel");
			PrimeFaces.current().ajax().update("breadcrumbForm");

		}
	}

	private void checkCameraDisplay() {


		PrimeFaces.current().ajax().update("procedureDetailForm:miscPanel:cameraStatusPanel");
	}
	
	private void checkMessages() {
		if (procedureExecutionState.getProcedureException() != null) {
			
			if (procedureExecutionState.getProcedureException() instanceof BadDarkMedianValueException) {
				FacesContext.getCurrentInstance().addMessage(null, Utils.procedureFailedMessage(procedureExecutionState.getProcedureException()));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, Utils.procedureFailedMessageCheckLogs(procedureExecutionState.getProcedureException()));
			}
		}
	}
	
	private void checkIsWaiting() {
		// check to see if we are waiting on user input, and for how long
		
		int waitForUserThreshold = 30;
		
		int secs = graphicDisplayMgmt.getWaitingForSecs() + userPromptMgmt.getWaitingForSecs();
		

		if (secs > waitForUserThreshold) {
			PrimeFaces.current().executeScript("play_wake_up_sound();");
		} else {
			PrimeFaces.current().executeScript("stop_wake_up_sound();");
		}
	}
	

	
	public void onComplete() {
		
		try {
			// update camera state to be the one associated with the first frame.  We do it here because it is the 'last' asynchronous thing we do
			if (procedureController.getProcedure().getProcedureCcdFrameList() != null ) {
				CameraState cameraState = procedureController.getProcedure().getProcedureCcdFrameList().get(0).getCcdFrame().getCameraState();
				CcdState ccdState = procedureController.getProcedure().getProcedureCcdFrameList().get(0).getCcdFrame().getCcdState();
				procedureController.loadInstrumentState(cameraState, ccdState);
			}
			statusLogController.refreshProcedureStatusLog();
			
			// update the fits frames to be available to the rest of the application
			frameController.reload();
			
			
			// display frame

			PrimeFaces.current().ajax().update("procedureDetailForm:framePanel");
			PrimeFaces.current().executeScript("markFrame()");


			// update the breadcrumb to associate the current session as the first link
			Session session = sessionController.getCurrentSession();
			breadcrumbMenuBean.insertFirst("Session: " + session.getTelescope().getTelescopeName() + " - (" + session.getSessionDateFormatted() + ")", "/modules/session/sessionDetail.xhtml");
			PrimeFaces.current().ajax().update("breadcrumbForm");
			
			// update the currentSessionPersisted for use in the UI
			sessionController.updateCurrentSession();
			
			PrimeFaces.current().ajax().update("procedureDetailForm:controlPanel");
			
			
		} catch (Throwable e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		} finally {
			setOnCompletePerformed(true);
			
			PrimeFaces.current().executeScript("PF('procedureExecutionPoller').stop()");
			
			procedureController.doViewArchivedProcedure();
			
			logger.info("ONCOMPLETE COMPLETED");
		}
	}

	private void checkCompleteMessage() {
		
		if (procedureExecutionState.getProcedureException() == null) {
			// put up completion notice if there was no exception
			FacesContext.getCurrentInstance().addMessage(null, Utils.procedureSuccessfulMessage(procedureController.getProcedure().getProcedureType().getProcedureTypeName()));
			PrimeFaces.current().ajax().update("procedureDetailForm");
		}
		
		statusLogController.refreshProcedureStatusLog();

	}

	/**
	 * JSF Action method called when visualization display close button is clicked.
	 */
	public void doCloseGraphicsDisplay() {
		System.out.println("closing graphics display");
		graphicDisplayMgmt.setReturnState(1);
		visualizationController.setCurrentDisplay(null);
	}
	
	/**
	 * JSF Action method called when visualization display 'Yes' button is clicked
	 */
	public void doCloseGraphicsDisplay1() {
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_INFO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_YES);
		}
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_YES);
		}
		visualizationController.setCurrentDisplay(null);

	}
	
	/**
	 * JSF Action method called when visualization display 'No' button is clicked
	 */
	public void doCloseGraphicsDisplay2() {
		if (visualizationController.getCurrentDisplay().getPromptType() == UserPrompt.PROMPT_TYPE_YES_NO) {
			graphicDisplayMgmt.setReturnState(UserPrompt.PROMPT_VALUE_YES_NO_NO);
		}
		visualizationController.setCurrentDisplay(null);

	}
	
	/**
	 * JSF Action method called when visualization display 'Abort' button is clicked
	 */
	public void doCloseGraphicsDisplayAbort() {
		procedureExecutionState.setAbortRequested(true);
		graphicDisplayMgmt.setReturnState(1);
		visualizationController.setCurrentDisplay(null);

	}

	/**
	 * JSF Action method called when user prompt button #1 is clicked
	 */
	public void doCloseUserPrompt1() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton1Value());
		procedureController.setCurrentPrompt(null);
	}

	/**
	 * JSF Action method called when user prompt button #2 is clicked
	 */
	public void doCloseUserPrompt2() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton2Value());
		procedureController.setCurrentPrompt(null);

	}

	/**
	 * JSF Action method called when user prompt button #3 is clicked
	 */
	public void doCloseUserPrompt3() {
		userPromptMgmt.setReturnState(procedureController.getCurrentPrompt().getButton3Value());
		procedureController.setCurrentPrompt(null);

	}

	/**
	 * JSF Action method called when user prompt Abort button is clicked
	 */
	public void doCloseUserPromptAbort() {
		procedureExecutionState.setAbortRequested(true);
		userPromptMgmt.setReturnState(-1);
		procedureController.setCurrentPrompt(null);

	}

	public boolean isExecutionStatus() {
		return procedureExecutionState.getExecutionStatus();
	}

	/**
	 * @return true if the {@link #onComplete} method has been completed
	 */
	public boolean isOnCompletePerformed() {
		return procedureExecutionState.getOnCompletePerformed();
	}
	public void setOnCompletePerformed(boolean state) {
		procedureExecutionState.setOnCompletePerformed(state);
	}

	/**
	 * @return the percent complete to display on the user interface
	 */
	public int getPercentComplete() {
	
		return procedureExecutionState.getPercentComplete();
	}

	
}
