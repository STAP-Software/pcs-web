package org.tmt.aps.peas.procedure.ui;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.SessionController;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Named
@SessionScoped
public class AsyncController {

	@EJB
	GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	UserPromptMgmt userPromptMgmt;
	@EJB
	ProcedureExecutionState procedureExecutionMgmt;
	@Inject
	SessionController sessionController;
	
	public void asyncListener() {

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> Polling...");
		
		// ask user prompt display manager for any pending user prompts
		// ask graphic display manager for any pending displays
		if (userPromptMgmt.getPendingPrompt() != null) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("userPromptDialog.show()");
			userPromptMgmt.setPendingPrompt(null);
		}

		// ask graphic display manager for any pending displays
		if (graphicDisplayMgmt.getPendingDisplay() != null) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> About to execute requestContext...");
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.execute("displayDialog.show()");
			graphicDisplayMgmt.setPendingDisplay(null);
		}
		
		sessionController.setProcedureExecuting(procedureExecutionMgmt.getExecutionStatus());

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
		System.out.println("getPercentComplete::" + procedureExecutionMgmt.getPercentComplete());
		return procedureExecutionMgmt.getPercentComplete();
	}


	
	
	
}
