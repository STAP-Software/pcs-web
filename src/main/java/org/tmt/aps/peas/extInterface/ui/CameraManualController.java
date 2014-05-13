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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
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
	int selectedFilterWheelPos = 1;
	int selectedRefBeam = 1;
	int shutterCmd = 1;
	double ccdExposureTime = 1.0;
	Point fineTiltCmd = new Point(0, 0);
	Point coarseTiltCmd = new Point(0, 0);
	int twoPosCmd = 0;
	int ccdPowerCmd = 0;

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

	public int getSelectedFilterWheelPos() {
		return selectedFilterWheelPos;
	}

	public void setSelectedFilterWheelPos(int selectedFilterWheelPos) {
		this.selectedFilterWheelPos = selectedFilterWheelPos;
	}

	public int getSelectedRefBeam() {
		return selectedRefBeam;
	}

	public void setSelectedRefBeam(int selectedRefBeam) {
		this.selectedRefBeam = selectedRefBeam;
	}

	public int getShutterCmd() {
		return shutterCmd;
	}

	public void setShutterCmd(int shutterCmd) {
		this.shutterCmd = shutterCmd;
	}

	public double getCcdExposureTime() {
		return ccdExposureTime;
	}

	public void setCcdExposureTime(double ccdExposureTime) {
		this.ccdExposureTime = ccdExposureTime;
	}

	public Point getFineTiltCmd() {
		return fineTiltCmd;
	}

	public void setFineTiltCmd(Point fineTiltCmd) {
		this.fineTiltCmd = fineTiltCmd;
	}

	public Point getCoarseTiltCmd() {
		return coarseTiltCmd;
	}

	public void setCoarseTiltCmd(Point coarseTiltCmd) {
		this.coarseTiltCmd = coarseTiltCmd;
	}

	public int getTwoPosCmd() {
		return twoPosCmd;
	}

	public void setTwoPosCmd(int twoPosCmd) {
		this.twoPosCmd = twoPosCmd;
	}

	public int getCcdPowerCmd() {
		return ccdPowerCmd;
	}

	public void setCcdPowerCmd(int ccdPowerCmd) {
		this.ccdPowerCmd = ccdPowerCmd;
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
			int maskNumber = cameraMgmt.commandPupilMask(selectedPupilMaskPos);
			// update position
			camera.getPupilWheel().setSelectedPupilMaskNumber(maskNumber);
			break;
			
		case 2: // Filter
			int filterNumber = cameraMgmt.commandFilterWheel(selectedFilterWheelPos);
			// update position
			camera.getFilterWheel().setSelectedFilterNumber(filterNumber);
			break;
			
		case 3: // Ref Beam
			cameraMgmt.commandReferenceBeamState(selectedRefBeam);
			camera.setCurrentRefBeam(selectedRefBeam);
			break;
			
		case 4: // Shutter
			if (shutterCmd == 0 || shutterCmd == 1) {
				int state = cameraMgmt.commandCcdShutterState(shutterCmd);
				camera.getShutter().setState(state);
			} else {
				// timed exposure
				cameraMgmt.commandCcdShutterExposure((int)(ccdExposureTime/1000));
			}
			break;
			
		case 5: // Fine Tilt
			Point fineResult = cameraMgmt.commandFineTiltMirror(fineTiltCmd);
			
			camera.getFineTiltMirror().setCurrentPosition(fineResult);
			
			System.out.println("got " + fineResult);
			break;
			
		case 6: // Coarse Tilt
			Point coarseResult = cameraMgmt.commandCoarseTiltMirror(coarseTiltCmd);
			
			camera.getCoarseTiltMirror().setCurrentPosition(coarseResult);
			break;
			
		case 7: // Two Position Mech
			cameraMgmt.commandTwoPositionDevice(twoPosCmd);
			break;
			
		case 8: // CCD Power
			cameraMgmt.commandCcdPowerState(ccdPowerCmd);
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
