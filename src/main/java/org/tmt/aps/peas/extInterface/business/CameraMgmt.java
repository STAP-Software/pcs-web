/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.DeviceStates;
import org.tmt.aps.peas.instrument.model.Shutter;
import org.tmt.aps.peas.instrument.model.TwoPosMechanism;

@Stateless
public class CameraMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ExtInfFactory extInfFactory;
	@EJB
	CameraMgmtAsync cameraMgmtAsync;
	@EJB
	PhysicalModel physicalModel;

	// All Camera Commands should be defined here

	public CameraQueryResult queryCamera(int deviceCode) throws Exception {
		return extInfFactory.getCameraCommand().queryCamera(deviceCode);
	}

	public CameraStatus queryCameraStatus() throws Exception {
		return extInfFactory.getCameraCommand().getCameraStatus();
	}

	@Asynchronous
	public Future<Integer> commandPupilMask(int pupilMaskPosition) throws Exception {
		System.out.println("in commandPupilMask");
		int result = extInfFactory.getCameraCommand().commandPupilMask(pupilMaskPosition);
		System.out.println("returning");
		AsyncResult<Integer> as = new AsyncResult<Integer>(result);
		System.out.println("completed");
		return as;
	}

	/**
	 * Move filter wheel to specified location<br/>
	 * Timeout: 60 sec
	 * 
	 * @param filterWheelPosition
	 *            filter wheel position 1-6
	 * @return achieved Filter Wheel Position
	 */
	@Asynchronous
	public Future<Integer> commandFilterWheel(int filterWheelPosition) throws Exception {
		logger.info("camera command = " + extInfFactory.getCameraCommand());
		int result = extInfFactory.getCameraCommand().commandFilterWheel(filterWheelPosition);
		return new AsyncResult<Integer>(result);
	}

	/**
	 * Extend (into the beam) or Retract (out of the beam) the 45 Degree mirror (K1) or beam block (K2)<br/>
	 * Timeout: 60 sec
	 * 
	 * @param twoPositionDevicePosition
	 *            Desired mechanism position (0 = retracted,1=extended) 0 or 1
	 * @return achieved Two Position Device Position (0 = retracted,1=extended)
	 */
	@Asynchronous
	public Future<Integer> commandTwoPositionDevice(int twoPositionDevicePosition) throws Exception {
		logger.info("commandTwoPositionDevice: command is: " + twoPositionDevicePosition);
		int result = extInfFactory.getCameraCommand().commandTwoPositionDevice(twoPositionDevicePosition);
		return new AsyncResult<Integer>(result);
	}

	/**
	 * Open the shutter for the specified period of time<br/>
	 * Timeout: ccdExposureTime/1000 + 10 sec
	 * 
	 * @param ccdExposureTime
	 *            Desired exposure time 100 to 360000 milliseconds
	 * @return
	 */
	public void commandCcdShutterExposure(int ccdExposureTime) throws Exception {
		extInfFactory.getCameraCommand().commandCcdShutterExposure(ccdExposureTime);
	}

	/**
	 * Command the CCD shutter to open or close<br/>
	 * Timeout: 60 sec
	 * 
	 * @param ccdShuterState
	 *            Desired CCD shutter state (0 = closed, 1 = open) 0 or 1
	 * @return achived CCD Shuter State (0 = closed, 1 = open)
	 */
	public int commandCcdShutterState(int ccdShuterState) throws Exception {
		return extInfFactory.getCameraCommand().commandCcdShutterState(ccdShuterState);
	}

	/**
	 * Command a specified reference beam to turn off or on<br/>
	 * Timeout: 60 sec
	 * 
	 * @param referenceBeamCommand
	 *            Number of the desired reference beam to turn on, 0 turns off all reference beams. Multiple reference beams can be on at
	 *            the same time. 0-9
	 * @return
	 */
	@Asynchronous
	public Future<Integer> commandReferenceBeamState(int referenceBeamCommand) throws Exception {
		extInfFactory.getCameraCommand().commandReferenceBeamState(referenceBeamCommand);
		return new AsyncResult<Integer>(referenceBeamCommand);
	}

	/**
	 * Turn on or off the CCD Power <br/>
	 * Timeout: 60 sec <br/>
	 * On K2 this also turns on the thermal electrical cooler, but on K1 the thermal electrical cooler is a manual switch.
	 * 
	 * @param ccdPowerState
	 *            Desired CCD power state (0 = off, 1 = on) 0 or 1
	 * @return achieved CCD Power State (0 = off, 1 = on)
	 */
	@Asynchronous
	public Future<Integer> commandCcdPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandCcdPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Point> commandFineTiltMirror(Point fineTiltCmd) throws Exception {

		Future<Integer> xFuture = cameraMgmtAsync.commandFineTiltMirrorX(fineTiltCmd.x);
		Future<Integer> yFuture = cameraMgmtAsync.commandFineTiltMirrorY(fineTiltCmd.y);

		while (!xFuture.isDone() || !yFuture.isDone()) {
			Thread.sleep(300);
		}
		Point result = new Point(xFuture.get(), yFuture.get());
		return new AsyncResult<Point>(result);
	}

	@Asynchronous
	public Future<Point> commandCoarseTiltMirror(Point coarseTiltCmd) throws Exception {
		Future<Integer> xFuture = cameraMgmtAsync.commandCoarseTiltMirrorX((int) coarseTiltCmd.x);
		Future<Integer> yFuture = cameraMgmtAsync.commandCoarseTiltMirrorY((int) coarseTiltCmd.y);

		while (!xFuture.isDone() || !yFuture.isDone()) {
			Thread.sleep(300);
		}
		Point result = new Point(xFuture.get(), yFuture.get());
		return new AsyncResult<Point>(result);
	}

	@Asynchronous
	public Future<Boolean> refreshStatusOrig() throws Exception {

		Camera camera = physicalModel.getInstrument().getCamera();
		Ccd ccd = physicalModel.getInstrument().getCcd();

		CameraQueryResult result = null;
		CameraQueryResult result2 = null;
		
		
		// Pupil Mask
		result = queryCamera(CameraCommand.DEVICE_CODE_PUPIL_WHEEL);
		camera.getPupilWheel().setState(result.getState());
		camera.getPupilWheel().setSelectedPupilMaskNumber(result.getStateValue());

		// Filter
		result = queryCamera(CameraCommand.DEVICE_CODE_FILTER_WHEEL);
		camera.getFilterWheel().setState(result.getState());
		camera.getFilterWheel().setSelectedFilterNumber(result.getStateValue());

		// Ref Beam
		result = queryCamera(CameraCommand.DEVICE_CODE_REFERENCE_BEAMS);
		camera.setCurrentRefBeam(result.getStateValue() == 0 ? 0 : result.getStateValue());

		// Shutter
		result = queryCamera(CameraCommand.DEVICE_CODE_CCD_SHUTTER);

		if (result.getState() == DeviceStates.STATE_IN_TRANSIT) {
			camera.getShutter().setState(Shutter.STATE_IN_TRANSIT);
		} else {
			camera.getShutter().setState(result.getStateValue() == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
		}

		// Fine Tilt
		result = queryCamera(CameraCommand.DEVICE_CODE_X_TILT_PLATE);
		camera.getFineTiltMirror().setStateX(result.getState());

		result2 = queryCamera(CameraCommand.DEVICE_CODE_Y_TILT_PLATE);
		camera.getFineTiltMirror().setStateY(result2.getState());

		Point fineResult = new Point(result.getStateValue(), result2.getStateValue());
		camera.getFineTiltMirror().setCurrentPosition(fineResult);

		// Coarse Tilt
		result = queryCamera(CameraCommand.DEVICE_CODE_X_STEERING_MIRROR);
		camera.getCoarseTiltMirror().setStateX(result.getState());

		result2 = queryCamera(CameraCommand.DEVICE_CODE_Y_STEERING_MIRROR);
		camera.getCoarseTiltMirror().setStateY(result2.getState());

		Point coarseResult = new Point(result.getStateValue(), result2.getStateValue());
		camera.getCoarseTiltMirror().setCurrentPosition(coarseResult);

		// Two Position Mech
		result = queryCamera(CameraCommand.DEVICE_CODE_TWO_POSITION_DEVICE);
		if (result.getState() == DeviceStates.STATE_IN_TRANSIT) {
			camera.getTwoPosMechanism().setState(TwoPosMechanism.TWO_POS_MECH_STATE_IN_TRANSIT);
		} else {
			camera.getTwoPosMechanism().setState(
					result.getStateValue() == 1 ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);
		}

		// CCD Power
		result = queryCamera(CameraCommand.DEVICE_CODE_CCD_POWER);
		ccd.setState(result.getStateValue() == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

		// CCD Temperature
		result = queryCamera(CameraCommand.DEVICE_CODE_CCD_TEMPERATURE);
		ccd.setTemperature(((float) result.getDoubleVal()));

		// Instrument Temperature
		result = queryCamera(CameraCommand.DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE);
		camera.setInstrumentTemperature(((float) result.getDoubleVal()));

		// Electronics Box Temperature
		result = queryCamera(CameraCommand.DEVICE_CODE_ELECTONICS_BOX_TEMPERATURE);
		camera.setElectronicsBoxTemperature(((float) result.getDoubleVal()));

		logger.info(">> status refresh compete <<");
		
		return new AsyncResult<Boolean>(true);
	}

	@Asynchronous
	public Future<Boolean> refreshStatus() throws Exception {

		Camera camera = physicalModel.getInstrument().getCamera();
		Ccd ccd = physicalModel.getInstrument().getCcd();

		CameraStatus cameraStatus = queryCameraStatus();
		
		// Pupil Mask
		camera.getPupilWheel().setState(cameraStatus.prismWheelIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getPupilWheel().setSelectedPupilMaskNumber(cameraStatus.prismWheelPos);

		// Filter
		camera.getFilterWheel().setState(cameraStatus.filterWheelIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFilterWheel().setSelectedFilterNumber(cameraStatus.filterWheelPos);

		// Ref Beam
		camera.setCurrentRefBeam(cameraStatus.refBeamPos);

		// Shutter
		camera.getShutter().setState(cameraStatus.shutterState == CameraCommand.CLOSED ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
		
		// Fine Tilt
		camera.getFineTiltMirror().setCurrentPosition(new Point(cameraStatus.tiltPlateX, cameraStatus.tiltPlateY));
		camera.getFineTiltMirror().setStateX(cameraStatus.tiltPlateXIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFineTiltMirror().setStateY(cameraStatus.tiltPlateYIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);

		// Coarse Tilt
		camera.getCoarseTiltMirror().setCurrentPosition(new Point(cameraStatus.steeringMirrorX, cameraStatus.steeringMirrorY));
		camera.getCoarseTiltMirror().setStateX(cameraStatus.steeringMirrorXIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getCoarseTiltMirror().setStateY(cameraStatus.steeringMirrorYIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);

		// Two Position Mech
		camera.getTwoPosMechanism().setState(cameraStatus.twoPosDevPos == CameraCommand.EXTENDED ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);

		// CCD Power
		ccd.setState(cameraStatus.ccdPowerState == CameraCommand.ON ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

		// CCD Temperature
		ccd.setTemperature(((float) cameraStatus.ccdTemp));

		// Instrument Temperature
		camera.setInstrumentTemperature(((float) cameraStatus.benchTemp));

		// Electronics Box Temperature
		camera.setElectronicsBoxTemperature(((float) cameraStatus.boxTemp));

		logger.info(">> status refresh compete <<");
		
		return new AsyncResult<Boolean>(true);
	}

}
