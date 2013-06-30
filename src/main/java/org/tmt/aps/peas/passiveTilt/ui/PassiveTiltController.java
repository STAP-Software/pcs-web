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

import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.Procedure;
import org.tmt.aps.peas.ProcedureWizardBean;
import org.tmt.aps.peas.SessionController;
import org.tmt.aps.peas.passiveTilt.business.PassiveTiltMgmt;
import org.tmt.aps.peas.passiveTilt.model.PassiveTiltDef;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;

@Named
@SessionScoped
public class PassiveTiltController implements Serializable {

	@EJB
	PassiveTiltMgmt passiveTiltMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private ProcedureWizardBean procedureWizardBean;
	@Inject
	private SessionController sessionController;

	PassiveTiltDef passiveTiltDef;

	List<Procedure> sessionList;
	List<String> frameList;

	@PostConstruct
	private void init() {

		// test only, in the future, the DB will return a list of procedures,
		// and the menus will be generated from those

		passiveTiltDef = new PassiveTiltDef(); // for advanced options access from template

		frameList = new ArrayList<String>();
		frameList.add("1");
		frameList.add("2");
		frameList.add("3");

	}

	public PassiveTiltDef getPassiveTiltDef() {
		return passiveTiltDef;
	}

	public void setPassiveTiltDef(PassiveTiltDef passiveTiltDef) {
		this.passiveTiltDef = passiveTiltDef;
	}

	public List<String> getFrameList() {
		return frameList;
	}

	public void setFrameList(List<String> frameList) {
		this.frameList = frameList;
	}



	public String doNewPassiveTilt() {

		passiveTiltDef = new PassiveTiltDef();
		procedureWizardBean.reset();

		sessionController.setInPassiveTilt(true);

		// perform setup for new...
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");

		breadcrumbMenuBean.addItem("Passive Tilt - " + sdf.format(date), "newProcedure.xhtml");

		return "/modules/passiveTilt/passiveTilt.xhtml?faces-redirect=true";
	}

	public String doCancelProcedure() {

		return "/modules/sessionList.xhtml?faces-redirect=true";
	}


	public void doExecuteProcedure(ActionEvent actionEvent) {

		// TODO: maybe this should be a bean that backs the menu bar
		sessionController.setProcedureExecuting(true);

		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		if (passiveTiltDef.getFilter() != 611) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		passiveTiltMgmt.executeProcedure(passiveTiltDef);

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
