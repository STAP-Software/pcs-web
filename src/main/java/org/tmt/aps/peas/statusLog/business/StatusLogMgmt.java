/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;
import org.tmt.aps.peas.statusLog.model.StatusLogEntry;


@Stateless
public class StatusLogMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	public ProcedureStatusLog getProcedureStatusLog(Long procedureId) {
		
		TypedQuery<StatusLogEntry> query = em.createNamedQuery("findEntriesByProcedureId", StatusLogEntry.class);
		query.setParameter("procedureId", procedureId);

		List<StatusLogEntry> statusList = query.getResultList();
		
		ProcedureStatusLog statusLog = new ProcedureStatusLog();
		statusLog.setLogEntryList(statusList);
		return statusLog;
	}
	
	public void saveStatusLog(ProcedureStatusLog statusLog, Long procedureId) {
		for (StatusLogEntry logEntry : statusLog.getLogEntryList()) {
			logEntry.setProcedureId(procedureId);
			em.persist(logEntry);
		}
		
	}
	
}
