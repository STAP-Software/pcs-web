/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;

@Named
@SessionScoped
public class StatusLogController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	StatusLogMgmt statusLogMgmt;
	@EJB
	StatusLogger statusLogger;
	@Inject
	ProcedureController procedureController;
	
	private ProcedureStatusLog procedureStatusLog = new ProcedureStatusLog();
	
	public ProcedureStatusLog getProcedureStatusLog() {
		if (procedureStatusLog == null || procedureStatusLog.getLogEntryList() == null) {
			logger.debug("getProcedureStatusLog:: " + procedureStatusLog);
		} else {
			logger.debug("getProcedureStatusLog:: " + procedureStatusLog.getLogEntryList().size());		
		}
		return procedureStatusLog;
	}
	
	// call load most recent procedure status log
	public void refreshCurrentProcedureStatusLog() {
		logger.debug("refreshing");
		this.procedureStatusLog = statusLogger.getProcedureStatusLog();
	}
	
	// call to load legacy procedure status log
	public void refreshProcedureStatusLog() {
		this.procedureStatusLog = statusLogMgmt.getProcedureStatusLog(procedureController.getProcedure().getProcedureId());
	}
	

	
}
