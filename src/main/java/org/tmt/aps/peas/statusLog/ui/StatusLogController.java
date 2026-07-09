/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.ui;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;

/**
 * JSF Controller class for the StatusLog related functionality in the PEAS user interface.  Exposes a procedureStatusLog to the user interface.
 * @author smichaels
 */
@Named("statusLogController")
@SessionScoped
public class StatusLogController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4267451454652435265L;

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
			//logger.debug("getProcedureStatusLog:: " + procedureStatusLog);
		} else {
			//logger.debug("getProcedureStatusLog:: " + procedureStatusLog.getLogEntryList().size());		
		}
		return procedureStatusLog;
	}
	
	/**
	 * load the most recent procedure status log to expose to the user interface
	 */
	public void refreshCurrentProcedureStatusLog() {
		logger.debug("refreshing");
		this.procedureStatusLog = statusLogger.getProcedureStatusLog();
	}
	
	/**
	 * load procedure status log from a completed procedure to expose to the user interface
	 */
	public void refreshProcedureStatusLog() {
		this.procedureStatusLog = statusLogMgmt.getProcedureStatusLog(procedureController.getProcedure().getProcedureId());
	}
	
	/**
	 * clears the reference being exposed to the user interface
	 */
	public void clearProcedureStatusLog() {
		procedureStatusLog = new ProcedureStatusLog();
	}
	
	
	
}
