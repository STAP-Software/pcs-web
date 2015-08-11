/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.ExtInfConnectConfig;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

@Named
@SessionScoped
public class SessionController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	SessionMgmt sessionMgmt;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	ProcedureExecutionState procedureExecutionState;
	@EJB
	PeasProperties peasProperties;
	@EJB
	ExtInfConfigState extInfConfigState;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	Session currentSession;
	Session currentSessionPersisted; // the session that is completed and stored
	Session session;
	List<Session> sessionList;
	List<String> frameList;

	boolean procedureExecuting;
	Telescope telescope;
	Instrument instrument;

	boolean advancedViewMode;
	boolean extInfSimulationMode = true;
	
	String password;
	

	@PostConstruct
	private void init() {

		try {
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			telescope = telescopeMgmt.findTelescope(new Long(telescopeIdStr));

			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			instrument = sessionMgmt.findInstrument(new Long(instrumentIdStr));

			sessionList = sessionMgmt.findAllSessions(telescope.getTelescopeId());

			currentSession = sessionMgmt.findCurrentSession(telescope.getTelescopeId());
			// do we get our own copy??
			currentSessionPersisted = sessionMgmt.findCurrentSession(telescope.getTelescopeId());

			if (currentSession == null) {
				currentSession = sessionMgmt.createNewSession(instrument, telescope);
				currentSessionPersisted = (Session) BeanUtils.cloneBean(currentSession);
				// the cloneBean will copy the procedure list, we want our own copy
				currentSessionPersisted.setProcedureList(new ArrayList<Procedure>());
			}

			session = currentSession;

			advancedViewMode = false;
			

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
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

	public boolean isProcedureExecuting() {
		return procedureExecutionState.getExecutionStatus();
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

	public boolean isExtInfSimulationMode() {
		return extInfSimulationMode;
	}

	public void setExtInfSimulationMode(boolean extInfSimulationMode) {
		this.extInfSimulationMode = extInfSimulationMode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public ExtInfConnectConfig getExtInfConnectConfig() {
		return extInfConfigState.getExtInfConnectConfig();
	}

	public int procedureSortFunction(Object o1, Object o2) {
		Procedure p1 = (Procedure) o1;
		Procedure p2 = (Procedure) o2;
		return new ProcedureNumberComparator().compare(p1, p2);
	}

	public void updateCurrentSessionPersisted() {
		try {
			currentSessionPersisted = sessionMgmt.findSession(currentSession.getSessionId());
		} catch (Exception e) {
			// do nothing
		}

	}

	public String doViewCurrentSession() {

		try {
			session = sessionMgmt.findSession(currentSession.getSessionId());
		} catch (Exception e) {
			session = currentSessionPersisted;
		}

		// order procedures by procedure number
		Collections.sort(session.getProcedureList(), new ProcedureNumberComparator());

		breadcrumbMenuBean.addFirstItem("Current Session", "/modules/session/sessionDetail.xhtml");
		return "/modules/session/sessionDetail.xhtml?faces-redirect=true";

	}

	public String doViewSession() {

		try {
			session = sessionMgmt.findSession(session.getSessionId());
	
			// order procedures by procedure number
			Collections.sort(session.getProcedureList(), new ProcedureNumberComparator());
	
			breadcrumbMenuBean.addFirstItem(
					"Session: " + session.getTelescope().getTelescopeName() + " - (" + session.getSessionDateFormatted() + ")",
					"/modules/session/sessionDetail.xhtml");
			return "/modules/session/sessionDetail.xhtml?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	public String doSaveSession() {

		try {
			sessionMgmt.updateSession(session);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

		try {
			currentSessionPersisted = sessionMgmt.findSession(session.getSessionId());
	
			breadcrumbMenuBean.addFirstItem("Session: " + session, "/modules/session/sessionDetail.xhtml");
			return "/modules/session/sessionDetail.xhtml";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}


	}

	public String doCancelSaveSession() {

		return doViewSessionList();

	}

	public String doViewSessionList() {

		try {
			sessionList = sessionMgmt.findAllSessions(telescope.getTelescopeId());
	
			breadcrumbMenuBean.addFirstItem("Sessions", "/modules/session/sessionList.xhtml");
			return "/modules/session/sessionList.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	public void addNewProcedure(Procedure procedure) {
		currentSession.getProcedureList().add(procedure);
	}

	public Procedure getCurrentSessionLastProcedure() {
		List<Procedure> pList = currentSessionPersisted.getProcedureList();

		if (pList == null || pList.size() == 0) {
			return null;
		}
		
		Procedure lastProcedure = null;
		int maxProcNum = 0;
		for (Procedure procedure : pList) {
			String procNumStr = procedure.getProcedureNumber();
			if (procNumStr.indexOf(".") < 0 && (new Integer(procNumStr)) > maxProcNum) {
				lastProcedure = procedure;
				maxProcNum = new Integer(procNumStr);
			}
		}

		return lastProcedure;

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
		RequestContext requestContext = RequestContext.getCurrentInstance();


		if (!password.equals("ekinrez")) {
			advancedViewMode = false;
			requestContext.update("menuForm");
		}
		requestContext.update("procedureDetailForm");
	}

	public void extInfChangeListener() {
		// here we check the mode and popup dialog at correct state change
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (extInfSimulationMode == true) {
			
			// default all the ext interface checkboxes in the dialog only
//			requestContext.execute("setAllExtInfCheckboxes()");
			requestContext.execute("extInfDialog.show()");
			
		} else {
			// turn off all the ext interfaces
			getExtInfConnectConfig().reset();
			
			extInfSimulationMode = true;
			requestContext.update("extInfMode");
		}
	}

	public void doExtInfChange(boolean ok) {
		// here we check the password and change the mode accordingly
		RequestContext requestContext = RequestContext.getCurrentInstance();

		if (ok) {
			extInfSimulationMode = false;
			requestContext.update("menuForm");
		} else {
			extInfSimulationMode = true;
			// turn off all the ext interfaces
			getExtInfConnectConfig().reset();
		}
		
		requestContext.update("extInfMode");

	}

}