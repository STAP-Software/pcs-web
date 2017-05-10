/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.faces.model.SelectItem;
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
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.business.IterationEntityCache;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.IterationListConfig;
import org.tmt.aps.peas.config.model.NbFilterSeqConfig;
import org.tmt.aps.peas.config.model.NbFilterSeqConfigDefaults;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfig;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfigDefaults;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.FrameSimulator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.executor.CenterTelescopeExecutor;
import org.tmt.aps.peas.procedure.executor.CoarsePhasingExecutor;
import org.tmt.aps.peas.procedure.executor.CreateRefMapExecutor;
import org.tmt.aps.peas.procedure.executor.FineScreenExecutor;
import org.tmt.aps.peas.procedure.executor.NarrowBandPhasingExecutor;
import org.tmt.aps.peas.procedure.executor.PassiveTiltExecutor;
import org.tmt.aps.peas.procedure.executor.PupilRegistrationExecutor;
import org.tmt.aps.peas.procedure.executor.SufsExecutor;
import org.tmt.aps.peas.procedure.model.CenterTelescopeProcedureOutput;
import org.tmt.aps.peas.procedure.model.CoarsePhasingProcedureOutput;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.FineScreenProcedureOutput;
import org.tmt.aps.peas.procedure.model.NarrowBandPhasingProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.model.PupilRegistrationProcedureOutput;
import org.tmt.aps.peas.procedure.model.SufsProcedureOutput;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.session.business.FieldMetaDataCache;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.model.UserPrompt;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;
import org.tmt.aps.peas.visualization.ui.VisualizationController;

