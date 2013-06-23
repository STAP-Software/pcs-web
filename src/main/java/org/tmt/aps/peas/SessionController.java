package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
		sessionList.add(new Procedure(1l, "Reference Beam", 1, new Date()));
		sessionList.add(new Procedure(1l, "Passive Tilt", 2, new Date()));
		sessionList.add(new Procedure(1l, "Fine Screen", 3, new Date()));
		
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