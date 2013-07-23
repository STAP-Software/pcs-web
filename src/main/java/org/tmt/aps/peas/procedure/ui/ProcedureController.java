package org.tmt.aps.peas.procedure.ui;

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
import org.tmt.aps.peas.config.ui.GlobalConfigController;
import org.tmt.aps.peas.passiveTilt.business.PassiveTiltMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.ui.SessionController;

@Named
@SessionScoped
public class ProcedureController implements Serializable {

	@EJB
	PeasProperties peasProperties;
	@EJB
	ProcedureMgmt procedureMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private SessionController sessionController;
	@Inject
	private GlobalConfigController globalConfigController;

	Procedure procedure;


	public Procedure getProcedure() {
		return procedure;
	}


	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}


	public String doViewProcedure() {

		breadcrumbMenuBean.addItem("Procedure #" + procedure.getProcedureNumber() + ": " + procedure.getProcedureType().getProcedureTypeName(), "newProcedure.xhtml");

		return "/modules/passiveTilt/passiveTilt.xhtml?faces-redirect=true";
	}


}
