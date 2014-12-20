/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FIConfigDefaults;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.FindCentConfigDefaults;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.ProcedureConfigDefaults;
import org.tmt.aps.peas.config.model.RefMapDefaults;
import org.tmt.aps.peas.config.model.Subimage;
import org.tmt.aps.peas.config.ui.GlobalConfigController;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.FrameSimulator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.executor.CenterTelescopeExecutor;
import org.tmt.aps.peas.procedure.executor.CreateRefMapExecutor;
import org.tmt.aps.peas.procedure.executor.PassiveTiltExecutor;
import org.tmt.aps.peas.procedure.model.CenterTelescopeProcedureOutput;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOuput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.business.FieldMetaDataCache;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.VisualizationDisplayMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;
import org.tmt.aps.peas.visualization.ui.VisualizationDisplayLink;

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
	VisualizationDisplayMgmt visualizationDisplayMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private SessionController sessionController;
	@Inject
	private GlobalConfigController globalConfigController;
	@Inject
	private StatusLogController statusLogController;
	@Inject
	private FrameController frameController;
	@Inject
	private GraphicDisplayMgmt graphicDisplayMgmt;

	Procedure procedure;
	ProcedureType procedureType;

	List<Procedure> procedureList;
	float integrationAddTime;
	UploadedFile uploadFitsFile;
	List<FitsFilename> selectedFitsFiles;
	byte[] falseColorPng;
	private TreeNode visualizationDisplayRoot;
	
	PupilMask defaultMask; // current default mask for procedure type
	ProcedureCcdFrame procedureCcdFrame;

	UserPrompt currentPrompt;
	
	boolean centroidDisplayEnabled;
	boolean centroidOffsetDisplayEnabled;
	boolean avgCentroidOffsetDisplayEnabled;
	boolean actuatorDeltaDisplayEnabled;	
	
	Instrument frameInstrument;
	int selectedFrameNumber;
	ProcedureCcdFrame selectedFrame;
	List<Float> integrationTimeList;

	
	@PostConstruct
	private void init() throws Exception {
		
		currentPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_YES_NO, "My Default Text");
		
		// set up the instrument to be associated with each frame to display archived state
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		frameInstrument = cameraDefMgmt.findInstrument(instrumentId);		
	}
	
	private void initVisualizationDisplays(Long procedureTypeId) {
		
		centroidDisplayEnabled = false;
		centroidOffsetDisplayEnabled = false;
		avgCentroidOffsetDisplayEnabled = false;
		actuatorDeltaDisplayEnabled = false;

		List<VisualizationDisplay> visualizationDisplayList = visualizationDisplayMgmt.findVisualizationDisplays(procedureTypeId);
		
		visualizationDisplayRoot = new DefaultTreeNode("Root", null);
		
		TreeNode node0 = new DefaultTreeNode("folder", "Iteration 1", visualizationDisplayRoot);
		
		for (VisualizationDisplay visualizationDisplay : visualizationDisplayList) {

			switch (visualizationDisplay.getVisualizationDisplayId().intValue()) {
			case VisualizationDisplay.DISPLAY_TYPE_CENTROIDS:
				new DefaultTreeNode("link", new VisualizationDisplayLink("Centroids", "runDrawSpots();centroidsDisplayDialog.show()"), node0);
				centroidDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS:
				new DefaultTreeNode("link", new VisualizationDisplayLink("Centroid Offsets", "runDrawOffsets(); centroidOffsetDisplayDialog.show()"), node0);
				centroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_AVG_CENTROID_OFFSETS:
				new DefaultTreeNode("link", new VisualizationDisplayLink("Avg. Centroid Offsets", "centroidOffsetDisplayDialog.show()"), node0);
				avgCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS:
				new DefaultTreeNode("link", new VisualizationDisplayLink("Actuator Deltas", "centroidOffsetDisplayDialog.show()"), node0);
				actuatorDeltaDisplayEnabled = true;
				break;	
			}
		}

		node0.setExpanded(true);
	}
	
	public List<Float> getIntegrationTimeList() {
		return integrationTimeList;
	}
	public void setIntegrationTimeList(List<Float> integrationTimeList) {
		this.integrationTimeList = integrationTimeList;
	}

	public boolean isCentroidDisplayEnabled() {
		return centroidDisplayEnabled;
	}	

	public boolean isCentroidOffsetDisplayEnabled() {
		return centroidOffsetDisplayEnabled;
	}

	public boolean isAvgCentroidOffsetDisplayEnabled() {
		return avgCentroidOffsetDisplayEnabled;
	}

	public boolean isActuatorDeltaDisplayEnabled() {
		return actuatorDeltaDisplayEnabled;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}

	public ProcedureCcdFrame getProcedureCcdFrame() {
		return procedureCcdFrame;
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

	public String getCentroidXs() {
		return graphicDisplayMgmt.getCentroidXs();
	}

	public void setCentroidXs(String centroidXs) {
		graphicDisplayMgmt.setCentroidXs(centroidXs);
	}

	public String getCentroidYs() {
		return graphicDisplayMgmt.getCentroidYs();
	}

	public void setCentroidYs(String centroidYs) {
		graphicDisplayMgmt.setCentroidYs(centroidYs);
	}

	public String getCentroidNbrs() {
		return graphicDisplayMgmt.getCentroidNbrs();
	}

	public void setCentroidNbrs(String centroidNbrs) {
		graphicDisplayMgmt.setCentroidNbrs(centroidNbrs);
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
		if (procedure.getProcedureConfigSet().getFindCentConfig() == null) {
			return "6";
		}
		return "" + procedure.getProcedureConfigSet().getFindCentConfig().getIrad();
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

	public StreamedContent getGraphicImage() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Get ID value from actual request param.
			//String indexStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("frameIndex");

			// index is passed when the procedure has completed execution and we need to know which one
			// if index == null, then get the current procedure frame


//			if (indexStr == null) {
//				pcf = procedure.getLatestProcedureCcdFrame();
//			} else {
				selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);
//			}
			

			byte[] falseColorPng = selectedFrame.getCcdFrame().getFalseColorPng();

			logger.debug("falseColorPng = " + falseColorPng);

			if (falseColorPng == null) {
				return null;
			}
			return new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");
		}
	}

	public TreeNode getVisualizationDisplayRoot() {
		return visualizationDisplayRoot;
	}

	public List<FitsFilename> getAvailableFitsFiles() {
		List<FitsFilename> fitsFileList = frameController.getProcedureFitsFiles(procedureType.getProcedureTypeCd());
		logger.debug("FitsFileList size = " + fitsFileList.size());
		return fitsFileList;
	}

	public void handleFileUpload(FileUploadEvent event) {

		try {
			uploadFitsFile = event.getFile();

			CcdFrame loadedFitsFile = frameMgmt.loadFitsFrame(uploadFitsFile.getInputstream(), uploadFitsFile.getFileName());

			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);

			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}

	}

	public boolean getRenderPupilMaskSelect() {
		
		return procedure.getProcedureType().getProcedureTypeId().equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP);

	}
	
	public boolean getRenderNumTrials() {
		System.out.println("type   =  " + procedureType + ", id = " + procedureType.getProcedureTypeId());
		return procedureType.isFineScreen() || procedureType.isPassiveTilt();
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
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT, new PassiveTiltProcedureOuput());
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
	
	public String doNewProcedure(Long procedureTypeId, ProcedureOutput procedureOutput) {

		try {
			procedure = new Procedure();

			// get the procedure type object
			procedureType = procedureMgmt.findProcedureType(procedureTypeId);
			procedure.setProcedureType(procedureType);
			
			procedure.setProcedureOutput(procedureOutput);
			
			sessionController.setCurrentProcedureTypeId(procedureTypeId);

			ProcedureConfigDefaults procedureConfigDefaults = procedureMgmt.findDefaultProcedureConfig(sessionController.getTelescope().getTelescopeId(),
					sessionController.getInstrument().getInstrumentId(), procedureTypeId);
			
			// copy the default config into the current procedure config for potential modification
			ProcedureConfig procedureConfig = new ProcedureConfig(procedureConfigDefaults);
			
			// and associate it with the procedure
			procedure.getProcedureConfigSet().setProcedureConfig(procedureConfig);
			

			// get the default mask, if it is installed on the wheel
			defaultMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(procedureConfig.getPupilMaskType().getPupilMaskTypeId(), 
					sessionController.getInstrument().getCamera().getPupilWheel().getPupilWheelId());
			
			procedureConfig.setPupilMask(defaultMask);
			
			// get the filter to default to if it exists
			Filter defaultFilter = cameraDefMgmt.getFilterByFilterTypeAndWheel(procedureConfig.getFilterType().getFilterTypeId(), 
					sessionController.getInstrument().getCamera().getFilterWheel().getFilterWheelId());
			
			procedureConfig.setFilter(defaultFilter);
			
			// if procedure type is create ref map, then populate ref beam and integration times from the table
			if (procedureTypeId.equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {
				
				// these get set into procedure config
				setupCreateRefMapDefaults(sessionController.getInstrument().getInstrumentId(), 
						defaultMask.getPupilMaskType().getPupilMaskTypeId(), 
						defaultFilter.getFilterType().getFilterTypeId());
			} else {
				// set up default int times for all procedure types except for reference beam
				integrationTimeList =  procedureConfig.getIntegrationTimeList();
			}
			
			// select defaults based on mask and light source
			updateFIConfig();
			
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_NEW);

			
			// add it to the session and give it a procedure number
			sessionController.setupNewProcedure(procedure);

			
			// clear the status log
			statusLogController.clearProcedureStatusLog();
			
			// clear any selected FITS files
			selectedFitsFiles = null;
			
			// clear any marking
			frameDisplayMgmt.clearMarking();

			// clean up from previous procedure state
			procedureExecutionState.init(procedure);

			// set up visualization display list for later
			initVisualizationDisplays(procedureTypeId);
			
			logger.info("default mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = new Date();
			breadcrumbMenuBean.addFirstItem(procedureType.getProcedureTypeName() + " - " + sdf.format(date), "/modules/procedure/procedurePerspective.xhtml");

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Initializing Procedure, check log files for details"));
			return null;
		}

		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}
	
	private void setupCreateRefMapDefaults(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		RefMapDefaults refMapDefaults = globalConfigMgmt.findRefMapDefaults(instrumentId, pupilMaskTypeId, filterTypeId);
		
		// Set up default ref beam and int time
		
		procedure.getProcedureConfigSet().getProcedureConfig().setLightSource(ProcedureConfig.LIGHT_SOURCE_LED);
		procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(refMapDefaults.getReferenceBeam());
		procedure.getProcedureConfigSet().getProcedureConfig().setIntegrationTime(refMapDefaults.getIntegrationTime());
		
		// make the list of possible int times equal to the 'one' we have
		integrationTimeList = new ArrayList<Float>();
		integrationTimeList.add(refMapDefaults.getIntegrationTime());
	}

	public String doCancelProcedure() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	private void updateFIConfig() throws Exception {
		if (!procedure.getProcedureType().isCenterTelescope()) {

			FIConfigDefaults fiConfigDefaults = globalConfigMgmt.findFIConfigDefaults(sessionController.getInstrument().getInstrumentId(), 
				procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(),
				procedure.getProcedureConfigSet().getProcedureConfig().getLightSource());
			
			// use defaults as actuals if user doesn't subsequently change them
			
			// TODO: need to add these fields to the database
			//fiConfig.setForceScaleSource(1);
			//fiConfig.setForceRotationSource(1);
			//fiConfig.setForceScaleValue(0.0f);
			//fiConfig.setForceRotationValue(0.0f);
			
			FIConfig fiConfig = new FIConfig(fiConfigDefaults);
			
			procedure.getProcedureConfigSet().setFiConfig(fiConfig);

		}
	}
	
	
	public void doExecuteProcedure() {

		logger.debug(" ###############################  doExecuteProcedure:: starting: mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());

		// tell the world so the UI can disable things the user cannot touch
		procedureExecutionState.setExecutionStatus(true);

		// global config needs loaded in case it has changed from nominal
		GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt.findDefaultConfig(sessionController.getTelescope().getTelescopeId(), 
				sessionController.getInstrument().getInstrumentId());
		// store with procedure config set
		procedure.getProcedureConfigSet().setGlobalConfig(new GlobalConfig(globalConfigDefaults));

		procedure.setInstrument(sessionController.getInstrument());
		procedure.setTelescope(sessionController.getTelescope());
				
		// add the associated ref def map to the fi config for this procedure
		if (!procedure.getProcedureType().isCenterTelescope()) {
			RefBeamMap refDefMap = centroidMapMgmt.getRefBeamDefMap(procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId());
			procedure.setRefDefMap(refDefMap);
		}
		
		// get FindCentDefaults and create a procedure related copy
		FindCentConfigDefaults findCentConfigDefaults = globalConfigMgmt.findFindCentConfig( 
				procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId());
			procedure.getProcedureConfigSet().setFindCentConfig(new FindCentConfig(findCentConfigDefaults));


		// if this is frame from file, associate the frame now
		if (procedure.getProcedureConfigSet().getProcedureConfig().isFrameFromFile()) {

			try {
				frameSimulator.init(selectedFitsFiles);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// get the star info from the DCS interface
			try {
				StarInfo starInfo = dcsMgmt.queryStar();
				procedure.setStarName(starInfo.getStarName());
				procedure.setStarSpType(starInfo.getStarColor());
				procedure.setStarVmag(String.format("%.2f", starInfo.getStarMag()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		logger.debug("doExecuteProcedure::mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		// TODO: check if this is passive tilt before performing this validation
		if (procedureType.isPassiveTilt() && procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getWavelength() == 611.0) {
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back
		
		if (procedureType.isCreateRefMap()) {
			createRefMapExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedureType.isPassiveTilt()) {
			passiveTiltExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedureType.isCenterTelescope()) {
			centerTelescopeExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		}
		
		
		logger.debug("doExecuteProcedure::after executor call");

	}

	public String doViewProcedure() {

		procedure = procedureMgmt.findProcedure(procedure.getProcedureId());
				
		procedureType = procedure.getProcedureType();

		statusLogController.refreshProcedureStatusLog();

		// load up frames that were used
		for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
			String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

			CcdFrame loadedFitsFile = null;

			logger.debug("filename = " + filename);
			try {

				loadedFitsFile = frameMgmt.loadFitsFrame(filename);

			} catch (Exception e) {
				e.printStackTrace();
			}

			// if a png file for display exists, read it in. Otherwise create it.
			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
			procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);
			
		}
		
		// set up display of camera state values for first frame
		loadCameraState(procedure.getProcedureCcdFrameList().get(0).getCcdFrame().getCameraState());
		
		// set up visualization displays
		initVisualizationDisplays(procedureType.getProcedureTypeId());

		// in case the values are not in the DB, just dummy some values
		if (procedure.getProcedureConfigSet().getFiConfig() == null) {
			procedure.getProcedureConfigSet().setFiConfig(new FIConfig());
		}

		breadcrumbMenuBean.addItem("Procedure #" + procedure.getProcedureNumber() + ": "
				+ procedure.getProcedureType().getProcedureTypeName(), "/modules/procedure/procedurePerspective.xhtml");

		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}

	public void doAbortProcedure() {
		procedureExecutionState.setAbortRequested(true);
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
			setupCreateRefMapDefaults(sessionController.getInstrument().getInstrumentId(), 
					procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
					procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		}

	}
	
	public void doUpdateFilter() {
		
		if (procedure.getProcedureType().getProcedureTypeId().equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {
			
			// change int time and selected ref beam settings in procedure config
			setupCreateRefMapDefaults(sessionController.getInstrument().getInstrumentId(),
					procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
					procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		}
	}
		
	public void selectFrameLogListener(ProcedureCcdFrame procedureCcdFrame) throws Exception {
		this.procedureCcdFrame = procedureCcdFrame;
		
		List<FrameFieldDisplay> displayList =  sessionMgmt.findAllFrameFieldsToDisplay();
		
		for (FrameFieldDisplay fieldDisplay : displayList) {
			
			Object object;
			// TODO: make this code generic later
			if (fieldDisplay.getClassName().equals("org.tmt.aps.peas.refBeamMap.model.centroidMap")) {
				object = procedureCcdFrame.getCentroidMap();
			} else {
				object = procedureCcdFrame.getCcdFrame();
			}
			
			Class clazz = object.getClass();
			
			String fieldName = fieldDisplay.getFieldName();
			
			String methodPrefix = fieldDisplay.getFieldMetaData().getDataType() == Constant.DATA_TYPE_BOOLEAN ? "is" : "get";
			
			String methodName = methodPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Method method = clazz.getMethod(methodName , null);
			
			String value = "" + method.invoke(object, null);
			
			fieldDisplay.setValue(value);
			
		}
				
		procedureCcdFrame.setFrameFieldDisplayList(displayList);
	}
	
	public void loadCameraState(CameraState cameraState) {
		frameInstrument.updateState(cameraState);
	}
		
	public void frameSelectListener() {
		
		selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);
		loadCameraState(selectedFrame.getCcdFrame().getCameraState());
	}
	
	// ====================================================================================== //
	//   Visualization Displays                                                               //
	// ====================================================================================== //
	
	public void doUpdateDisplays() {

		// TODO - this needs to know which node was selected - for now, centroids display
		// TODO - for now, just the first iteration
		CentroidMap centroidMap = procedure.getProcedureCcdFrameList().get(0).getCentroidMap();
		List<FloatPoint> centroids = FloatPointListEncoder.decodeList(centroidMap.getCentroidMapData());
		encodeOrderedPointList(centroids);
		
	}
	
	private List<Subimage> getSubimageDefsForMask(Long maskTypeId) {
		
		PupilMaskType pupilMaskType = cameraDefMgmt.findPupilMaskType(maskTypeId);

		logger.debug("Number of Spots = " + pupilMaskType.getNumSpots());

		Map<String, Integer> spots = new LinkedHashMap<String, Integer>();
		for (int i = 1; i <= pupilMaskType.getNumSpots(); i++) {
			spots.put("spot  # " + i, i);
		}

		// TODO: read in subimageDefList based on pupilMaskType
		List<Subimage> subimageDefList = new ArrayList<Subimage>();
		if (pupilMaskType.getPupilMaskTypeId().equals(PupilMaskType.PUPIL_MASK_TYPE_ID_36)) {
			for (int i = 0; i < Subimage.PT_DEF_X_ARRAY.length; i++) {
				Subimage subimage = new Subimage(i + 1, Subimage.PT_DEF_X_ARRAY[i], Subimage.PT_DEF_Y_ARRAY[i]);
				subimageDefList.add(subimage);
			}
		}
		return subimageDefList;
	}
	
	private void encodeSubimageHiddenVars(List<Subimage> subimageList) {
				
		List<Float> xList = new ArrayList<Float>();
		List<Float> yList = new ArrayList<Float>();
		List<Integer> numList = new ArrayList<Integer>();
		for (Subimage subimage : subimageList) {
			
			numList.add(subimage.getSubimageNumber());
			xList.add(subimage.getxCcd());
			yList.add(subimage.getyCcd());			
		}
		
		setCentroidXs(FloatListEncoder.encodeList(xList));
		setCentroidYs(FloatListEncoder.encodeList(yList));
		setCentroidNbrs(IntegerListEncoder.encodeList(numList));
	}
	
	private void encodeOrderedPointList(List<FloatPoint> inputList) {
		
		List<Float> xList = new ArrayList<Float>();
		List<Float> yList = new ArrayList<Float>();
		List<Integer> numList = new ArrayList<Integer>();
		int i=1;
		for (FloatPoint coord : inputList) {
			
			numList.add(i++);
			xList.add(coord.x);
			yList.add(coord.y);			
		}
		
		setCentroidXs(FloatListEncoder.encodeList(xList));
		setCentroidYs(FloatListEncoder.encodeList(yList));
		setCentroidNbrs(IntegerListEncoder.encodeList(numList));
	}

	public void doHandMark() {
		
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");
		
		int x = 2 * (new Double(xStr)).intValue(); // 512 * 2 = 1024
		int y = 2 * (new Double(yStr)).intValue(); // 512 * 2 = 1024
		// add to the centroid hidden form vars
		
		String centroidXs = getFrameCentroidXs();
		String centroidYs = getFrameCentroidYs();
		centroidXs = (centroidXs == null) ? "" + x : centroidXs + "," + x;
		centroidYs = (centroidYs == null) ? "" + y : centroidYs + "," + y;
		setFrameCentroidXs(centroidXs);
		setFrameCentroidYs(centroidYs);
		
		// make marking available to executor
		frameDisplayMgmt.setMarking(FloatListEncoder.decodeList(centroidXs), FloatListEncoder.decodeList(centroidYs));
	}
	
	public void doApplyMarking() {
		// TODO: put this in the action for the apply marking on the frame
		frameDisplayMgmt.setPendingMarkAction(false);
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("instructionDialog.hide()");
	}
	
	public void doResetMarking() {
		setFrameCentroidXs(null);
		setFrameCentroidYs(null);
	}
		
}
