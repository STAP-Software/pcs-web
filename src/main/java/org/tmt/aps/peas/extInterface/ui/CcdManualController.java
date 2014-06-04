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
import org.tmt.aps.peas.extInterface.business.CcdMgmt;

@Named
@SessionScoped
public class CcdManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	@EJB
	CcdMgmt ccdMgmt;

	int commandSelection;
	int integrationTime;
	
	


	public int getCommandSelection() {
		return commandSelection;
	}

	public void setCommandSelection(int commandSelection) {
		this.commandSelection = commandSelection;
	}


	public int getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(int integrationTime) {
		this.integrationTime = integrationTime;
	}

	public boolean getShowIntegrationTime() {
		return commandSelection == 12;
	}
	
	public String doViewCcdDiagnostic() {

		commandSelection = 1;
		
		breadcrumbMenuBean.addFirstItem("CCD Diagnostic", "doViewCcdDiagnostic()");

		return "/modules/diagnostic/ccdDiagnostic.xhtml?faces-redirect=true";
	}

	public void sendCcdCommand() {
		try {

			switch (commandSelection) {

			case 1: // FastWipe
				// update position
				ccdMgmt.fastWipeCcd();
				break;

			case 2: // Continuous Wipe on
				// update position
				//camera.getPupilWheel().setState(DeviceStates.STATE_IN_POSITION);
				//camera.getPupilWheel().setSelectedPupilMaskNumber(maskNumber);
				break;

			case 3: // Read CCD Raw
				// update position
				//camera.getPupilWheel().setState(DeviceStates.STATE_IN_POSITION);
				//camera.getPupilWheel().setSelectedPupilMaskNumber(maskNumber);
				break;


			default:

			}

			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}
	}

	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}
	public void doSendCommand() {

		FacesContext context = FacesContext.getCurrentInstance();  
        
        context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0")); 
	}

}
