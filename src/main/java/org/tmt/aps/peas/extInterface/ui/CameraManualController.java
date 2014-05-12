/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.Shutter;

@Named
@SessionScoped
public class CameraManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PhysicalModel physicalModel;
	
	@EJB
	CameraMgmt cameraMgmt;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	Camera camera;
	int commandSelection;
	int selectedPupilMaskPos = 1;

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

	public int getSelectedPupilMaskPos() {
		return selectedPupilMaskPos;
	}

	public void setSelectedPupilMaskPos(int selectedPupilMaskPos) {
		this.selectedPupilMaskPos = selectedPupilMaskPos;
	}


	public void refreshCamera() {
		
		camera = physicalModel.getInstrument().getCamera();
		
		camera.setCurrentState(1, 1, 1, 1, 23.0f,  
				 6.22f,  0.43f,  7.54f,  -0.32f,  1, 
				 1,  -43.2f);
		commandSelection = 1;
	}
	
	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (camera.getShutter().getState() == Shutter.STATE_TIMED_EXPOSURE);
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
		
		try {
		
		switch (commandSelection) {
		
		case 1:	// Pupil Mask
			cameraMgmt.commandPupilMask(selectedPupilMaskPos);
			break;
			
		case 2: // Filter
			break;
			
		case 3: // Ref Beam
			break;
			
		case 4: // Shutter
			break;
			
		case 5: // Fine Tilt
			break;
			
		case 6: // Coarse Tilt
			break;
			
		case 7: // Two Position Mech
			break;
			
		case 8: // CCD Power
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

}
