package org.tmt.aps.peas.visualization.ui;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Named
@SessionScoped
public class AsyncPopupController {

	@EJB
	GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	UserPromptMgmt userPromptMgmt;

	
	
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
	}

	
	public void doCloseGraphicsDisplay() {
		graphicDisplayMgmt.setReturnState(1);
	}
	
	public void doCloseUserPrompt() {
		userPromptMgmt.setReturnState(1);
	}
}
