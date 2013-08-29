/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.business;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;


@Singleton
@Lock(LockType.READ)
public class StatusLogger {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB 
	StatusLogMgmt statusLogMgmt;
	
	// cache
	ProcedureStatusLog procedureStatusLog;

	public void initLog() {
		procedureStatusLog = new ProcedureStatusLog();
	}
	
	public ProcedureStatusLog getProcedureStatusLog() {
		return procedureStatusLog;
	}
	
	public void log(String entry) {
		procedureStatusLog.addEntry(entry);
	}
	
	public void log(int code, String[] args) {
		
		// get the text from the resource bundle
		
		//procedureStatusLog.addEntry(entry);
	}
	
	public void saveLog(Long procedureId) {
		statusLogMgmt.saveStatusLog(procedureStatusLog, procedureId);
	}
}
