/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.business;

import java.util.Collection;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;

/**
 * Singleton EJB cache for the status log.  Maintains a {@link ProcedureStatusLog} and supports methods for adding entries to the log.
 * Stack support for one level of sub-procedure support only.
 * @author smichaels
 *
 */
@Singleton
@Lock(LockType.READ)
public class StatusLogger {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB 
	StatusLogMgmt statusLogMgmt;
	
	ProcedureStatusLog stack;  // 1-depth stack for now
	
	// cache
	ProcedureStatusLog procedureStatusLog;
	
	/**
	 * Startup clears all cached values
	 */
	@PostConstruct
	public void startup() {
		stack = null;
		procedureStatusLog = null;
	}

	/**
	 * Initialization method creates a new {@link ProcedureStatusLog}
	 */
	public void initLog() {
		if (procedureStatusLog == null) {
			procedureStatusLog = new ProcedureStatusLog();
		} else {
			// must be a subprocedure request for a logger
			stack = procedureStatusLog;
			procedureStatusLog = new ProcedureStatusLog();
		}
	}
	
	public ProcedureStatusLog getProcedureStatusLog() {
		return procedureStatusLog;
	}
	
	/**
	 * Logs a message bundle generated message with a key
	 * @param key the key to the message in the message bundle
	 */
	public void log(String key) {
		String message = MessageGenerator.generateMessage(key);
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Logs a message raw message
	 */
	public void logRaw(String message) {
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Logs a message bundle generated message with a key and variable number of arguments
	 * @param key the key to the message in the message bundle
	 * @param args variable number of args to substitute into the message: e.g. <code>{0} blah blah {1}</code>
	 */
	public void log(String key, Object ... args) {
				
		String message = MessageGenerator.generateMessage(key, args);
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Logs a message bundle generated message with a key and an integer coordinate
	 * @param key the key to the message in the message bundle
	 * @param arg1 the coordinate to substitute into the message into <code>{0},{1}</code>
	 */
	public void log(String key, Point arg1) {
		String message = MessageGenerator.generateMessage(key, arg1);
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Logs a message bundle generated message with a key and a floating point coordinate
	 * @param key the key to the message in the message bundle
	 * @param arg1 the coordinate to substitute into the message into <code>{0},{1}</code>
	 */
	public void log(String key, FloatPoint arg1) {
		String message = MessageGenerator.generateMessage(key, arg1);
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Logs an error message from a FORTRAN returned RetVal structure
	 * @param retVal the FORTRAN RetVal structure
	 */
	public void log(RetVal retVal) {
		
		// get the text from the resource bundle
		String message = MessageGenerator.generateErrorMessage(retVal);
		procedureStatusLog.addEntry(message);
	}
	
	/**
	 * Calls {@link StatusLogMgmt} to save the log in its current state.
	 * If the stack has a procedure log on it, pop it off and use it as the current procedureStatusLog
	 * @param procedureId the if of the procedure this status log should be stored with
	 */
	public void saveLog(Long procedureId) {
		// save and pop off stack
		statusLogMgmt.saveStatusLog(procedureStatusLog, procedureId);
		if (stack != null) {
			// saving a subprocedure log, pop off the stack
			procedureStatusLog = stack;
			stack = null;
		} else {
			procedureStatusLog = null;			
		}
	}
	
	
	

}
