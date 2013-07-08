package org.tmt.aps.peas.passiveTilt.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.ProcedureWizardBean;
import org.tmt.aps.peas.passiveTilt.business.PassiveTiltMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.ui.SessionController;

@Named
@SessionScoped
public class PassiveTiltController implements Serializable {

	@EJB
	PassiveTiltMgmt passiveTiltMgmt;
	@EJB
	PeasProperties peasProperties;
	@EJB
	ProcedureMgmt procedureMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private ProcedureWizardBean procedureWizardBean;
	@Inject
	private SessionController sessionController;

	Procedure procedure;

	List<Procedure> procedureList;
	List<String> frameList;
	float integrationAddTime;

	@PostConstruct
	private void init() {

		// test only, in the future, the DB will return a list of procedures,
		// and the menus will be generated from those

		procedure = new Procedure(); // for advanced options access from template

		procedure.getProcedureConfig().setCalculationOptions(5);
		procedure.getProcedureConfig().setAutoCenterPupil(1);
		procedure.getProcedureConfig().setAutoCenterPupilMechanism(2);
		procedure.getProcedureConfig().setAutoCenterTelescope(1);
		procedure.getProcedureConfig().setAutoSendActuatorCmds(3);
		procedure.getProcedureConfig().setFrameScaleRotationRemoval(2);
		procedure.getProcedureConfig().setAutoTakeRefBeam(1);

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

	public String doNewPassiveTilt() {

		try {
			procedure = new Procedure();

			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			//ProcedureConfig procedureConfig = new ProcedureConfig();
			ProcedureConfig procedureConfig = procedureMgmt.findDefaultProcedureConfig(new Long(telescopeIdStr), new Long(instrumentIdStr),
					ProcedureType.PROCEDURE_TYPE_ID_PASSIVE_TILT);

			
			procedure.setProcedureConfig(procedureConfig);

			procedureWizardBean.reset();

			sessionController.setInPassiveTilt(true);

			// perform setup for new...

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Error Initializing Procedure, check log files for details"));
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");
		Date date = new Date();
		breadcrumbMenuBean.addItem("Passive Tilt - " + sdf.format(date), "newProcedure.xhtml");

		return "/modules/passiveTilt/passiveTilt.xhtml?faces-redirect=true";
	}

	public String doCancelProcedure() {

		return "/modules/procedureList.xhtml?faces-redirect=true";
	}

	public void doExecuteProcedure(ActionEvent actionEvent) {

		// TODO: maybe this should be a bean that backs the menu bar
		sessionController.setProcedureExecuting(true);

		System.out.println("doExecuteProcedure::");
		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		if (procedure.getProcedureConfig().getFilter() != 611) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		// DO NOT CALL WITHIN a try/catch - will not get called due to the fact that the Tx cannot be rolled back
		passiveTiltMgmt.executeProcedure(procedure);
		System.out.println("doExecuteProcedure::after to call passiveTiltMgmt");
		
	}

	public void doSaveAdvancedOptions() {

	}

	public void doCancelSaveAdvancedOptions() {

	}

	public void doSaveExecutionPreferences() {

	}

	public void doCancelSaveExecutionPreferences() {

	}

}
