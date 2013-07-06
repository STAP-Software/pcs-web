package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.procedure.model.Procedure;

@Named
@SessionScoped
public class SessionController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	
	List<Procedure> sessionList;
	List<String> frameList;
	boolean inPassiveTilt;
	boolean procedureExecuting;

	@PostConstruct
	private void init() {
		
		sessionList = new ArrayList<Procedure>();
		
		// TODO: get the session list in the correct way
		
		
	}
	

	public List<Procedure> getSessionList() {
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