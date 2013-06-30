package org.tmt.aps.peas.camera.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.camera.model.Camera;
import org.tmt.aps.peas.camera.model.KnifeEdge;
import org.tmt.aps.peas.camera.model.PreflashLEDs;
import org.tmt.aps.peas.camera.model.Shutter;

@Named
@SessionScoped
public class CameraDiagnosticController implements Serializable {



	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	Camera camera;
	int commandSelection;

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public int getCommandSelection() {
		return commandSelection;
	}

	public void setCommandSelection(int commandSelection) {
		this.commandSelection = commandSelection;
	}

	public void refreshCamera() {
		camera = new Camera(1, 1, 1, 1, 23.0f, 1, 0.0f, 
				 6.22f,  0.43f,  7.54f,  -0.32f,  1, 
				 1,  123.5f,  99.3f,  43.6f, 
				 1, 0f, 1, 0f,
				 2.3f,  1);
		commandSelection = 1;
	}
	
	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (camera.getShutter().getState() == Shutter.STATE_TIMED_EXPOSURE);
	}
	
	public boolean getRenderPreflashTime() {
		return (commandSelection == 5) && (camera.getPreflashLEDs().getState() == PreflashLEDs.STATE_TIMED_FLASH);
	}
	
	public boolean getRenderKnifeEdgePosition() {
		return (commandSelection == 13) && (camera.getKnifeEdge().getPositionCommand() == KnifeEdge.POSITION_COMMAND_TYPE_POSITION);
	}
	
	public boolean getRenderKnifeEdgeRate() {
		return (commandSelection == 14) && (camera.getKnifeEdge().getRateCommand() == KnifeEdge.RATE_COMMAND_TYPE_RATE);
	}
	
	public String doViewCameraDiagnostic() {

		
		refreshCamera();
		
		breadcrumbMenuBean.addFirstItem("Camera Diagnostic", "doViewCameraDiagnostic()");

		return "/modules/diagnostic/cameraDiagnostic.xhtml?faces-redirect=true";
	}


	public String doCancel() {

		return "/modules/sessionList.xhtml?faces-redirect=true";
	}
	
	public void doSendCommand() {

		FacesContext context = FacesContext.getCurrentInstance();  
        
        context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0")); 
	}

}
