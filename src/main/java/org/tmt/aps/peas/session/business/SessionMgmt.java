/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.FieldMetaData;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Stateless
public class SessionMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	PeasProperties peasProperties;

	public Session findSession(Long sessionId) {
		TypedQuery<Session> query = em.createNamedQuery("findSession", Session.class);
		query.setParameter("sessionId", sessionId);

		return query.getSingleResult();
	}

	public List<Session> findAllSessions(Long telescopeId) {
		TypedQuery<Session> query = em.createNamedQuery("findAllSessions", Session.class);
		query.setParameter("telescopeId", telescopeId);
		
		return query.getResultList();
	}

	public Session findCurrentSession(Long telescopeId) {
		// get the session for this date
		try {
			try {
				// we use UTC as the timezone, hawaii time a session is within a UTC day
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				Date dateWithoutTime = sdf.parse(sdf.format(new Date()));

				TypedQuery<Session> query = em.createNamedQuery("findSessionByDate", Session.class);
				query.setParameter("sessionDate", dateWithoutTime);
				query.setParameter("telescopeId", telescopeId);

				return query.getSingleResult();

			} catch (NoResultException nre) {
				logger.info("no current session found");
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

	public Instrument findInstrument(long instrumentId) {
		TypedQuery<Instrument> query = em.createNamedQuery("findInstrument", Instrument.class);
		query.setParameter("instrumentId", instrumentId);

		return query.getSingleResult();
	}

	public String getNextProcedureNumber(Long sessionId, String superProcedureNum) {
		// query the session for all the procedures, order by procedureNumber
		
		try {
			TypedQuery<Procedure> query = em.createNamedQuery("findLatestSessionProcedure", Procedure.class);
			query.setParameter("sessionId", sessionId);
			query.setMaxResults(1);
			
			Procedure latestProcedure = query.getSingleResult();
			
			String latestProcedureNum = latestProcedure.getProcedureNumber();
			boolean isLatestProcedureSubProcedure = latestProcedureNum.indexOf(".") > -1;
			
			if (superProcedureNum == null) {
				// increment major number
				String latestMajorNum = isLatestProcedureSubProcedure ? latestProcedureNum.substring(0, latestProcedureNum.indexOf(".")) : latestProcedureNum;
				int newMajorNum = new Integer(latestMajorNum) + 1;
				return "" + newMajorNum;
			} else {
				if (isLatestProcedureSubProcedure) {
					// extract minor number
					String oldMinorNum = latestProcedureNum.substring(latestProcedureNum.indexOf(".") + 1);
					return superProcedureNum + "." + (new Integer(oldMinorNum) + 1);
				} else {
					return superProcedureNum + ".1";
				}
					
			}
			
		} catch (NoResultException e) {
			return (superProcedureNum == null) ? "1" : "1.1";
		}

		
	}

	public void updateCurrentSession(Session currentSession) {
		// update the session object (and all the procedures in the list)
		
		// if sessionId is null, we create instead
		if (currentSession.getSessionId() == null) {
			em.persist(currentSession);
		} 
		
		for (Procedure procedure : currentSession.getProcedureList()) {
			if (procedure.getSession() == null) {
				if (procedure.getProcedureState() != Procedure.PROCEDURE_STATE_NEW && procedure.isNewRecord()) {
					// if it executed and is not in the DB, then save it
					procedure.setSession(currentSession);
					em.persist(procedure);
				}
			}
		}
		
		em.merge(currentSession);

	}
	
	public List<FieldMetaData> findAllFieldMetaData() {
		TypedQuery<FieldMetaData> query = em.createNamedQuery("findAll", FieldMetaData.class);
		
		return query.getResultList();
	}

	public List<FrameFieldDisplay> findAllFrameFieldsToDisplay() {
		TypedQuery<FrameFieldDisplay> query = em.createNamedQuery("findAllFields", FrameFieldDisplay.class);
		
		return query.getResultList();
	}

}
