/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.business;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
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
	
	public void log(String entry) {
		procedureStatusLog.addEntry(entry);
	}
	
	public void log(RetVal retVal) {
		
		// get the text from the resource bundle
		String message = generateErrorMessage(retVal);
		procedureStatusLog.addEntry(message);
	}
	
	public void saveLog(Long procedureId) {
		statusLogMgmt.saveStatusLog(procedureStatusLog, procedureId);
	}
	
	
	private String generateErrorMessage(RetVal retVal) {
		String key = "E" + String.format("%05d", retVal.getCode());
		String pattern = ResourceBundle.getBundle("errorCodes").getString(key);
		
		
		Double[] args = new Double[10];
		args[0] = retVal.getArg0();
		args[1] = retVal.getArg1();
		args[2] = retVal.getArg2();
		args[3] = retVal.getArg3();
		args[4] = retVal.getArg4();
		args[5] = retVal.getArg5();
		args[6] = retVal.getArg6();
		args[7] = retVal.getArg7();
		args[8] = retVal.getArg8();
		args[9] = retVal.getArg9();
		
		String message = MessageFormat.format(pattern, (Object[])args);
		
		return message;

	}		

}
