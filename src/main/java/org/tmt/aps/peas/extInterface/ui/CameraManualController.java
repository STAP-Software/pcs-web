/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CameraPoller;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.DeviceStates;
import org.tmt.aps.peas.instrument.model.Shutter;
import org.tmt.aps.peas.instrument.model.TwoPosMechanism;

@Named
@SessionScoped
public class CameraManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PhysicalModel physicalModel;

	@EJB
	CameraMgmt cameraMgmt;
	@EJB
	CameraPoller cameraPoller;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	//Camera camera;
	//Ccd ccd;
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
	public void init() throws Exception {
		
		//physicalModel.refresh();
		//camera = physicalModel.getInstrument().getCamera();

		//camera.setCurrentState(1, 1, 1, 1, 23.0f, 6.22f, 0.43f, 7.54f, -0.32f, 1, 1, -43.2f);
		commandSelection = 1;

		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + physicalModel.getInstrument().getCcd());

		//ccd = physicalModel.getInstrument().getCcd();
	}

	public Camera getCamera() {
		return physicalModel.getInstrument().getCamera();
	}

	//public void setCamera(Camera camera) {
	//	this.camera = camera;
	//}

	public Ccd getCcd() {
		return physicalModel.getInstrument().getCcd();
	}

	//public void setCcd(Ccd ccd) {
	//	this.ccd = ccd;
	//}

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

	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (shutterCmd == Shutter.STATE_TIMED_EXPOSURE);
	}

	public String doViewCameraDiagnostic() {

		try {
			breadcrumbMenuBean.addFirstItem("Camera Diagnostic", "/modules/diagnostic/cameraDiagnostic.xhtml");

			return "/modules/diagnostic/cameraDiagnostic.xhtml?faces-redirect=true";

		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying camera database", e.getMessage()));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		
	}

	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	public void doSendCommand() {

		try {

			//cameraPoller.setDoPoll(false);
			//Thread.sleep(5000);
			//cameraMgmt.resetCamera();
			
			String commandType = null;
			
			logger.info("command selection = " + commandSelection);
			switch (commandSelection) {

			case 1: // Pupil Mask
				Future<Integer> pupilCmdFuture = cameraMgmt.commandPupilMask(selectedPupilMaskPos);
				while (!pupilCmdFuture.isDone()) {
					logger.debug("Thread waiting");
					Thread.sleep(500);
				}
				logger.debug("PupilCmdFuture is Done");
				int maskNumber = pupilCmdFuture.get();
				
				// update position
				getCamera().getPupilWheel().setState(DeviceStates.STATE_IN_POSITION);
				getCamera().getPupilWheel().setSelectedPupilMaskNumber(maskNumber);
				commandType = "Pupil Mask";
				break;

			case 2: // Filter
				Future<Integer> filterCmdFuture = cameraMgmt.commandFilterWheel(selectedFilterWheelPos);
				while (!filterCmdFuture.isDone()) {
					Thread.sleep(500);
				}
				int filterNumber = filterCmdFuture.get(); 
				// update position
				getCamera().getFilterWheel().setState(DeviceStates.STATE_IN_POSITION);
				getCamera().getFilterWheel().setSelectedFilterNumber(filterNumber);
				commandType = "Filter";
				break;

			case 3: // Ref Beam
				cameraMgmt.commandReferenceBeamState(selectedRefBeam);
				getCamera().setCurrentRefBeam(selectedRefBeam);
				commandType = "Ref Beam";
				break;

			case 4: // Shutter

				if (shutterCmd == Shutter.STATE_CLOSE) {
					int state = cameraMgmt.commandCcdShutterState(0);
					getCamera().getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
				} else if (shutterCmd == Shutter.STATE_OPEN) {
					int state = cameraMgmt.commandCcdShutterState(1);
					getCamera().getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
				} else {
					// timed exposure
					cameraMgmt.commandCcdShutterExposure((int) (ccdExposureTime * 1000));
					getCamera().getShutter().setState(Shutter.STATE_TIMED_EXPOSURE);
				}
				commandType = "Shutter";
				break;

			case 5: // Fine Tilt
				
				Future<Point> fineFuture = cameraMgmt.commandFineTiltMirror(fineTiltCmd);
				while (!fineFuture.isDone()) {
					Thread.sleep(500);
				}
				Point fineResult = fineFuture.get();
				
				getCamera().getFineTiltMirror().setCurrentPosition(fineResult);

				getCamera().getFineTiltMirror().setStateX(DeviceStates.STATE_IN_POSITION);
				getCamera().getFineTiltMirror().setStateY(DeviceStates.STATE_IN_POSITION);
				commandType = "Fine Tilt";
				break;

			case 6: // Coarse Tilt
				Future<Point> coarseFuture = cameraMgmt.commandCoarseTiltMirror(coarseTiltCmd);
				while (!coarseFuture.isDone()) {
					Thread.sleep(500);
				}
				Point coarseResult = coarseFuture.get();

				getCamera().getCoarseTiltMirror().setCurrentPosition(coarseResult);

				getCamera().getCoarseTiltMirror().setStateX(DeviceStates.STATE_IN_POSITION);
				getCamera().getCoarseTiltMirror().setStateY(DeviceStates.STATE_IN_POSITION);
				commandType = "Coarse Tilt";
				break;

			case 7: // Two Position Mech
				int command = (twoPosCmd == TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND) ? 1 : 0;
				Future<Integer> twoPosFuture = cameraMgmt.commandTwoPositionDevice(command);
				while (!twoPosFuture.isDone()) {
					Thread.sleep(500);
				}
				int twoPosState = twoPosFuture.get();
				getCamera().getTwoPosMechanism().setState(
						twoPosState == 1 ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);

				commandType = "Two Pos Mech";
				break;

			case 8: // CCD Power

				Future<Integer> ccdPowerFuture = cameraMgmt.commandCcdPowerState(ccdPowerCmd == Ccd.POWER_STATE_ON ? 1 : 0);
				while (!ccdPowerFuture.isDone()) {
					Thread.sleep(500);
				}
				int ccdState = ccdPowerFuture.get();
				getCcd().setState(ccdState == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);
				commandType = "Ccd Power";
				break;

			default:

			}

			//cameraPoller.setDoPoll(true);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage(commandType));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}


}
