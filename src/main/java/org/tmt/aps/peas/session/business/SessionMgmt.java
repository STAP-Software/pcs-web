/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.IterationEntityCache;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.business.ProcedureOutputMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.session.model.FieldMetaData;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Session EJB containing database methods to search, create and update night sessions
 * Manages procedure numbering
 * @author smichaels
 *
 */
@Stateless
public class SessionMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	PeasProperties peasProperties;
	@EJB
	ProcedureOutputMgmt procedureOutputMgmt;
	@EJB
	IterationEntityCache iterationEntityCache;

	/**
	 * Returns a night session given its id
	 * @param sessionId the night session id
	 * @return the night session
	 */
	public Session findSession(Long sessionId, boolean includeTestData) {
		
		TypedQuery<Session> query = null;
		if (includeTestData) {
		
			query = em.createNamedQuery("findSession", Session.class);
		
		} else {
			query = em.createNamedQuery("findSessionOperationalData", Session.class);
			
		}
		
		query.setParameter("sessionId", sessionId);

		Session session = null;
		
		try {
		
			session = query.getSingleResult();
		
		} catch (NoResultException e) {
			query = em.createNamedQuery("findSessionLight", Session.class);
			query.setParameter("sessionId", sessionId);
			session = query.getSingleResult();
			session.setProcedureList(new ArrayList<Procedure>());
		}
		
		for (Procedure procedure : session.getProcedureList()) {
			try {
				ProcedureOutput procedureOutput = procedureOutputMgmt.findProcedureOutput(procedure);
				procedure.setProcedureOutput(procedureOutput);
				
				// get the frame list shallow for the summary list
				TypedQuery<ProcedureCcdFrame> query2 = em.createNamedQuery("findProcedureCcdFramesShallow", ProcedureCcdFrame.class);
				query2.setParameter("procedureId", procedure.getProcedureId());
				
				List<ProcedureCcdFrame> procedureCcdFrameList = query2.getResultList();
				procedure.setProcedureCcdFrameList(procedureCcdFrameList);
				
				// also process the iteration list config to get the integration time list for the row expansion values
				if (procedure.getProcedureConfigSet().getIterationListConfig() != null) {
					iterationEntityCache.populateIterationValueList(procedure.getProcedureConfigSet().getIterationListConfig(), procedure.getProcedureType().getProcedureTypeId());
					int lightSource = procedure.getProcedureConfigSet().getProcedureConfig().getLightSource();
					procedure.getProcedureConfigSet().getIterationListConfig().updateIntegrationTimeList(lightSource);
				}
				
			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
			}
		}
		
		return session;
	}

	/**
	 * Searches for the most recent <code>searchQuantity</code> night sessions prior to <code>searchDate</code>
	 * @param telescopeId the telescope to search night sessions for
	 * @param searchDate the search date to search prior to
	 * @param searchQuantity the number of night sessions to retrieve
	 * @return a list of night sessions
	 */
	public List<Session> findLastSessions(Long telescopeId, Date searchDate, int searchQuantity) {
		TypedQuery<Session> query = em.createNamedQuery("findLastSessions", Session.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("searchDate", searchDate);
		query.setMaxResults(searchQuantity);
		
		return query.getResultList();
	}

	/**
	 * Returns the current night session for this telescope
	 * @return the current night session or null if none found
	 */
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
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}
	
	/**
	 * Creates a new night session for the current night
	 * @param instrument PCS1 or PCS2
	 * @param telescope Keck1 or Keck2
	 * @return the new night session
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Session createNewSession(Instrument instrument, Telescope telescope) throws Exception {
		// create a new session object
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

	}

	/**
	 * Updates the passed session.  If it is a new record, then create a new session.
	 * @param session the session entity object to be created or updated.
	 * @return the persisted session entity
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Session updateSession(Session session) {
		logger.info(MessageGenerator.generateMessage("record.update", "session"));
		
		if (session.isNewRecord()) {
			em.persist(session);
		} else {
			em.merge(session);
		}
		
		return session;
	}

	/**
	 * Returns the instrument given its id.  This method appears to be a duplicate of {@link org.tmt.aps.peas.instrument.business.CameraDefMgmt#findInstrument(Long)}
	 * @param instrumentId PCS1 or PCS2
	 * @return the instrument definitionn data structure
	 */
	public Instrument findInstrument(long instrumentId) {
		TypedQuery<Instrument> query = em.createNamedQuery("findInstrument", Instrument.class);
		query.setParameter("instrumentId", instrumentId);

		return query.getSingleResult();
	}

	/**
	 * Generates the next procedure number.  Handles sub-procedure numbering.
	 * @param sessionId the session id of the current session
	 * @param superProcedureNum the procedure number of the super procedure if this is a sub-procedure, null if it is a top level procedure
	 * @return the next procedure number
	 */
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

	/**
	 * Updates the current session in the database
	 * @param currentSession the session to update
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateCurrentSession(Session currentSession) {
		
		logger.info(MessageGenerator.generateMessage("record.update", "currentSession"));
		em.merge(currentSession);

	}
	
	/**
	 * Queries the database for all field metadata
	 * @return all metadata fields in the database
	 */
	public List<FieldMetaData> findAllFieldMetaData() {
		TypedQuery<FieldMetaData> query = em.createNamedQuery("findAll", FieldMetaData.class);
		
		return query.getResultList();
	}

	/**
	 * Queries the database for all frame related fields that will be displayed in reports
	 * @return the list of all fields in the FrameFieldDisplay table
	 */
	public List<FrameFieldDisplay> findAllFrameFieldsToDisplay() {
		TypedQuery<FrameFieldDisplay> query = em.createNamedQuery("findAllFields", FrameFieldDisplay.class);
		
		return query.getResultList();
	}


}
