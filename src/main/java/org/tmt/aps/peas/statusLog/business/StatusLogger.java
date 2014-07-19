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
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.lang.interop.RetVal;
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
	
	public void log(String key) {
		String message = MessageGenerator.generateMessage(key);
		procedureStatusLog.addEntry(message);
	}
	
	public void log(String key, Object arg1) {
		String message = MessageGenerator.generateMessage(key, arg1);
		procedureStatusLog.addEntry(message);
	}
	
	public void log(String key, Object arg1, Object arg2) {
		String message = MessageGenerator.generateMessage(key, arg1, arg2);
		procedureStatusLog.addEntry(message);
	}
	
	public void log(String key, Point arg1) {
		String message = MessageGenerator.generateMessage(key, arg1);
		procedureStatusLog.addEntry(message);
	}
	
	public void log(String key, FloatPoint arg1) {
		String message = MessageGenerator.generateMessage(key, arg1);
		procedureStatusLog.addEntry(message);
	}
	
	public void log(RetVal retVal) {
		
		// get the text from the resource bundle
		String message = MessageGenerator.generateErrorMessage(retVal);
		procedureStatusLog.addEntry(message);
	}
	
	public void saveLog(Long procedureId) {
		statusLogMgmt.saveStatusLog(procedureStatusLog, procedureId);
	}
	
	
	

}
