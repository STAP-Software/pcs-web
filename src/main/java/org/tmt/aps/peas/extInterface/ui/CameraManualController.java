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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;
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

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	Camera camera;
	Ccd ccd;
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
		
		physicalModel.refresh();
		camera = physicalModel.getInstrument().getCamera();

		camera.setCurrentState(1, 1, 1, 1, 23.0f, 6.22f, 0.43f, 7.54f, -0.32f, 1, 1, -43.2f);
		commandSelection = 1;

		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + physicalModel.getInstrument().getCcd());

		ccd = physicalModel.getInstrument().getCcd();
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Ccd getCcd() {
		return ccd;
	}

	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
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

	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (shutterCmd == Shutter.STATE_TIMED_EXPOSURE);
	}

	public String doViewCameraDiagnostic() {

		try {
			breadcrumbMenuBean.addFirstItem("Camera Diagnostic", "doViewCameraDiagnostic()");

			return "/modules/diagnostic/cameraDiagnostic.xhtml?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error querying camera database", e.getMessage()));
			return null;
		}
		
	}

	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	public void doSendCommand() {

		try {

			logger.info("command selection = " + commandSelection);
			switch (commandSelection) {

			case 1: // Pupil Mask
				Future<Integer> pupilCmdFuture = cameraMgmt.commandPupilMask(selectedPupilMaskPos);
				while (!pupilCmdFuture.isDone()) {
					System.out.println("Thread waiting");
					Thread.sleep(500);
				}
				System.out.println("PupilCmdFuture is Done");
				int maskNumber = pupilCmdFuture.get();
				
				// update position
				camera.getPupilWheel().setState(DeviceStates.STATE_IN_POSITION);
				camera.getPupilWheel().setSelectedPupilMaskNumber(maskNumber);
				break;

			case 2: // Filter
				Future<Integer> filterCmdFuture = cameraMgmt.commandFilterWheel(selectedFilterWheelPos);
				while (!filterCmdFuture.isDone()) {
					Thread.sleep(500);
				}
				int filterNumber = filterCmdFuture.get(); 
				// update position
				camera.getFilterWheel().setState(DeviceStates.STATE_IN_POSITION);
				camera.getFilterWheel().setSelectedFilterNumber(filterNumber);
				break;

			case 3: // Ref Beam
				cameraMgmt.commandReferenceBeamState(selectedRefBeam);
				camera.setCurrentRefBeam(selectedRefBeam);
				break;

			case 4: // Shutter

				if (shutterCmd == Shutter.STATE_CLOSE) {
					int state = cameraMgmt.commandCcdShutterState(0);
					camera.getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
				} else if (shutterCmd == Shutter.STATE_OPEN) {
					int state = cameraMgmt.commandCcdShutterState(1);
					camera.getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
				} else {
					// timed exposure
					cameraMgmt.commandCcdShutterExposure((int) (ccdExposureTime * 1000));
					camera.getShutter().setState(Shutter.STATE_TIMED_EXPOSURE);
				}
				break;

			case 5: // Fine Tilt
				
				Future<Point> fineFuture = cameraMgmt.commandFineTiltMirror(fineTiltCmd);
				while (!fineFuture.isDone()) {
					Thread.sleep(500);
				}
				Point fineResult = fineFuture.get();
				
				camera.getFineTiltMirror().setCurrentPosition(fineResult);

				camera.getFineTiltMirror().setStateX(DeviceStates.STATE_IN_POSITION);
				camera.getFineTiltMirror().setStateY(DeviceStates.STATE_IN_POSITION);
				break;

			case 6: // Coarse Tilt
				Future<Point> coarseFuture = cameraMgmt.commandCoarseTiltMirror(coarseTiltCmd);
				while (!coarseFuture.isDone()) {
					Thread.sleep(500);
				}
				Point coarseResult = coarseFuture.get();

				camera.getCoarseTiltMirror().setCurrentPosition(coarseResult);

				camera.getCoarseTiltMirror().setStateX(DeviceStates.STATE_IN_POSITION);
				camera.getCoarseTiltMirror().setStateY(DeviceStates.STATE_IN_POSITION);
				break;

			case 7: // Two Position Mech
				int command = (twoPosCmd == TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND) ? 1 : 0;
				Future<Integer> twoPosFuture = cameraMgmt.commandTwoPositionDevice(command);
				while (!twoPosFuture.isDone()) {
					Thread.sleep(500);
				}
				int twoPosState = twoPosFuture.get();
				camera.getTwoPosMechanism().setState(
						twoPosState == 1 ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);

				break;

			case 8: // CCD Power

				if (ccdPowerCmd == Ccd.POWER_STATE_ON) {
					int ccdState = cameraMgmt.commandCcdPowerState(1);
					ccd.setState(ccdState == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

				} else if (ccdPowerCmd == Ccd.POWER_STATE_OFF) {
					int ccdState = cameraMgmt.commandCcdPowerState(0);
					ccd.setState(ccdState == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

				}
				break;

			default:

			}

			System.out.println("Returning");
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));

		} catch (CommandFailureException e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Command Failure Exception: failure code = " + e.getFailureCode() , e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			// TODO: generic way to output errors that give all info to user on screen
			context.addMessage(null, new FacesMessage("Error: " + e.getMessage() + e.getClass().getName() + " " + e.getStackTrace()[0]));

		}
		
	}

	public void doRefresh() {

		try {

			CameraQueryResult result = null;
			CameraQueryResult result2 = null;
			
			// Pupil Mask
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_PUPIL_WHEEL);
			logger.info("camera = " + camera + ", result = " + result);
			camera.getPupilWheel().setState(result.getState());
			camera.getPupilWheel().setSelectedPupilMaskNumber(result.getStateValue());

			
			// Filter
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_FILTER_WHEEL);
			camera.getFilterWheel().setState(result.getState());
			camera.getFilterWheel().setSelectedFilterNumber(result.getStateValue());

			// Ref Beam
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_REFERENCE_BEAMS);
			camera.setCurrentRefBeam(result.getStateValue() == 0 ? 0 : result.getStateValue());

			// Shutter
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_CCD_SHUTTER);
			
			if (result.getState() == DeviceStates.STATE_IN_TRANSIT) {
				camera.getShutter().setState(Shutter.STATE_IN_TRANSIT);
			} else {
				camera.getShutter().setState(result.getStateValue() == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
			}

			
			// Fine Tilt
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_X_TILT_PLATE);
			camera.getFineTiltMirror().setStateX(result.getState());
			
			result2 = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_Y_TILT_PLATE);
			camera.getFineTiltMirror().setStateY(result2.getState());
			
			Point fineResult = new Point(result.getStateValue(), result2.getStateValue());
			camera.getFineTiltMirror().setCurrentPosition(fineResult);


			
			// Coarse Tilt
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_X_STEERING_MIRROR);
			camera.getCoarseTiltMirror().setStateX(result.getState());
			
			result2 = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_Y_STEERING_MIRROR);
			camera.getCoarseTiltMirror().setStateY(result2.getState());
			
			Point coarseResult = new Point(result.getStateValue(), result2.getStateValue());
			camera.getCoarseTiltMirror().setCurrentPosition(coarseResult);

			
			// Two Position Mech
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_TWO_POSITION_DEVICE);
			if (result.getState() == DeviceStates.STATE_IN_TRANSIT) {
				camera.getTwoPosMechanism().setState(TwoPosMechanism.TWO_POS_MECH_STATE_IN_TRANSIT);
			} else {
				camera.getTwoPosMechanism().setState(
						result.getStateValue() == 1 ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);
			}
						
			// CCD Power
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_CCD_POWER);			
			ccd.setState(result.getStateValue() == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

			
			// CCD Temperature
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_CCD_TEMPERATURE);
			ccd.setTemperature(((float)result.getDoubleVal()));
			
			// Instrument Temperature
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE);
			camera.setInstrumentTemperature(((float)result.getDoubleVal()));
			
			// Electronics Box Temperature
			result = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_ELECTONICS_BOX_TEMPERATURE);
			camera.setElectronicsBoxTemperature(((float)result.getDoubleVal()));
			
			

			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			
			// TODO: generic way to output errors that give all info to user on screen
			context.addMessage(null, new FacesMessage("Error: " + e.getMessage() + e.getClass().getName() + " " + e.getStackTrace()[0]));

		}
	}

}
