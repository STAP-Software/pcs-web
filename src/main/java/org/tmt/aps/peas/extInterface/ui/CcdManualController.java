/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;

@Named
@SessionScoped
public class CcdManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	int ccdCommand;
	String utilityWord;
	int integrationTime;
	
	
	public int getCcdCommand() {
		return ccdCommand;
	}

	public void setCcdCommand(int ccdCommand) {
		this.ccdCommand = ccdCommand;
	}

	public String getUtilityWord() {
		return utilityWord;
	}

	public void setUtilityWord(String utilityWord) {
		this.utilityWord = utilityWord;
	}

	public int getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(int integrationTime) {
		this.integrationTime = integrationTime;
	}

	public boolean getShowUtilityWord() {
		return ccdCommand == 6;
	}

	public boolean getShowIntegrationTime() {
		return ccdCommand == 12;
	}

	public String doViewCcdDiagnostic() {

		ccdCommand = 1;
		utilityWord = "";
		
		breadcrumbMenuBean.addFirstItem("CCD Diagnostic", "doViewCcdDiagnostic()");

		return "/modules/diagnostic/ccdDiagnostic.xhtml?faces-redirect=true";
	}


	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}
	public void doSendCommand() {

		FacesContext context = FacesContext.getCurrentInstance();  
        
        context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0")); 
	}

}
