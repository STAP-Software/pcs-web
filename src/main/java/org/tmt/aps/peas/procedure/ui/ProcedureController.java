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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.model.Subimage;
import org.tmt.aps.peas.config.ui.GlobalConfigController;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.FrameSimulator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.executor.PassiveTiltExecutor;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
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
	FrameMgmt frameMgmt;
	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	FrameSimulator frameSimulator;
	@EJB
	ProcedureExecutionState procedureExecutionState;

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

	Procedure procedure;

	List<Procedure> procedureList;
	float integrationAddTime;
	UploadedFile uploadFitsFile;
	List<FitsFilename> selectedFitsFiles;
	byte[] falseColorPng;
	private TreeNode visualizationDisplayRoot;
	
	String centroidXs;
	String centroidYs;
	String centroidNbrs;

	@PostConstruct
	private void init() {

		visualizationDisplayRoot = new DefaultTreeNode("Root", null);
		
		TreeNode node0 = new DefaultTreeNode("folder", "Iteration 1", visualizationDisplayRoot);
		TreeNode node1 = new DefaultTreeNode("folder", "Iteration 2", visualizationDisplayRoot);
		
		TreeNode node00 = new DefaultTreeNode("link", new VisualizationDisplayLink("Centroids", "runDrawSpots();centroidsDisplayDialog.show()"), node0);
		TreeNode node01 = new DefaultTreeNode("link", new VisualizationDisplayLink("Centroid Offsets", "runDrawOffsets(); centroidOffsetDisplayDialog.show()"), node0);
		TreeNode node02 = new DefaultTreeNode("link", new VisualizationDisplayLink("Avg. Centroid Offsets", "centroidOffsetDisplayDialog.show()"), node0);
		TreeNode node03 = new DefaultTreeNode("link", new VisualizationDisplayLink("Actuator Deltas", "centroidOffsetDisplayDialog.show()"), node0);

		TreeNode node10 = new DefaultTreeNode("link", new VisualizationDisplayLink("Centroids", "centroidsDisplayDialog.show()"), node1);
		TreeNode node11 = new DefaultTreeNode("link", new VisualizationDisplayLink("Centroid Offsets", "centroidOffsetDisplayDialog.show()"), node1);
		TreeNode node12 = new DefaultTreeNode("link", new VisualizationDisplayLink("Avg. Centroid Offsets", "centroidOffsetDisplayDialog.show()"), node1);
		TreeNode node13 = new DefaultTreeNode("link", new VisualizationDisplayLink("Actuator Deltas", "centroidOffsetDisplayDialog.show()"), node1);

		node0.setExpanded(true);
		node1.setExpanded(true);
		
		
		//updateCentroidOffsetsDisplay();
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

	public String getCentroidXs() {
		return centroidXs;
	}

	public void setCentroidXs(String centroidXs) {
		this.centroidXs = centroidXs;
	}

	public String getCentroidYs() {
		return centroidYs;
	}

	public void setCentroidYs(String centroidYs) {
		this.centroidYs = centroidYs;
	}

	public String getCentroidNbrs() {
		return centroidNbrs;
	}

	public void setCentroidNbrs(String centroidNbrs) {
		this.centroidNbrs = centroidNbrs;
	}

	public StreamedContent getGraphicImage() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
			return new DefaultStreamedContent();
		} else {
			// So, browser is requesting the image. Get ID value from actual request param.
			String indexStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("frameIndex");

			// index is passed when the procedure has completed execution and we need to know which one
			// if index == null, then get the current procedure frame

			ProcedureCcdFrame pcf = null;

			logger.debug("indexStr = " + indexStr);

			if (indexStr == null) {
				pcf = procedure.getLatestProcedureCcdFrame();
			} else {
				pcf = procedure.getProcedureCcdFrameList().get(new Integer(indexStr));
			}

			byte[] falseColorPng = pcf.getCcdFrame().getFalseColorPng();

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
		List<FitsFilename> fitsFileList = frameController.getProcedureFitsFiles("PT");
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

			// if a png file for display exists, read it in. Otherwise create it.
			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);

			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void frameSourceListener() {
		logger.debug("Frame Source Listener");
	}

	// TODO: this should be split into a generic doNewProcedure
	public String doNewPassiveTilt() {

		try {
			procedure = new Procedure();

			// ProcedureConfig procedureConfig = new ProcedureConfig();
			ProcedureConfig procedureConfig = procedureMgmt.findDefaultProcedureConfig(sessionController.getTelescope().getTelescopeId(),
					sessionController.getInstrument().getInstrumentId(), ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT);

			procedure.setProcedureConfig(procedureConfig);

			procedure.setProcedureState(Procedure.PROCEDURE_STATE_NEW);

			// add it to the session and give it a procedure number
			sessionController.setupNewProcedure(procedure);

			// TODO: this should come from a MetaData component
			ProcedureType procedureType = new ProcedureType();
			procedureType.setProcedureTypeId(new Long(1));
			procedureType.setProcedureTypeName("Passive Tilt");

			procedure.setProcedureType(procedureType);
			sessionController.setInPassiveTilt(true);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Initializing Procedure, check log files for details"));
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		breadcrumbMenuBean.addFirstItem("Passive Tilt - " + sdf.format(date), "newProcedure.xhtml");

		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}

	public String doCancelProcedure() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	//public void doExecuteProcedure(ActionEvent actionEvent) {
	public void doExecuteProcedure() {

		logger.debug(" ###############################  doExecuteProcedure:: starting");

		// TODO: maybe this should be a bean that backs the menu bar
		sessionController.setProcedureExecuting(true);

		// TODO: global config needs to be altered and saved if it has changed from nominal
		procedure.setGlobalConfig(globalConfigController.getGlobalConfig());

		procedure.setInstrument(sessionController.getInstrument());
		procedure.setTelescope(sessionController.getTelescope());

		procedureExecutionState.init(procedure);

		// if this is frame from file, associate the frame now
		if (procedure.getProcedureConfig().isFrameFromFile()) {

			try {
				frameSimulator.init(selectedFitsFiles);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		logger.debug("doExecuteProcedure::");
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		// TODO: check if this is passive tilt before performing this validation
		if (procedure.getProcedureConfig().getFilter() != 611) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back
		passiveTiltExecutor.executeProcedure(procedure, sessionController.getCurrentSession());
		logger.debug("doExecuteProcedure::after to call passiveTiltMgmt");

	}

	public String doViewProcedure() {

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
				e.printStackTrace();
			}

			// if a png file for display exists, read it in. Otherwise create it.
			falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
			procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

		}

		breadcrumbMenuBean.addItem("Procedure #" + procedure.getProcedureNumber() + ": "
				+ procedure.getProcedureType().getProcedureTypeName(), "newProcedure.xhtml");

		return "/modules/procedure/procedurePerspective.xhtml?faces-redirect=true";
	}

	// Maybe in another controller, not sure yet

	public void doSaveAdvancedOptions() {

	}

	public void doCancelSaveAdvancedOptions() {

	}

	public void doSaveExecutionPreferences() {

	}

	public void doCancelSaveExecutionPreferences() {

	}
	
	// ====================================================================================== //
	//   Visualization Displays                                                               //
	// ================================================================================	alert(xArray.length);====== //
	
	public void doUpdateDisplays() {

		// TODO - this needs to know which node was selected
		
		List<Subimage> subimageDefList = getSubimageDefsForMask(PupilMaskType.PUPIL_MASK_TYPE_ID_36);

		encodeSubimageHiddenVars(subimageDefList);
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
		
		// generate centroid numbers, x and y positions
		StringBuffer numBuf = new StringBuffer();
		StringBuffer xBuf = new StringBuffer();
		StringBuffer yBuf = new StringBuffer();
		for (Subimage subimage : subimageList) {
			numBuf.append(subimage.getSubimageNumber() + ",");
			xBuf.append(subimage.getxCcd() + ",");
			yBuf.append(subimage.getyCcd() + ",");
		}
		numBuf.deleteCharAt(numBuf.length() - 1);
		xBuf.deleteCharAt(xBuf.length() - 1);
		yBuf.deleteCharAt(yBuf.length() - 1);
		centroidXs = xBuf.toString();
		centroidYs = yBuf.toString();
		centroidNbrs = numBuf.toString();

	}

	
}
