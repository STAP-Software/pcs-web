package org.tmt.aps.peas.session.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;

@Named
@SessionScoped
public class SessionController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	
	List<Procedure> procedureList;
	List<Session> sessionList;
	List<String> frameList;
	boolean inPassiveTilt;
	boolean procedureExecuting;

	@PostConstruct
	private void init() {
		
		procedureList = new ArrayList<Procedure>();
		sessionList = new ArrayList<Session>();
		
		// TODO: get the session list in the correct way
		
		
	}
	

	public List<Procedure> getProcedureList() {
		return procedureList;
	}


	public List<Session> getSessionList() {
		return sessionList;
	}


	public boolean isInPassiveTilt() {
		return inPassiveTilt;
	}

	public void setInPassiveTilt(boolean inPassiveTilt) {
		this.inPassiveTilt = inPassiveTilt;
	}


	public boolean isProcedureExecuting() {
		return procedureExecuting;
	}


	public void setProcedureExecuting(boolean procedureExecuting) {
		this.procedureExecuting = procedureExecuting;
	}
	
}