package org.tmt.aps.peas.statusLog.ui;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.statusLog.model.ProcedureStatusLog;

@Named
@SessionScoped
public class StatusLogController {

	@EJB
	StatusLogMgmt statusLogMgmt;
	
	public ProcedureStatusLog getProcedureStatusLog() {
		return statusLogMgmt.getProcedureStatusLog();
	}
	
	
	
}
