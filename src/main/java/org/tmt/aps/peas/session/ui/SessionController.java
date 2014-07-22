/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Named
@SessionScoped
public class SessionController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	SessionMgmt sessionMgmt;
	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	
	Session currentSession;
	Session session;
	List<Session> sessionList;
	List<String> frameList;
	Long currentProcedureTypeId;
	boolean procedureExecuting;
	Telescope telescope;
	Instrument instrument;
	
	boolean advancedViewMode;
	String password;

	@PostConstruct
	private void init() {
		
		try {
		String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
		telescope = sessionMgmt.findTelescope(new Long(telescopeIdStr));
		
		String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
		instrument = sessionMgmt.findInstrument(new Long(instrumentIdStr));
			
		sessionList = sessionMgmt.findAllSessions();
		
		currentSession = sessionMgmt.findCurrentSession();
		
		if (currentSession == null) {
			currentSession = createNewSession();
		} else {
			// order procedures by procedure number
			Collections.sort(currentSession.getProcedureList(), new BeanComparator("procedureNumber"));
		}
		
		advancedViewMode = false;
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public List<Session> getSessionList() {
		return sessionList;
	}
	public void setSessionList(List<Session> sessionList) {
		this.sessionList = sessionList;
	}

	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}

	public Telescope getTelescope() {
		return telescope;
	}
	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public Instrument getInstrument() {
		return instrument;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public Long getCurrentProcedureTypeId() {
		return currentProcedureTypeId;
	}

	public void setCurrentProcedureTypeId(Long currentProcedureTypeId) {
		this.currentProcedureTypeId = currentProcedureTypeId;
	}


	public boolean isProcedureExecuting() {
		return procedureExecuting;
	}


	public void setProcedureExecuting(boolean procedureExecuting) {
		this.procedureExecuting = procedureExecuting;
	}
	
	public Session getCurrentSession() {
		return currentSession;
	}
	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}
	
	public boolean isAdvancedViewMode() {
		return advancedViewMode;
	}

	public void setAdvancedViewMode(boolean advancedViewMode) {
		this.advancedViewMode = advancedViewMode;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	private Session createNewSession() {
		// create a new session object
		try {
		Session session = new Session();
		List<Procedure> procedureList = new ArrayList<Procedure>();
		session.setProcedureList(procedureList);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date dateWithoutTime = sdf.parse(sdf.format(new Date()));
		session.setSessionDate(dateWithoutTime);
		
		// get telescope and instrument
		session.setInstrument(instrument);
		session.setTelescope(telescope);
		
		return session;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public String doViewCurrentSession() {
		
		session = currentSession;
		
		breadcrumbMenuBean.addFirstItem("Current Session", "sessionDetail.xhtml");
		return "/modules/session/sessionDetail.xhtml?faces-redirect=true";

	}
	public String doViewSession() {
		
		session = sessionMgmt.findSession(session.getSessionId());
		
		breadcrumbMenuBean.addFirstItem("Session: " + session.getTelescope().getTelescopeName() + " - (" + session.getSessionDateFormatted() + ")", "sessionDetail.xhtml");
		return "/modules/session/sessionDetail.xhtml?faces-redirect=true";

	}
	
	public String doSaveSession() {
		
		sessionMgmt.updateSession(session);
		
        FacesContext context = FacesContext.getCurrentInstance();          
        context.addMessage(null, new FacesMessage("Record Save Successful", "More text"));  
	
		breadcrumbMenuBean.addFirstItem("Session: " + session, "sessionDetail.xhtml");
		return "/modules/session/sessionDetail.xhtml";
		
	}

	public String doCancelSaveSession() {
		
		return doViewSessionList();
		
	}
	
	public String doViewSessionList() {
		
		sessionList = sessionMgmt.findAllSessions();
		
		breadcrumbMenuBean.addFirstItem("Sessions", "sessionList.xhtml");
		return "/modules/session/sessionList.xhtml?faces-redirect=true";

	}


	public void setupNewProcedure(Procedure procedure) {
		int procNum = sessionMgmt.getNextProcedureNumber(currentSession.getSessionId());
		procedure.setProcedureNumber(procNum);

		currentSession.getProcedureList().add(procedure);
		
	}
	
	public void modeChangeListener() {
		// here we check the mode and popup dialog at correct state change
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (advancedViewMode == true) {
			requestContext.execute("loginDialog.show()");
		}
	}
	
	public void login() {
		// here we check the password and change the mode accordingly
		if (!password.equals("ekinrez")) {
			advancedViewMode = false;
		}

	}
	
}