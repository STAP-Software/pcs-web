package org.tmt.aps.peas.statusLog.business;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;



@Singleton
@Lock(LockType.READ)
public class StatusLogMgmt {

	ProcedureStatusLog procedureStatusLog;
	
	public ProcedureStatusLog getProcedureStatusLog() {
		return procedureStatusLog;
	}
	
	public void initLog() {
		procedureStatusLog = new ProcedureStatusLog();
	}
	
	public void log(String entry) {
		procedureStatusLog.addEntry(entry);
	}
	
}
