package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.KnifeEdge;
import org.tmt.aps.peas.instrument.model.PreflashLEDs;
import org.tmt.aps.peas.instrument.model.Shutter;

@Named
@SessionScoped
public class CameraManualController implements Serializable {



	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	Camera camera;
	int commandSelection;

	@PostConstruct
	public void init() {
		refreshCamera();
	}
	
	
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
				 1,  -43.2f);
		commandSelection = 1;
	}
	
	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (camera.getShutter().getState() == Shutter.STATE_TIMED_EXPOSURE);
	}
	
	public boolean getRenderPreflashTime() {
		return (commandSelection == 5) && (camera.getPreflashLEDs().getState() == PreflashLEDs.STATE_TIMED_FLASH);
	}
	
	
	public String doViewCameraDiagnostic() {

		
		refreshCamera();
		
		breadcrumbMenuBean.addFirstItem("Camera Diagnostic", "doViewCameraDiagnostic()");

		return "/modules/diagnostic/cameraDiagnostic.xhtml?faces-redirect=true";
	}


	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}
	
	public void doSendCommand() {

		FacesContext context = FacesContext.getCurrentInstance();  
        
        context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0")); 
	}

}
