/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.FrameSimulator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.executor.CenterTelescopeExecutor;
import org.tmt.aps.peas.procedure.executor.CreateRefMapExecutor;
import org.tmt.aps.peas.procedure.executor.PassiveTiltExecutor;
import org.tmt.aps.peas.procedure.model.CenterTelescopeProcedureOutput;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.session.business.FieldMetaDataCache;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.ui.VisualizationController;

import magiqLibs.centroid.Centroid;

@Named
@SessionScoped
public class ProcedureController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	@EJB
	ProcedureMgmt procedureMgmt;
	@EJB
	PassiveTiltExecutor passiveTiltExecutor;
	@EJB
	CreateRefMapExecutor createRefMapExecutor;
	@EJB
	CenterTelescopeExecutor centerTelescopeExecutor;
	@EJB
	FrameMgmt frameMgmt;
	@EJB
	FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	FrameSimulator frameSimulator;
	@EJB
	ProcedureExecutionState procedureExecutionState;
	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	DcsMgmt dcsMgmt;
	@EJB
	FieldMetaDataCache fieldMetaDataCache;
	@EJB
	SessionMgmt sessionMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	@EJB
	ProcedureExecutionMgmt procedureExecutionMgmt;
	@EJB
	private ComputationContext computationContext;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private SessionController sessionController;
	@Inject
	private VisualizationController visualizationController;
	@Inject
	private StatusLogController statusLogController;
	@Inject
	private FrameController frameController;

	// need to exchange when changing from subprocedure and back
	Procedure procedure;
	Procedure superProcedure;

	// do not need to exchange
	float integrationAddTime;
	UploadedFile uploadFitsFile;
	List<FitsFilename> selectedFitsFiles;
	List<FitsFilename> availableFitsFiles;
	int selectedFrameNumber;
	ProcedureCcdFrame selectedFrame;
	byte[] falseColorPng;
	UserPrompt currentPrompt;
	Instrument frameInstrument;

	boolean frameMarkingMode = false;
	List<Procedure> procedureList;

	@PostConstruct
	private void init() throws Exception {

		currentPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_YES_NO, "My Default Text");

		// set up the instrument to be associated with each frame to display archived state
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		frameInstrument = cameraDefMgmt.findInstrument(instrumentId);
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public float getIntegrationAddTime() {
		return integrationAddTime;
	}

	public void setIntegrationAddTime(float integrationAddTime) {
		this.integrationAddTime = integrationAddTime;
	}

	public List<FitsFilename> getSelectedFitsFiles() {
		return selectedFitsFiles;
	}

	public void setSelectedFitsFiles(List<FitsFilename> selectedFitsFiles) {
		this.selectedFitsFiles = selectedFitsFiles;
	}

	public UploadedFile getUploadFitsFile() {
		return uploadFitsFile;
	}

	public String getFrameCentroidXs() {
		return frameDisplayMgmt.getCentroidXs();
	}

	public void setFrameCentroidXs(String centroidXs) {
		frameDisplayMgmt.setCentroidXs(centroidXs);
	}

	public String getFrameCentroidYs() {
		return frameDisplayMgmt.getCentroidYs();
	}

	public void setFrameCentroidYs(String centroidYs) {
		frameDisplayMgmt.setCentroidYs(centroidYs);
	}
	
	// search radius is from findCentConfig
	public String getFrameSearchRadius() {
		try {
			int irad = procedure.getProcedureConfigSet().getFindCentConfig().getIrad();
			if (frameMarkingMode)
				return "" + (irad * 2);
			return "" + irad;
		} catch (Throwable th) {
			return "6";
		}
	}

	public boolean isFrameMarkingMode() {
		return frameMarkingMode;
	}

	public void setFrameMarkingMode(boolean frameMarkingMode) {
		this.frameMarkingMode = frameMarkingMode;
	}

	public void setFrameSearchRadius(String searchRadius) {

	}

	public UserPrompt getCurrentPrompt() {
		return currentPrompt;
	}

	public void setCurrentPrompt(UserPrompt currentPrompt) {
		this.currentPrompt = currentPrompt;
	}

	public Instrument getFrameInstrument() {
		return frameInstrument;
	}

	public void setFrameInstrument(Instrument frameInstrument) {
		this.frameInstrument = frameInstrument;
	}

	public int getSelectedFrameNumber() {
		return selectedFrameNumber;
	}

	public void setSelectedFrameNumber(int selectedFrameNumber) {
		this.selectedFrameNumber = selectedFrameNumber;
	}

	public ProcedureCcdFrame getSelectedFrame() {
		return selectedFrame;
	}

	public void setSelectedFrame(ProcedureCcdFrame selectedFrame) {
		this.selectedFrame = selectedFrame;
	}

	public String getFrameInstructions() {
		return frameDisplayMgmt.getFrameInstructions();
	}

	public String getFrameInstructionImageName() {
		return frameDisplayMgmt.getFrameInstructionImageName();
	}

	public StreamedContent getGraphicImage() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Get ID value from actual request param.
			// String indexStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("frameIndex");

			// index is passed when the procedure has completed execution and we need to know which one
			// if index == null, then get the current procedure frame

			// if (indexStr == null) {
			// pcf = procedure.getLatestProcedureCcdFrame();
			// } else {
			selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);
			// }

			byte[] falseColorPng = selectedFrame.getCcdFrame().getFalseColorPng();

			logger.debug("falseColorPng = " + falseColorPng);

			if (falseColorPng == null) {
				return null;
			}
			return new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");
		}
	}

	public List<FitsFilename> getAvailableFitsFiles() {
		return availableFitsFiles;
	}

	public void handleFileUpload(FileUploadEvent event) {

		try {
			uploadFitsFile = event.getFile();

			CcdFrame loadedFitsFile = frameMgmt.loadFitsFrame(uploadFitsFile.getInputstream(), uploadFitsFile.getFileName());

			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);

			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public void doLoadFitsFile() {
		try {

			CcdFrame loadedFitsFile = frameMgmt.loadFitsFrame(selectedFitsFiles.get(0).getFileName());

			// set the frame source stored with the file
			procedure.getProcedureConfigSet().getProcedureConfig().setLightSource(loadedFitsFile.getFrameLightSource());

			// if a png file for display exists, read it in. Otherwise create it.
			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);

			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}

	public boolean getRenderPupilMaskSelect() {
		return procedure.getProcedureType().getProcedureTypeId().equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP);
	}

	public boolean getRenderNumTrials() {
		return procedure.getProcedureType().isFineScreen() || procedure.getProcedureType().isPassiveTilt();
	}

	public boolean getRenderFrameInstructions() {
		return frameDisplayMgmt.getFrameInstructions() != null;
	}

	// start button enable logic
	public boolean isStartEnabled() {
		if (procedure.getProcedureConfigSet().getProcedureConfig().getFrameSource() == ProcedureConfig.FRAME_SOURCE_FILE) {
			if (selectedFitsFiles == null || selectedFitsFiles.size() == 0) {
				return false;
			}
		}
		return true;
	}

	public void frameSourceListener() {
		logger.debug("Frame Source Listener");
	}

	// setup for each procedure type
	public String doNewPassiveTilt() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT, new PassiveTiltProcedureOutput());
	}

	public String doNewPhasing() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PHASING, null);
	}

	public String doNewFineScreen() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_FINE_SCREEN, null);
	}

	public String doNewSufs() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_SUFS, null);
	}

	public String doNewPupilRegistration() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PUPIL_REGISTRATION, null);
	}

	public String doNewCenterTelescope() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_CENTER_TELESCOPE, new CenterTelescopeProcedureOutput());
	}

	public String doNewCreateRefBeam() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, new CreateRefBeamMapProcedureOutput());
	}

	public String doNewCreateFirstRefBeam() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_CREATE_FIRST_REFERENCE_BEAM_MAP, null);
	}

	public String doNewLastProc() {
		ProcedureType lastProcedureType = sessionController.getCurrentSessionLastProcedure().getProcedureType();
		if (lastProcedureType.isCenterTelescope()) {
			return doNewCenterTelescope();
		} else if (lastProcedureType.isCreateRefMap()) {
			return doNewCreateRefBeam();
		} else if (lastProcedureType.isPassiveTilt()) {
			return doNewPassiveTilt();
		} else if (lastProcedureType.isPupilRegistration()) {
			return doNewPassiveTilt();
		} else if (lastProcedureType.isFineScreen()) {
			return doNewPassiveTilt();
		} else if (lastProcedureType.isPhasing()) {
			return doNewPassiveTilt();
		} else if (lastProcedureType.isSufs()) {
			return doNewPassiveTilt();
		} else {
			return null;
		}

	}

	public String doNewProcedure(Long procedureTypeId, ProcedureOutput procedureOutput) {

		try {

			Procedure lastProcedure = sessionController.getCurrentSessionLastProcedure();
			String testNumber = (lastProcedure != null) ? lastProcedure.getTestNumber() : "";

			procedure = procedureExecutionMgmt.performProcedureSetup(procedureTypeId, sessionController.getCurrentSession().getSessionId(),
					testNumber, procedureOutput);

			// add the procedure to the session
			sessionController.addNewProcedure(procedure);

			// clear the status log
			statusLogController.clearProcedureStatusLog();

			// clear any selected FITS files
			selectedFitsFiles = null;

			// create available FITS file list
			availableFitsFiles = frameController.getProcedureFitsFiles(procedure.getProcedureType().getProcedureTypeCd());
			Collections.sort(availableFitsFiles, new BeanComparator("fileName"));

			// clean up from previous procedure state
			procedureExecutionState.init(procedure);

			// set up visualization display list for later
			visualizationController.initVisualizationDisplays(procedureTypeId);

			logger.info("default mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			breadcrumbMenuBean.addFirstItem("Procedure #" + procedure.getProcedureNumber() + ": "
					+ procedure.getProcedureType().getProcedureTypeName(),
					"/modules/procedure/procedurePerspective.xhtml?faces-redirect=true");

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Initializing Procedure, check log files for details"));
			return null;
		}

		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}

	public String doCancelProcedure() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	public void doExecuteProcedure() {

		logger.debug(" ###############################  doExecuteProcedure:: starting: mask = "
				+ procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());

		logger.debug("doExecuteProcedure::mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		// TODO: check if this is passive tilt before performing this validation
		if (procedure.getProcedureType().isPassiveTilt()
				&& procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getWavelength() == 611.0) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		procedureExecutionMgmt.performProcedureStartup(procedure, selectedFitsFiles);

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back

		if (procedure.getProcedureType().isCreateRefMap()) {
			createRefMapExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isPassiveTilt()) {
			passiveTiltExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isCenterTelescope()) {
			centerTelescopeExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		}

		logger.debug("doExecuteProcedure::after executor call");

	}

	public void doOnLoad() {

		// if we are running, we need to restore some things

		if (procedureExecutionState.getExecutionStatus() == true) {

			// restart the poller
			RequestContext.getCurrentInstance().execute("procedureExecutionPoller.start();");

			// update the frame display
			// frameDisplayMgmt.setPendingDisplay(true);
			frameDisplayMgmt.setPendingMarkedDisplay(true);

			// popup any popups that are currently active
			RequestContext.getCurrentInstance().execute("drawSpots(); centroidsDisplayDialog.show()");

		}

	}

	public String doViewProcedure() {

		// we might be running....
		if (procedureExecutionState.getExecutionStatus() != true) {

			return doViewArchivedProcedure();

		}

		return null;
	}

	public String doViewArchivedProcedure() {

		try {

			procedure = procedureMgmt.findProcedure(procedure.getProcedureId());

			statusLogController.refreshProcedureStatusLog();

			// load up frames that were used
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
				
				String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

				CcdFrame loadedFitsFile = null;

				logger.debug("filename = " + filename);
				try {

					loadedFitsFile = frameMgmt.loadFitsFrame(filename);

				} catch (Exception e) {
					logger.error(MessageGenerator.generateMessage("generic.error"), e);
				}

				// if a png file for display exists, read it in. Otherwise create it.
				falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
				procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

			}
			
			// TODO: this needs to account for multiple frames someday.
			selectedFrame = procedure.getProcedureCcdFrameList().get(0);

			// set up display of camera state values for first frame
			loadCameraState(procedure.getProcedureCcdFrameList().get(0).getCcdFrame().getCameraState());

			// set up visualization displays
			visualizationController.initVisualizationDisplays(procedure.getProcedureType().getProcedureTypeId());

			// in case the values are not in the DB, just dummy some values
			if (procedure.getProcedureConfigSet().getFiConfig() == null) {
				procedure.getProcedureConfigSet().setFiConfig(new FIConfig());
			}

			breadcrumbMenuBean.removeTo("Session:");

			breadcrumbMenuBean.addItem("Procedure #" + procedure.getProcedureNumber() + ": "
					+ procedure.getProcedureType().getProcedureTypeName(),
					"/modules/procedure/procedurePerspective.xhtml?faces-redirect=true");

			return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error loading procedure data"));
			return null;
		}
	}

	public void doAbortProcedure() {
		procedureExecutionState.setAbortRequested(true);
	}

	public String doShowProcedureLog() {

		breadcrumbMenuBean.removeTo("Procedure #");

		breadcrumbMenuBean.addItem("Procedure Log", "/modules/procedure/procedureLog.xhtml");

		return "/modules/procedure/procedureLog.xhtml?faces-redirect=true";

	}

	// Maybe in another controller, not sure yet

	public void doSaveAdvancedOptions() {

	}

	public void doViewAdvancedOptions() {

	}

	public void doCancelSaveAdvancedOptions() {

	}

	public void doSaveExecutionPreferences() {

	}

	public void doCancelSaveExecutionPreferences() {

	}

	public void doUpdatePupilMask() {

		if (procedure.getProcedureType().getProcedureTypeId().equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {

			// change int time and selected ref beam settings in procedure config
			procedureExecutionMgmt.setupCreateRefMapDefaults(procedure, sessionController.getInstrument().getInstrumentId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		}
	}

	public void doUpdateFilter() {

		if (procedure.getProcedureType().getProcedureTypeId().equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {

			// change int time and selected ref beam settings in procedure config
			procedureExecutionMgmt.setupCreateRefMapDefaults(procedure, sessionController.getInstrument().getInstrumentId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		}
	}

	public void doSaveContext() {

		try {

			if (!procedure.isNewRecord()) {
				// only save if procedure has been saved. Prior to that, values will be persisted when the procedure is.
				procedureMgmt.updateProcedure(procedure);

				// for propagating value to the next procedure
				sessionController.updateCurrentSessionPersisted();
			}

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	public void loadCameraState(CameraState cameraState) {
		frameInstrument.updateState(cameraState);
	}

	public void frameSelectListener() {

		selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);
		loadCameraState(selectedFrame.getCcdFrame().getCameraState());
	}

	public void intTimeChangeListener(AjaxBehaviorEvent event) {
		Float intTime = procedure.getProcedureConfigSet().getProcedureConfig().getIntegrationTime();
		logger.debug("int time = " + intTime);
	}

	// ====================================================================================== //
	// Frame Displays //
	// ====================================================================================== //

	public void doHandMark() {

		frameMarkingMode = true;

		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");

		int x = 2 * (new Double(xStr)).intValue(); // 512 * 2 = 1024
		int y = 2 * (new Double(yStr)).intValue(); // 512 * 2 = 1024
		// add to the centroid hidden form vars

		// call findCent on each centroid
		// FIXME this means that frame marking needs to be a sub-procedure
		FloatPoint guess = new FloatPoint(x, y);
		// if findCent fails then we just use the user-marked guess as the centroid
		FindCentResult findCentResult = new FindCentResult(guess, 0.0f, 0.0f);

		try {
			FindCentConfig findCentConfig = (FindCentConfig) BeanUtils.cloneBean(procedure.getProcedureConfigSet().getFindCentConfig());
			// double the search radius for hand marking
			findCentConfig.setIrad(findCentConfig.getIrad() * 2);

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			float[][] frame = procedure.getLatestProcedureCcdFrame().getCcdFrame().getCorrectedFrame();

			findCentResult = computationLibrary.findCent(frame, guess, findCentConfig, Constants.SPOT_TYPE_INTERIOR);
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

		FloatPoint centroid = findCentResult.getCentroid();
		
		String centroidXs = getFrameCentroidXs();
		String centroidYs = getFrameCentroidYs();
		centroidXs = (centroidXs == null || centroidXs.trim().length() == 0) ? "" + centroid.x : centroidXs + "," + centroid.x;
		centroidYs = (centroidYs == null || centroidYs.trim().length() == 0) ? "" + centroid.y : centroidYs + "," + centroid.y;
		setFrameCentroidXs(centroidXs);
		setFrameCentroidYs(centroidYs);

		// make marking available to executor
		frameDisplayMgmt.setMarking(FloatListEncoder.decodeList(centroidXs), FloatListEncoder.decodeList(centroidYs));
	}

	public void doApplyMarking() {
		// TODO: put this in the action for the apply marking on the frame
		frameDisplayMgmt.setPendingMarkAction(false);

		frameMarkingMode = false;
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("instructionDialog.hide()");
	}

	public void doResetMarking() {
		setFrameCentroidXs(null);
		setFrameCentroidYs(null);
	}

	public void doUndoMarking() {
		// remove the last one marked
		String centroidXs = getFrameCentroidXs();
		String centroidYs = getFrameCentroidYs();

		List<Float> xList = FloatListEncoder.decodeList(centroidXs);
		List<Float> yList = FloatListEncoder.decodeList(centroidYs);

		if (!xList.isEmpty())
			xList.remove(xList.size() - 1);
		if (!yList.isEmpty())
			yList.remove(yList.size() - 1);

		centroidXs = FloatListEncoder.encodeList(xList);
		centroidYs = FloatListEncoder.encodeList(yList);

		setFrameCentroidXs(centroidXs);
		setFrameCentroidYs(centroidYs);
	}

}
