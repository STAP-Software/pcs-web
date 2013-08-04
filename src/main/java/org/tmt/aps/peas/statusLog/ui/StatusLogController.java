package org.tmt.aps.peas.statusLog.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;

@Named
@SessionScoped
public class StatusLogController implements Serializable {

	@EJB
	StatusLogMgmt statusLogMgmt;
	@EJB
	StatusLogger statusLogger;
	@Inject
	ProcedureController procedureController;
	
	private ProcedureStatusLog procedureStatusLog = new ProcedureStatusLog();
	
	public ProcedureStatusLog getProcedureStatusLog() {
		if (procedureStatusLog == null || procedureStatusLog.getLogEntryList() == null) {
			System.out.println("getProcedureStatusLog:: " + procedureStatusLog);
		} else {
			System.out.println("getProcedureStatusLog:: " + procedureStatusLog.getLogEntryList().size());		
		}
		return procedureStatusLog;
	}
	
	// call load most recent procedure status log
	public void refreshCurrentProcedureStatusLog() {
		System.out.println("refreshing");
		this.procedureStatusLog = statusLogger.getProcedureStatusLog();
	}
	
	// call to load legacy procedure status log
	public void refreshProcedureStatusLog() {
		this.procedureStatusLog = statusLogMgmt.getProcedureStatusLog(procedureController.getProcedure().getProcedureId());
	}
	

	
}
