package org.tmt.aps.peas.camera.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;

@Named
@SessionScoped
public class CcdDiagnosticController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	int ccdCommand;
	String utilityWord;
	
	
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

	public boolean getShowUtilityWord() {
		return ccdCommand == 6;
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
