package org.tmt.aps.peas.session.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Stateless
public class SessionMgmt {

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	PeasProperties peasProperties;

	public Session findSession(Long sessionId) {
		TypedQuery<Session> query = em.createNamedQuery("findSession", Session.class);
		query.setParameter("sessionId", sessionId);

		return query.getSingleResult();
	}

	public List<Session> findAllSessions() {
		TypedQuery<Session> query = em.createNamedQuery("findAllSessions", Session.class);

		return query.getResultList();
	}

	public Session findCurrentSession() {
		// get the session for this date
		try {
			try {
				// we use UTC as the timezone, hawaii time a session is within a UTC day
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date dateWithoutTime = sdf.parse(sdf.format(new Date()));

				TypedQuery<Session> query = em.createNamedQuery("findSessionByDate", Session.class);
				query.setParameter("sessionDate", dateWithoutTime);

				return query.getSingleResult();

			} catch (NoResultException nre) {
				nre.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public Session createSession(Session session) throws Exception {
		// store both session and procedure list
		// generate ids

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date dateWithoutTime = sdf.parse(sdf.format(new Date()));

			session.setSessionDate(dateWithoutTime);
			em.persist(session);

			return session;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	public Session updateSession(Session session) {
		em.merge(session);
		return session;
	}

	public Telescope findTelescope(long telescopeId) {
		TypedQuery<Telescope> query = em.createNamedQuery("findTelescope", Telescope.class);
		query.setParameter("telescopeId", telescopeId);

		return query.getSingleResult();
	}

	public Instrument findInstrument(long instrumentId) {
		TypedQuery<Instrument> query = em.createNamedQuery("findInstrument", Instrument.class);
		query.setParameter("instrumentId", instrumentId);

		return query.getSingleResult();
	}

	public int getNextProcedureNumber(Long sessionId) {
		// query the session for all the procedures, order by procedureNumber
		TypedQuery<Procedure> query = em.createNamedQuery("findLatestSessionProcedure", Procedure.class);
		query.setParameter("sessionId", sessionId);
		query.setMaxResults(1);
		
		List<Procedure> procedureList = query.getResultList();
		
		if (procedureList.isEmpty()) {
			return 1;
		} else {
			return procedureList.get(0).getProcedureNumber() + 1;
		}
		
	}

	public void updateCurrentSession(Session currentSession) {
		// update the session object (and all the procedures in the list)
		
		// if sessionId is null, we create instead
		if (currentSession.getSessionId() == null) {
			em.persist(currentSession);
			for (Procedure procedure : currentSession.getProcedureList()) {
				procedure.setSession(currentSession);
				em.persist(procedure);
			}
		} else {
			em.merge(currentSession);
			for (Procedure procedure : currentSession.getProcedureList()) {
				if (procedure.getSession() == null) {
					procedure.setSession(currentSession);
					em.persist(procedure);
				}
			}

		}
		
		
	}

}
