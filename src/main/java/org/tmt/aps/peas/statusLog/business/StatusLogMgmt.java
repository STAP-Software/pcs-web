/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.business;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;
import org.tmt.aps.peas.statusLog.model.StatusLogEntry;

/**
 * Session EJB managing database reads/writes of the procedure status log.
 * @author smichaels
 *
 */
@Stateless
public class StatusLogMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Returns a procedure status log given its associated procedure id
	 * @param procedureId the associated procedure id
	 * @return the procedure status log
	 */
	public ProcedureStatusLog getProcedureStatusLog(Long procedureId) {
		
		TypedQuery<StatusLogEntry> query = em.createNamedQuery("findEntriesByProcedureId", StatusLogEntry.class);
		query.setParameter("procedureId", procedureId);

		List<StatusLogEntry> statusList = query.getResultList();
		
		ProcedureStatusLog statusLog = new ProcedureStatusLog();
		statusLog.setLogEntryList(statusList);
		return statusLog;
	}
	
	/**
	 * Saves the status log in the database by storing all {@link StatusLogEntry} entities in the log to the database
	 * @param statusLog the status log to store
	 * @param procedureId the id of the procedure to associate it with
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveStatusLog(ProcedureStatusLog statusLog, Long procedureId) {
		for (StatusLogEntry logEntry : statusLog.getLogEntryList()) {
			logEntry.setProcedureId(procedureId);
			logger.info(MessageGenerator.generateMessage("record.create", "logEntry"));
			em.persist(logEntry);
		}
		
	}
	
}
