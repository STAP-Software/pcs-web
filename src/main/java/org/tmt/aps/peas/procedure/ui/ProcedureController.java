package org.tmt.aps.peas.procedure.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.ui.GlobalConfigController;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.ui.FalseColorProcessor;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.passiveTilt.business.PassiveTiltMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;

@Named
@SessionScoped
public class ProcedureController implements Serializable {

	@EJB
	PeasProperties peasProperties;
	@EJB
	ProcedureMgmt procedureMgmt;
	@EJB
	PassiveTiltMgmt passiveTiltMgmt;
	@EJB
	FrameMgmt frameMgmt;

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
	List<String> frameList;
	float integrationAddTime;
	UploadedFile uploadFitsFile;
	FitsFilename selectedFitsFile;
	byte[] falseColorPng;

	@PostConstruct
	private void init() {

		// test only, in the future, the DB will return a list of procedures,
		// and the menus will be generated from those

		frameList = new ArrayList<String>();
		frameList.add("1");
		frameList.add("2");
		frameList.add("3");

	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public List<String> getFrameList() {
		return frameList;
	}

	public void setFrameList(List<String> frameList) {
		this.frameList = frameList;
	}

	public float getIntegrationAddTime() {
		return integrationAddTime;
	}

	public void setIntegrationAddTime(float integrationAddTime) {
		this.integrationAddTime = integrationAddTime;
	}

	public FitsFilename getSelectedFitsFile() {
		return selectedFitsFile;
	}

	public void setSelectedFitsFile(FitsFilename selectedFitsFile) {
		this.selectedFitsFile = selectedFitsFile;
	}

	public UploadedFile getUploadFitsFile() {
		return uploadFitsFile;
	}

	public StreamedContent getGraphicImage() {
		if (falseColorPng == null) {
			return null;
		}
        return new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");   
	}

	public List<FitsFilename> getAvailableFitsFiles() {
		List<FitsFilename> fitsFileList = frameController.getProcedureFitsFiles("PT");
		System.out.println("FitsFileList size = " + fitsFileList.size());
		return fitsFileList;
	}

	public void handleFileUpload(FileUploadEvent event) {

		try {
			uploadFitsFile = event.getFile();

			CcdFrame fbs = frameMgmt.loadFitsFrame(uploadFitsFile.getInputstream());

			short frameArray[][] = fbs.getResult();

			FalseColorProcessor falseColorer = new FalseColorProcessor();
			falseColorPng = falseColorer.createImage(frameArray);

			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doLoadFitsFile() {
		try {
			CcdFrame fbs = frameMgmt.loadFitsFrame(selectedFitsFile.getFileName());

			short frameArray[][] = fbs.getResult();

			FalseColorProcessor falseColorer = new FalseColorProcessor();
			falseColorPng = falseColorer.createImage(frameArray);

 			FacesMessage msg = new FacesMessage("FITS Frame uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void frameSourceListener() {
		System.out.println("Frame Source Listener");
	}

	// TODO: this should be split into a generic doNewProcedure 
	public String doNewPassiveTilt() {

		try {
			procedure = new Procedure();

			// ProcedureConfig procedureConfig = new ProcedureConfig();
			ProcedureConfig procedureConfig = procedureMgmt.findDefaultProcedureConfig(sessionController.getTelescope().getTelescopeId(),
					sessionController.getInstrument().getInstrumentId(), ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT);

			procedure.setProcedureConfig(procedureConfig);

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

	public void doExecuteProcedure(ActionEvent actionEvent) {

		System.out.println("doExecuteProcedure:: starting");
		
		// TODO: maybe this should be a bean that backs the menu bar
		sessionController.setProcedureExecuting(true);

		// TODO: global config needs to be altered and saved if it has changed from nominal
		procedure.setGlobalConfig(globalConfigController.getGlobalConfig());

		procedure.setInstrument(sessionController.getInstrument());
		procedure.setTelescope(sessionController.getTelescope());

		System.out.println("doExecuteProcedure::");
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		// TODO: check if this is passive tilt before performing this validation
		if (procedure.getProcedureConfig().getFilter() != 611) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back
		passiveTiltMgmt.executeProcedure(procedure, sessionController.getCurrentSession());
		System.out.println("doExecuteProcedure::after to call passiveTiltMgmt");

	}



	public String doViewProcedure() {

		procedure = procedureMgmt.findProcedure(procedure.getProcedureId());
		
		statusLogController.refreshProcedureStatusLog();
		
		breadcrumbMenuBean.addItem("Procedure #" + procedure.getProcedureNumber() + ": " + procedure.getProcedureType().getProcedureTypeName(), "newProcedure.xhtml");

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
}
