package org.tmt.aps.peas.session.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.camera.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Named
@SessionScoped
public class SessionController implements Serializable {

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
	boolean inPassiveTilt;
	boolean procedureExecuting;
	Telescope telescope;
	Instrument instrument;

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
		}
		
		
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
	
	public Session getCurrentSession() {
		return currentSession;
	}
	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
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
		
		breadcrumbMenuBean.addFirstItem("Current Session", "procedureList.xhtml");
		return "/modules/session/procedureList.xhtml?faces-redirect=true";

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
	
	
}