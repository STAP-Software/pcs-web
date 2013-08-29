/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.ui;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.statusLog.ui.StatusLogController;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Named
@SessionScoped
public class AsyncController {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	UserPromptMgmt userPromptMgmt;
	@EJB
	ProcedureExecutionState procedureExecutionMgmt;
	@Inject
	SessionController sessionController;
	@Inject
	StatusLogController statusLogController;
	
	public void asyncListener() {

		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> Polling...");
		
		// ask user prompt display manager for any pending user prompts
		// ask graphic display manager for any pending displays
		if (userPromptMgmt.getPendingPrompt() != null) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("userPromptDialog.show()");
			userPromptMgmt.setPendingPrompt(null);
		}

		// ask graphic display manager for any pending displays
		if (graphicDisplayMgmt.getPendingDisplay() != null) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("displayDialog.show()");
			graphicDisplayMgmt.setPendingDisplay(null);
		}
		
		sessionController.setProcedureExecuting(procedureExecutionMgmt.getExecutionStatus());

		// refresh the controller from the logger to get it to the display
		statusLogController.refreshCurrentProcedureStatusLog();
	}

	public void onComplete() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Procedure Completed", "Progress Completed"));
	}

	public String doAbortProcedure() {
		return null;
	}
	
	public void doCloseGraphicsDisplay() {
		graphicDisplayMgmt.setReturnState(1);
	}
	
	public void doCloseUserPrompt() {
		userPromptMgmt.setReturnState(1);
	}


	public boolean isExecutionStatus() {
		return procedureExecutionMgmt.getExecutionStatus();
	}


	public int getPercentComplete() {
		logger.debug("getPercentComplete::" + procedureExecutionMgmt.getPercentComplete());
		return procedureExecutionMgmt.getPercentComplete();
	}


	
	
	
}