/**
 * JSF Controller for procedure setup, execution, frame marking and viewing archived procedures
 * @author smichaels
 */
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
	FineScreenExecutor fineScreenExecutor;
	@EJB
	CoarsePhasingExecutor coarsePhasingExecutor;
	@EJB
	NarrowBandPhasingExecutor narrowBandPhasingExecutor;
	@EJB
	SufsExecutor sufsExecutor;
	@EJB
	PupilRegistrationExecutor pupilRegistrationExecutor;
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
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private IterationEntityCache iterationEntityCache;


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
	@Inject
	private AsyncController asyncController;

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
	boolean markedDisplayMode = false;

	boolean frameMarkingMode = false;
	String pixelValue;
	List<Procedure> procedureList;
	List<PupilMask> pupilMaskSelectList;
	List<ProcedureType> procedureTypeForSelectList;
	List<SelectItem> sufsGroupSelectList;
	boolean blankImage = false;
	
	ProcedureType registerPupilFor;
	
	@PostConstruct
	private void init() throws Exception {

		currentPrompt = new UserPrompt("Default Text", UserPrompt.PROMPT_TYPE_YES_NO, "My Default Text");

		// set up the instrument to be associated with each frame to display archived state
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		frameInstrument = cameraDefMgmt.findInstrument(instrumentId);
		
		pupilMaskSelectList = sessionController.getInstrument().getCamera().getPupilWheel().getOrigPupilMaskList();
		
		procedureTypeForSelectList = new ArrayList<ProcedureType>();
		procedureTypeForSelectList.add(procedureMgmt.findProcedureType(ProcedureType.PROCEDURE_TYPE_ID_FINE_SCREEN));
		procedureTypeForSelectList.add(procedureMgmt.findProcedureType(ProcedureType.PROCEDURE_TYPE_ID_COARSE_PHASING));
		procedureTypeForSelectList.add(procedureMgmt.findProcedureType(ProcedureType.PROCEDURE_TYPE_ID_NARROW_BAND_PHASING));
		
		sufsGroupSelectList = new ArrayList<SelectItem>();
		for (int sufsGroupNumber=0; sufsGroupNumber<6; sufsGroupNumber++) {
			String displayString = constantsCache.getSufsConstants().getSufsGroupToMirrorDisplayString(sufsGroupNumber);
			SelectItem selectItem = new SelectItem(sufsGroupNumber+1, " " + (sufsGroupNumber+1) + " : (" + displayString + ")");
			sufsGroupSelectList.add(selectItem);
		}

		
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
	
	public void setPixelValue(String pixelValue) {
		this.pixelValue = pixelValue;
	}

	public String getPixelValue() {
		return pixelValue;
	}

	public boolean isBlankImage() {
		return blankImage;
	}

	public void setBlankImage(boolean blankImage) {
		this.blankImage = blankImage;
	}

	public ProcedureType getRegisterPupilFor() {
		return registerPupilFor;
	}

	public void setRegisterPupilFor(ProcedureType registerPupilFor) {
		this.registerPupilFor = registerPupilFor;
	}

	// search radius is from findCentConfig
	public String getFrameSearchRadius() {
		try {
			int irad = procedure.getProcedureConfigSet().getFindCentConfigInterior().getIrad();
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
	
	public boolean isMarkedDisplayMode() {
		return markedDisplayMode;
	}

	public void setMarkedDisplayMode(boolean markedDisplayMode) {
		this.markedDisplayMode = markedDisplayMode;
	}

	public void setFrameSearchRadius(String searchRadius) {

	}
	
	public String getMirrorConfigString() {
		Integer[] mirrorList = procedure.getProcedureConfigSet().getGlobalConfig().getMirrorList();
		
		StringBuffer buf = new StringBuffer();
		int i=1;
		for (Integer mirrorStatus : mirrorList) {
			if (mirrorStatus.intValue() == 1) {
				buf.append(i +",");
			}
			i++;
		}
		buf.delete(buf.length()-1, buf.length());
		
		return buf.toString();
	}

	public void setMirrorConfigString(String mirrorConfigString) {
		
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

	// the mask list is supplied here where we know what the procedure is
	public List<PupilMask> getPupilMaskSelectList() {
		return pupilMaskSelectList;
	}
	
	// select list for pupil registration to know what procedure type we are registering the pupil for
	public List<ProcedureType> getProcedureTypeForSelectList() {
		return procedureTypeForSelectList;
	}
	
	public List<SelectItem> getSufsGroupSelectList() {
		return sufsGroupSelectList;
	}
	
	public List<IterationListConfig> getIterationListConfigOptions() {
		return iterationEntityCache.getOptionList(procedure.getProcedureType().getProcedureTypeId());
	}

	/**
	 * Supply correct php image as streamed content for frame display, using selectedFrameNumber
	 */
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

			// if the selected frame was overwritten during the procedure, we want to show a blank frame
			
			if (procedure.getProcedureCcdFrameCount() > selectedFrameNumber + 1) {
				// check to see if its the same FITS filename as the selected frame
				ProcedureCcdFrame nextFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber + 1);
				if (nextFrame.getCcdFrame().getFitsFilename().equals(selectedFrame.getCcdFrame().getFitsFilename())) {
					// get a blank picture
					Utils.waitFor(1000);
					return new DefaultStreamedContent(new ByteArrayInputStream(new byte[0]), "image/png");
				}
			}
			
			
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

	/**
	 * Handles upload of FITS files
	 * @param event
	 */
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

	/**
	 * JSF action method called when frames from file selection dialog 'Save' button is clicked.
	 * If the current procedure type is Phasing, will load the entire sequence of files even if only a subset are chosen
	 */
	public void doLoadFitsFile() {
		try {

			// if this is a phasing procedure, then the entire set of fits files should be in the selected list
			List<FitsFilename> newList = new ArrayList<FitsFilename>();
			
			if (selectedFitsFiles.isEmpty()) {
				return;
			}
			
			if (procedure.getProcedureType().isCoarsePhasing() || procedure.getProcedureType().isNarrowBandPhasing()) {
				FitsFilename selected = selectedFitsFiles.get(0);
				for (FitsFilename candidate : availableFitsFiles) {
					if (selected.isInSamePhasingSequence(candidate)) {
						newList.add(candidate);
					}
				}
				selectedFitsFiles = newList;
			}
			
			for (FitsFilename fitsFilename : selectedFitsFiles) {
				CcdFrame loadedFitsFile = frameMgmt.loadFitsFrame(fitsFilename.getFileName());
	
				// set the frame source stored with the file
				ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
				procedureConfig.setLightSource(loadedFitsFile.getFrameLightSource());
	
				 
				// set the mask type stored with the file
				if (procedure.getProcedureType().isCreateRefMap()) {
					procedureConfig.setPupilMaskType(loadedFitsFile.getHeaderPupilMaskType());
					
					// get the default mask, if it is installed on the wheel
					PupilMask defaultMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(procedureConfig.getPupilMaskType().getPupilMaskTypeId(),
							physicalModel.getInstrument().getCamera().getPupilWheel().getPupilWheelId());
			
					procedureConfig.setPupilMask(defaultMask);
					
					// when the pupil mask changes the fiConfig needs to be reloaded
					procedureExecutionMgmt.reloadFIConfig(procedure, physicalModel.getInstrument().getInstrumentId());

				}
				
				// if a png file for display exists, read it in. Otherwise create it.
				falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
				
	
			}
			
			// if we are SUFS, then we want to update the group number selected
			if (procedure.getProcedureType().isSufs()) {
				procedure.getProcedureConfigSet().getProcedureConfig().setSufsGroup(selectedFitsFiles.get(0).getSufsGroup());
				// load other values that depend on the sufsGroup
				sufsGroupChangeListener();
			}
			
			if (procedure.getProcedureType().isNarrowBandPhasing()) {
				// we need to take the list of selected files and create/select a filter set
				IterationListConfig iterationListConfig = iterationEntityCache.getOrCreateOptionForFitsList(selectedFitsFiles, procedure.getProcedureType().getProcedureTypeId());
				procedure.getProcedureConfigSet().setIterationListConfig(iterationListConfig);
				iterationListChangeListener();
			}
			
			
			FacesMessage msg = new FacesMessage("FITS Frame(s) uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

			
			procedure.getProcedureConfigSet().getProcedureConfig().setNumberOfTrials(selectedFitsFiles.size());
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Loading FITS file(s): " + e.getMessage()));

		}

	}

	/**
	 * @return true if the pupil mask selection list should be rendered
	 */
	public boolean getRenderPupilMaskSelect() {
		return procedure.getProcedureType().isCreateRefMap();
	}

	/**
	 * @return true if the pupil registration for which procedure type selection list should be rendered
	 */
	public boolean getRenderRegisterForSelect() {
		return procedure.getProcedureType().isPupilRegistration();
	}

	/**
	 * @return true if the number of trials selection list should be rendered
	 */
	public boolean getRenderNumTrials() {
		return procedure.getProcedureType().isFineScreen() || procedure.getProcedureType().isSufs();
	}

	/**
	 * @return true if the SUFS group selection list should be rendered
	 */
	public boolean getRenderSufsGroup() {
		return procedure.getProcedureType().isSufs() || (procedure.getProcedureType().isCreateRefMap() && procedure.getProcedureConfigSet().getProcedureConfig().getPupilMaskType().isPupilMaskTypeSufs());
	}

	/**
	 * @return true if frame marking instructions should be rendered
	 */
	public boolean getRenderFrameInstructions() {
		return frameDisplayMgmt.getFrameInstructions() != null;
	}

	/**
	 * @return true if the start button should be enabled
	 */
	public boolean isStartEnabled() {
		
		// if frame source is file, a file needs to be selected
		if (procedure.getProcedureConfigSet().getProcedureConfig().getFrameSource() == ProcedureConfig.FRAME_SOURCE_FILE) {
			if (selectedFitsFiles == null || selectedFitsFiles.size() == 0) {
				return false;
			}
		}
		// if a coarse phasing procedure, then the coarse phasing option needs to be set
		if (procedure.getProcedureType().isCoarsePhasing() && procedure.getProcedureConfigSet().getProcedureConfig().getCoarsePhasingOption() == 0) {
			return false;
		}
		
		if (procedure.getProcedureType().isPupilRegistration() && registerPupilFor == null) {
			return false;
		}
		
		// if a pupil registration procedure and a phasing mask is selected, then the coarse phasing option needs to be set
		if (procedure.getProcedureType().isPupilRegistration() && registerPupilFor.isCoarsePhasing()) {
			
			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			int option = procedureConfig.getCoarsePhasingOption();			
			
			if (option != 30 && option != 100 && option != 300 && option != 1000) {
			
				return false;
			}
		}

		
		return true;
	}

	public void frameSourceListener() {
		logger.debug("Frame Source Listener");
	}

	/**
	 * JSF Action method called when user selects Run...Passive Tilt menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewPassiveTilt() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT, new PassiveTiltProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run... Coarse Phasing menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewCoarsePhasing() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_COARSE_PHASING, new CoarsePhasingProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...Narrow Band Phasing menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewNarrowBandPhasing() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_NARROW_BAND_PHASING, new NarrowBandPhasingProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...Fine Screen menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewFineScreen() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_FINE_SCREEN, new FineScreenProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...SUFS menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewSufs() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_SUFS, new SufsProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...Pupil Registration menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewPupilRegistration() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_PUPIL_REGISTRATION, new PupilRegistrationProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...Center Telescope menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewCenterTelescope() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_CENTER_TELESCOPE, new CenterTelescopeProcedureOutput());
	}

	/**
	 * JSF Action method called when user selects Run...Create Ref Beam Map menu item.  
	 * Calls {@link #doNewProcedure(Long, ProcedureOutput)} which calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @return JSF page to render the procedure perspective
	 */
	public String doNewCreateRefBeam() {
		return doNewProcedure(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, new CreateRefBeamMapProcedureOutput());
	}


	/**
	 * JSF Action method called with user select Run Last Procedure menu item
	 * Selects the appropriate doNewCreate.. method based on last procedure run
	 */
	public String doNewLastProc() {
		ProcedureType lastProcedureType = sessionController.getCurrentSessionLastProcedure().getProcedureType();
		if (lastProcedureType.isCenterTelescope()) {
			return doNewCenterTelescope();
		} else if (lastProcedureType.isCreateRefMap()) {
			return doNewCreateRefBeam();
		} else if (lastProcedureType.isPassiveTilt()) {
			return doNewPassiveTilt();
		} else if (lastProcedureType.isPupilRegistration()) {
			return doNewPupilRegistration();
		} else if (lastProcedureType.isFineScreen()) {
			return doNewFineScreen();
		} else if (lastProcedureType.isCoarsePhasing()) {
			return doNewCoarsePhasing();
		} else if (lastProcedureType.isNarrowBandPhasing()) {
			return doNewNarrowBandPhasing();
		} else if (lastProcedureType.isSufs()) {
			return doNewSufs();
		} else {
			return null;
		}

	}

	/**
	 * Sets up a new procedure for user configuration and execution
	 * Calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)}
	 * @param procedureTypeId the procedure type
	 * @param procedureOutput a procedure output to assign
	 * @return the JSF page to render the procedure perspective
	 */
	public String doNewProcedure(Long procedureTypeId, ProcedureOutput procedureOutput) {

		try {

			Procedure lastProcedure = sessionController.getCurrentSessionLastProcedure();
			String testNumber = (lastProcedure != null) ? lastProcedure.getTestNumber() : "";
			boolean operational = (lastProcedure != null) ? lastProcedure.isOperational() : true;
			// reset the UI only variable
			// FIXME: should this be part of the procedure config and thus persisted?
			registerPupilFor = null;
			
			// check to see if a new session is required
			sessionController.checkCurrentSession();
			
			procedure = procedureExecutionMgmt.performProcedureSetup(procedureTypeId, sessionController.getCurrentSession(),
					testNumber, operational, procedureOutput);

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

	/**
	 * JSF Action method calls when the 'Start' button is pressed.
	 * Calls {@link ProcedureExecutionMgmt#performProcedureSetup(Long, org.tmt.aps.peas.session.model.Session, String, ProcedureOutput)} to perform procedure setup
	 * tasks prior to execution.  Then calls the non-blocking "executeProcedure()" method on the appropriate procedure executor class.  
	 */
	public void doExecuteProcedure() {

		logger.debug(" ###############################  doExecuteProcedure:: starting: mask = "
				+ procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());

		logger.debug("doExecuteProcedure::mask = " + procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask());
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		// check if this is passive tilt before performing this validation
		if (procedure.getProcedureType().isPassiveTilt()
				&& procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getWavelength() == 611.0) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!", ""));

		}
		// reset marking mode in case of hiccup in previous procedure
		frameMarkingMode = false;
		
		try {
		
			procedureExecutionMgmt.performProcedureStartup(procedure, selectedFitsFiles);
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Initializing Procedure, check log files for details", ""));
			
			return;
		}
		

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back

		if (procedure.getProcedureType().isCreateRefMap()) {
			createRefMapExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isPassiveTilt()) {
			passiveTiltExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isFineScreen()) {
			fineScreenExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isCoarsePhasing()) {
			coarsePhasingExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isNarrowBandPhasing()) {
			narrowBandPhasingExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isSufs()) {
			sufsExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isPupilRegistration()) {
			pupilRegistrationExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		} else if (procedure.getProcedureType().isCenterTelescope()) {
			centerTelescopeExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		}
		

		logger.debug("doExecuteProcedure::after executor call");

	}
	
	public void doTest() {
		
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Initializing Procedure, check log files for details", ""));
	}

	/**
	 * JSF Action method called when the page is first loaded.  This method participates in a pattern that allows the persistent session 
	 * to be resumed on a running procedure even if the original browser session has terminated.  All open dialogs should appear popped 
	 * up.
	 */
	public void doOnLoad() {

		// if we are running, we need to restore some things

		RequestContext requestContext = RequestContext.getCurrentInstance();
		
		if (procedureExecutionState.getExecutionStatus() == true) {

			// restart the poller
			requestContext.execute("procedureExecutionPoller.start();");

				
			requestContext.execute("markFrame()");

			
			VisualizationDisplay visualizationDisplay = visualizationController.getCurrentDisplay();
			
			if (visualizationDisplay != null) {

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
			}
			
			// user prompt
			if (currentPrompt != null) {
				
				requestContext.execute("userPromptDialog.show()");
				
			}
			
			// breadcrumb
			if (procedureExecutionState.isExecutionContextSubProcedure()) {
				// embedded subprocedure
				Procedure superProcedure = procedureExecutionState.getSuperProcedure();
				breadcrumbMenuBean.addFirstItem("Procedure #" + superProcedure.getProcedureNumber() + ": "
					+ superProcedure.getProcedureType().getProcedureTypeName(),
					"/modules/procedure/procedurePerspective.xhtml?faces-redirect=true");
				breadcrumbMenuBean.addItem(procedure.getProcedureType().getProcedureTypeName() + " - EMBEDDED SUBPROCEDURE RUNNING",
					"/modules/procedure/procedurePerspective.xhtml");
				
			} else {
				breadcrumbMenuBean.addFirstItem("Procedure #" + procedure.getProcedureNumber() + ": "
						+ procedure.getProcedureType().getProcedureTypeName(),
						"/modules/procedure/procedurePerspective.xhtml?faces-redirect=true");

			}

			
			requestContext.update("breadcrumbForm");
				
		}

	}

	/**
	 * JSF Action method called when a procedure view is desired.  If the procedure is an archived procedure, this method calls
	 * {@link #doViewArchivedProcedure()}
	 */
	public String doViewProcedure() {

		// we might be running....
		if (procedureExecutionState.getExecutionStatus() != true || !sessionController.isRunProcedurePermission()) {

			return doViewArchivedProcedure();

		}
		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to bring up an archived procedure for viewing
	 * Loads up the procedure, the procedure output, all FITS frames and visualization displays
	 * @return the JSF page to view a procedure
	 */
	public String doViewArchivedProcedure() {

		try {

			procedure = procedureMgmt.findProcedure(procedure.getProcedureId());

			statusLogController.refreshProcedureStatusLog();

			// load up frames that were used
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
				
				String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

				CcdFrame loadedFitsFile = null;

				logger.info("filename = " + filename);
				try {

					loadedFitsFile = frameMgmt.loadFitsFrame(filename);
					procedureCcdFrame.getCcdFrame().setRawFrame(loadedFitsFile.getRawFrame());
					
					// if a png file for display exists, read it in. Otherwise create it.
					falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
					procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

				} catch (Exception e) {
					logger.error(MessageGenerator.generateMessage("generic.error"), e);
				}


			}
			
			if (procedure.getProcedureCcdFrameList() != null && !procedure.getProcedureCcdFrameList().isEmpty()) {
			
				selectedFrameNumber = 0;
				selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);

				if (procedure.getProcedureCcdFrameCount() > selectedFrameNumber + 1) {
					ProcedureCcdFrame nextFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber + 1);
					blankImage = (nextFrame.getCcdFrame().getFitsFilename().equals(selectedFrame.getCcdFrame().getFitsFilename()));
				} else {
					blankImage = false;
				}

				// set up display of camera state values for first frame
				loadCameraState(procedure.getProcedureCcdFrameList().get(selectedFrameNumber).getCcdFrame().getCameraState());

			}
			
			// set up visualization displays
			visualizationController.initVisualizationDisplays(procedure.getProcedureType().getProcedureTypeId());
			visualizationController.setIteration(0); // to avoid index out of bounds exceptions
			
			// in case the values are not in the DB, just dummy some values
			if (procedure.getProcedureConfigSet().getFiConfig() == null) {
				procedure.getProcedureConfigSet().setFiConfig(new FIConfig());
			}

			// show frames marked at first
			doSetMarkedDisplayMode(true);
			
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

	/**
	 * JSF Action method called when 'Abort' button is pressed on the procedure perspective view or any dialog.
	 */
	public void doAbortProcedure() {
		procedureExecutionState.setAbortRequested(true);
	}

	/** 
	 * JSF Actio method to render the procedure reports
	 * @return the JSF page to render the procedure report view
	 */
	public String doShowProcedureLog() {

		breadcrumbMenuBean.removeTo("Procedure #");

		breadcrumbMenuBean.addItem("Procedure Log", "/modules/procedure/procedureLog.xhtml");

		return "/modules/procedure/procedureLog.xhtml?faces-redirect=true";

	}

	// Maybe in another controller, not sure yet

	/**
	 * JSF Action method called when the Save button in the Advanced Options dialog is clicked
	 */
	public void doSaveAdvancedOptions() {

	}

	/**
	 * JSF Action method called when the Advanced Options menu item is clicked
	 */
	public void doViewAdvancedOptions() {
		
	}

	public void doTest1() {
		System.out.println("test");
	}
	
	/**
	 * JSF Action method called when the Cancel button in the Advanced Options dialog is clicked
	 */
	public void doCancelSaveAdvancedOptions() {

	}

	/**
	 * JSF Action method called when the Save button in the Execution Preferences dialog is clicked
	 */
	public void doSaveExecutionPreferences() {

	}

	/**
	 * JSF Action method called when the Cancel button in the Execution Preferences dialog is clicked
	 */
	public void doCancelSaveExecutionPreferences() {

	}

	/**
	 * JSF Action method called when the procedure context panel Save button is pressed
	 */
	public void doSaveContext() {

		try {

			if (!procedure.isNewRecord()) {
				// only save if procedure has been saved. Prior to that, values will be persisted when the procedure is.
				procedureMgmt.updateProcedure(procedure);

				// for propagating value to the next procedure
				sessionController.updateCurrentSession();
			}

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	/**
	 * method to load a passed camera state into the view
	 * @param cameraState the camera state to view
	 */
	public void loadCameraState(CameraState cameraState) {
		frameInstrument.updateState(cameraState);
	}

	// ====================================================================================== //
	// Select Listeners                                                                       //
	// ====================================================================================== //

	/**
	 * JSF Listener method when a frame is selected from a list
	 * Loads the camera state associated with the frame
	 */
	public void frameSelectListener() {

		selectedFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber);
		loadCameraState(selectedFrame.getCcdFrame().getCameraState());
		
		if (procedure.getProcedureCcdFrameCount() > selectedFrameNumber + 1) {
			ProcedureCcdFrame nextFrame = procedure.getProcedureCcdFrameList().get(selectedFrameNumber + 1);
			blankImage = (nextFrame.getCcdFrame().getFitsFilename().equals(selectedFrame.getCcdFrame().getFitsFilename()));
		} else {
			blankImage = false;
		}
		
		// load centroid values if markedDisplayMode is true
		doSetMarkedDisplayMode(markedDisplayMode);
		
	}

	/**
	 * JSF Listener method called when the integration time has changed
	 * @param event
	 */
	public void intTimeChangeListener(AjaxBehaviorEvent event) {
		Float intTime = procedure.getProcedureConfigSet().getProcedureConfig().getIntegrationTime();
		logger.debug("int time = " + intTime);
	}

	/**
	 * JSF Listener method called when the pupil mask selection has changed
	 * Loads new reference map configuration if in a ref map procedure; reloads the find and identify configuration, reloads the pupil registration error configuration
	 * @throws Exception
	 */
	public void pupilMaskSelectListener() throws Exception {

		// pupil mask has changed, but we need to change the pupil mask type
		procedure.getProcedureConfigSet().getProcedureConfig().setPupilMaskType(procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType());
		
		// change int time and selected ref beam settings in procedure config
		procedureExecutionMgmt.setupCreateRefMapDefaults(procedure, sessionController.getInstrument().getInstrumentId(), procedure
				.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedure
				.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
						
		
		procedureExecutionMgmt.reloadFIConfig(procedure, sessionController.getInstrument().getInstrumentId());
		procedureExecutionMgmt.reloadPupilRegErrorConfig(procedure);
	}

	/**
	 * JSF Listener method called when the register pupil for selection has changed
	 * Reloads the find and identify configuration, reloads the pupil registration error configuration
	 * @throws Exception
	 */
	public void registerPupilForSelectListener() throws Exception {

		// get the correct pupil mask type for the procedure type
		if (registerPupilFor.isFineScreen()) {
			procedure.getProcedureConfigSet().getProcedureConfig().setPupilMaskType(physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_508));
		} else {
			procedure.getProcedureConfigSet().getProcedureConfig().setPupilMaskType(physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_160));			
		}
		
		// also change the pupil mask
		PupilMask pupilMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(procedure.getProcedureConfigSet().getProcedureConfig().getPupilMaskType().getPupilMaskTypeId(),
				physicalModel.getInstrument().getCamera().getPupilWheel().getPupilWheelId());

		procedure.getProcedureConfigSet().getProcedureConfig().setPupilMask(pupilMask);
		
		// integration time needs to change
		// if the mask is FS, use default PR int time, if mask is PH use PH int time	
		procedureExecutionMgmt.setupPupilRegIntTime(procedure);

		
		// set default filter for NPH
		if (registerPupilFor.isNarrowBandPhasing()) {
			FilterType filterType = physicalModel.getFilterTypeById(FilterType.FILTER_TYPE_ID_891);
			procedure.getProcedureConfigSet().getProcedureConfig().setFilterType(filterType);
			
			Filter filter = cameraDefMgmt.getFilterByFilterTypeAndWheel(FilterType.FILTER_TYPE_ID_891, 
					physicalModel.getInstrument().getCamera().getFilterWheel().getFilterWheelId());
			
			procedure.getProcedureConfigSet().getProcedureConfig().setFilter(filter);
		}
 		
		procedureExecutionMgmt.reloadFIConfig(procedure, sessionController.getInstrument().getInstrumentId());
		procedureExecutionMgmt.reloadPupilRegErrorConfig(procedure);
	}

	/**
	 * JSF Listener method called when the filter selection has changed
	 * Loads new reference map configuration if in a ref map procedure
	 * @throws Exception
	 */
	public void filterSelectListener() {

		// filter has changed, but we need to change the filter type
		procedure.getProcedureConfigSet().getProcedureConfig().setFilterType(procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType());

		
		if (procedure.getProcedureType().isCreateRefMap()) {

			// change int time and selected ref beam settings in procedure config
			procedureExecutionMgmt.setupCreateRefMapDefaults(procedure, sessionController.getInstrument().getInstrumentId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		}
	}	

	/**
	 * JSF Listener method called when the light source selection has changed
	 * Sets reference beam #1 as default, reloads find and identify configuration
	 * @throws Exception
	 */
	public void lightSourceSelectListener() throws Exception {
		
		// if light source is now LED, load up refBeam #1 as a default
		if (procedure.getProcedureConfigSet().getProcedureConfig().getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
			if (procedure.getProcedureConfigSet().getProcedureConfig().getReferenceBeam() == null) {
				ReferenceBeam refBeam1 = sessionController.getInstrument().getCamera().getOrderedReferenceBeamList().get(0);
				procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(refBeam1);
			}
		}
		
		procedureExecutionMgmt.reloadFIConfig(procedure, sessionController.getInstrument().getInstrumentId());

		// if we are setting up NPH, then we need to load the integration times for the LED
		// FIXME: generalize this
		if (procedure.getProcedureType().isNarrowBandPhasing()) {
			IterationListConfig iterationListConfig = procedure.getProcedureConfigSet().getIterationListConfig();
			iterationListConfig.updateDisplayLists(procedure.getProcedureConfigSet().getProcedureConfig().getLightSource());
		}
	}
		
	/**
	 * JSF Listener method called when the coarse phasing selection has changed
	 * Updates phasing step size and filter; reloads new create ref map config if this is a ref map procedure.
	 * @throws Exception
	 */
	public void coarsePhasingOptionSelectListener() throws Exception {

		// determine new filter
		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		int option = procedureConfig.getCoarsePhasingOption();
		
		Long defaultFilterTypeId = null;
		float phasingStepSize = 0.0f;
		
		
		switch (option) {
		case 30:
			defaultFilterTypeId = FilterType.FILTER_TYPE_ID_NONE;
			phasingStepSize = constantsCache.getPhasingConstants().getStepSize30();
			break;
		case 100:
			defaultFilterTypeId = FilterType.FILTER_TYPE_ID_870;
			phasingStepSize = constantsCache.getPhasingConstants().getStepSize100();
			break;
		case 300:
			defaultFilterTypeId = FilterType.FILTER_TYPE_ID_852;
			phasingStepSize = constantsCache.getPhasingConstants().getStepSize300();
			break;
		case 1000:
			defaultFilterTypeId = FilterType.FILTER_TYPE_ID_891;
			phasingStepSize = constantsCache.getPhasingConstants().getStepSize1000();
			break;
		}
		

		Filter defaultFilter = cameraDefMgmt.getFilterByFilterTypeAndWheel(defaultFilterTypeId,
				physicalModel.getInstrument().getCamera().getFilterWheel().getFilterWheelId());

		procedureConfig.setFilter(defaultFilter);
		procedureConfig.setPhasingStepSize(phasingStepSize);
		

		
		if (procedure.getProcedureType().isCreateRefMap()) {

			// change int time and selected ref beam settings in procedure config
			procedureExecutionMgmt.setupCreateRefMapDefaults(procedure, sessionController.getInstrument().getInstrumentId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedure
					.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		} 
		
	}
	
	
	/**
	 * JSF Listener method called when the SUFS group selection has changed
	 * Reloads SUFS coarse offsets config defaults; if a create ref map procedure, sets up the correct reference beam selection
	 * @throws Exception
	 */
	public void sufsGroupChangeListener() throws Exception {

		// determine new group
		int sufsGroup = procedure.getProcedureConfigSet().getProcedureConfig().getSufsGroup();
		
		// get SufsCoarseOffsetsConfigDefaults
		SufsCoarseOffsetsConfigDefaults sufsCoarseOffsetsConfigDefaults = globalConfigMgmt.findSufsCoarseOffsetsConfig(
				physicalModel.getInstrument().getInstrumentId(), new Long(sufsGroup));
		procedure.getProcedureConfigSet().setSufsCoarseOffsetsConfig(new SufsCoarseOffsetsConfig(sufsCoarseOffsetsConfigDefaults));
		
		// set the ref beam if a ref map procedure
		if (procedure.getProcedureType().isCreateRefMap()) {
		
			int refBeamNum = physicalModel.getSufsGroupByNumber(sufsGroup).getDefaultRefBeamNum();
			ReferenceBeam referenceBeam = globalConfigMgmt.findReferenceBeamByNumber(refBeamNum);
			procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(referenceBeam);
		
		}
		
	}

	public void iterationListChangeListener() throws Exception {
		
		// change the associated integration times when the option changes (in procedure controller)
		IterationListConfig iterationListConfig = procedure.getProcedureConfigSet().getIterationListConfig();
		iterationListConfig.updateDisplayLists(procedure.getProcedureConfigSet().getProcedureConfig().getLightSource());
		
		System.out.println(procedure.getProcedureConfigSet().getIterationListConfig().getIterationValueList().getDisplayString());
		
		
		// load up the search range for NB phasing
		try {
			NbFilterSeqConfigDefaults nbFilterSeqConfigDefaults = globalConfigMgmt.findNbFilterSeqConfig(iterationListConfig.getIterationListConfigId());

			// add this to the procedure config set
			procedure.getProcedureConfigSet().setNbFilterSeqConfig(new NbFilterSeqConfig(nbFilterSeqConfigDefaults));
		} catch (Exception e) {
			// add an empty one to the procedure config set
			procedure.getProcedureConfigSet().setNbFilterSeqConfig(new NbFilterSeqConfig());
		}
		
	}

	
	// ====================================================================================== //
	// Frame Displays //
	// ====================================================================================== //

	/**
	 * JSF Action method called when the frame is marked
	 * A centroid is calculated using the marked location, and the list of frame markings is updated
	 * This should become a subprocedure.
	 */
	public void doHandMark() {

		frameMarkingMode = true;

		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");

		int x = 2 * (new Double(xStr)).intValue(); // 512 * 2 = 1024
		int y = 2 * (new Double(yStr)).intValue(); // 512 * 2 = 1024
		// add to the centroid hidden form vars

		// call findCent on each centroid
		FloatPoint guess = new FloatPoint(x, y);
		// if findCent fails then we just use the user-marked guess as the centroid
		Subimage subimage = new Subimage(guess, 0.0f, 0.0f, 0);

		try {
			FindCentConfig findCentConfig = (FindCentConfig) BeanUtils.cloneBean(procedure.getProcedureConfigSet().getFindCentConfigInterior());
			// double the search radius for hand marking
			findCentConfig.setIrad(findCentConfig.getIrad() * 2);

			float[][] frame = procedure.getLatestProcedureCcdFrame().getCcdFrame().getCorrectedFrame();

			FindCentResult findCentResult = computationLibrary.findCent(frame, guess, findCentConfig, Constants.SPOT_TYPE_INTERIOR);
			subimage = findCentResult.getSubimage();
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

		FloatPoint centroid = subimage.getCentroid();
		
		String centroidXs = getFrameCentroidXs();
		String centroidYs = getFrameCentroidYs();
		centroidXs = (centroidXs == null || centroidXs.trim().length() == 0) ? "" + centroid.x : centroidXs + "," + centroid.x;
		centroidYs = (centroidYs == null || centroidYs.trim().length() == 0) ? "" + centroid.y : centroidYs + "," + centroid.y;
		setFrameCentroidXs(centroidXs);
		setFrameCentroidYs(centroidYs);

		// make marking available to executor
		frameDisplayMgmt.setMarking(FloatListEncoder.decodeList(centroidXs), FloatListEncoder.decodeList(centroidYs));
	}

	/**
	 * JSF Action method called when the frame Apply button is pressed
	 */
	public void doApplyMarking() {
		
		frameDisplayMgmt.setPendingMarkAction(false);

		frameMarkingMode = false;
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("instructionDialog.hide()");
	}

	/**
	 * JSF Action method called when the frame 'Cancel' button is pressed
	 */
	public void doAbortFromHandMarking() {
		frameDisplayMgmt.setPendingMarkAction(false);

		frameMarkingMode = false;
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("instructionDialog.hide()");
		procedureExecutionState.setAbortRequested(true);
	}
	
	/**
	 * JSF Action method called when the 'Reset' button is pressed.  All marking info is reset.
	 */
	public void doResetMarking() {
		setFrameCentroidXs(null);
		setFrameCentroidYs(null);
	}

	/**
	 * JSF Action method called when the 'Undo' button is pressed.  The last centroid in the marking list is removed.
	 */
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

	/**
	 * JSF Action method called from the frame menu for "Show Marking/Do Not Show Marking" menu items
	 * When setting is true, gets all the centroids from the selected procedureCcdFrame and sets up rendering of marking for all centroids associated with the frame, 
	 * then displays the frame.
	 * When setting is false, clears all marking and displays the frame
	 * @param setting true if Show Marking, false otherwise
	 */
	public void doSetMarkedDisplayMode(boolean setting) {
		
		markedDisplayMode = setting;
		
		try {
		
			if (setting) {
				// get the marking and set it
				
				FloatPoint[] centroids = selectedFrame.getCentroidMap().getFindCentroidsResult().getCentroidList();
				//FloatPoint[] centroids = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult().getCentroidList();
				
				float[] xArray = FloatPointListEncoder.extractXArray(Arrays.asList(centroids));
				float[] yArray = FloatPointListEncoder.extractYArray(Arrays.asList(centroids));
				
				String centroidXs = FloatListEncoder.encodeList(xArray);
				String centroidYs = FloatListEncoder.encodeList(yArray);
	
				setFrameCentroidXs(centroidXs);
				setFrameCentroidYs(centroidYs);
				
				// display the frame unmarked
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("frameHiddenForm");
				requestContext.execute("markFrame()");
	
				
			} else {
				// clear the marking 
				setFrameCentroidXs(null);
				setFrameCentroidYs(null);
				
				// display the frame unmarked
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("frameHiddenForm");
				requestContext.execute("drawFrame()");
	
	
			}
		} catch (Throwable e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			
			// clear the marking 
			setFrameCentroidXs(null);
			setFrameCentroidYs(null);
			
			// display the frame unmarked
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("frameHiddenForm");
			requestContext.execute("drawFrame()");

		}
	}

	
	/**
	 * Sets the managed variable 'pixelValue' to a value from the raw frame associated with the 
	 * current mouse position in the request at 'mouse_x', 'mouse_y'
	 */
	public void doGetFrameValue() {
		
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mouse_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mouse_y");
		
		int x = (new Double(xStr)).intValue(); 
		int y = (new Double(yStr)).intValue(); 

		// check for null so we don't get exceptions
		if (selectedFrame.getCcdFrame().getRawFrame() != null) {
			int size = selectedFrame.getCcdFrame().getRawFrame()[0].length;
			
			// place within bounds
			x = (x < 0) ? 0 : x;
			y = (y < 0) ? 0 : y;
			x = (x > size-1) ? size-1 : x;
			y = (y > size-1) ? size-1 : y;
			
			int value = selectedFrame.getCcdFrame().getRawFrame()[x][y];
			
			pixelValue = "" + value;
			
			
		}
		
	}

	public String findIterationString(int iteration) throws Exception {
		if (procedure.getProcedureType().isNarrowBandPhasing()) {
			String filterName = procedure.getProcedureConfigSet().getIterationListConfig().getIterationValueList().findEntityLabelValue(iteration, "Filter");
			
			return (iteration + 1) + ", Filter = " + filterName;
		} else {
			return (iteration + 1) + "";
		}
	}
	
}
