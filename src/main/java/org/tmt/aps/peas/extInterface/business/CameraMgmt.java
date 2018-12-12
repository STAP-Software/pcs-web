/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.Hashtable;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Instrument;

/**
 * EJB Session bean for the PCS camera command interface. 
 * This EJB is the single entry point to the PCS camera interface called from executors and diagnostic user interfaces. 
 * All calls are delegated to the {@link ExtInfFactory} which will delegate to either the actual RPC client interface or a simulator.
 * @author smichaels
 */
@Stateless
public class CameraMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ExtInfFactory extInfFactory;
	@EJB
	CameraMgmtAsync cameraMgmtAsync;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	ExtInfConfigState extInfConfigState;

	
	public static final int X_TILT_MOTOR = 1;
	public static final int Y_TILT_MOTOR = 2;
	public static final int X_STEERING_MOTOR = 3;
	public static final int Y_STEERING_MOTOR = 4;

	
	public static Hashtable<Integer, Integer> errorCodeToMechanism = new Hashtable<Integer, Integer>();
	
	static {
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_TILT_MOTOR_CONTROLLER_NOT_RESPONDING, X_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_TILT_MOTOR_FAILED_TO_FIND_HOME_POSITION, X_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_TILT_MOTOR_FAILED_TO_REACH_COMMANDED_POSITION, X_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_TILT_MOTOR_CONTROLLER_NOT_RESPONDING, Y_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_TILT_MOTOR_FAILED_TO_FIND_HOME_POSITION, Y_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_TILT_MOTOR_FAILED_TO_REACH_COMMANDED_POSITION, Y_TILT_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_STEERING_MOTOR_CONTROLLER_NOT_RESPONDING, X_STEERING_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_STEERING_MOTOR_FAILED_TO_FIND_HOME_POSITION, X_STEERING_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_X_STEERING_MOTOR_FAILED_TO_REACH_COMMANDED_POSITION, X_STEERING_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_STEERING_MOTOR_CONTROLLER_NOT_RESPONDING, Y_STEERING_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_STEERING_MOTOR_FAILED_TO_FIND_HOME_POSITION, Y_STEERING_MOTOR);
		errorCodeToMechanism.put(CommandFailureException.FAILURE_CODE_Y_STEERING_MOTOR_FAILED_TO_REACH_COMMANDED_POSITION, Y_STEERING_MOTOR);
	}
	
	
	
	// All Camera Commands should be defined here

	public CameraQueryResult queryCamera(int deviceCode) throws Exception {
		return extInfFactory.getCameraCommand().queryCamera(deviceCode);
	}

	public CameraStatus queryCameraStatus() throws Exception {
		return extInfFactory.getCameraCommand().getCameraStatus();
	}

	public void resetCamera() throws Exception {
		extInfFactory.getCameraCommand().resetCamera();
	}
	
	@Asynchronous
	public Future<Integer> commandPupilMask(int pupilMaskPosition) throws Exception {
		logger.debug("in commandPupilMask");
		int result = extInfFactory.getCameraCommand().commandPupilMask(pupilMaskPosition);
		logger.debug("returning");
		AsyncResult<Integer> as = new AsyncResult<Integer>(result);
		logger.debug("completed");
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
	public int commandCcdShutterState(int ccdShutterState) throws Exception {
		//return extInfFactory.getCameraCommand().commandCcdShutterState(ccdShutterState);
		return ccdShutterState;
	}

	/**
	 * Command a specified reference beam to turn off or on<br/>
	 * Timeout: 60 sec
	 * 
	 * @param referenceBeamCommand
	 *            Number of the desired reference beam to turn on, 0 turns off all reference beams. Multiple reference beams can be on at
	 *            the same time. 0-9
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
	public Future<Integer> commandCcdControllerPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandCcdControllerPowerState(ccdPowerState);
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
	public Future<Integer> commandFineTiltMirrorX(Integer fineTiltCmdX) throws Exception {

		Future<Integer> xFuture = cameraMgmtAsync.commandFineTiltMirrorX(fineTiltCmdX);

		while (!xFuture.isDone()) {
			Thread.sleep(300);
		}
		Integer result = new Integer(xFuture.get());
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandFineTiltMirrorY(Integer fineTiltCmdY) throws Exception {

		Future<Integer> yFuture = cameraMgmtAsync.commandFineTiltMirrorY(fineTiltCmdY);

		while (!yFuture.isDone()) {
			Thread.sleep(300);
		}
		Integer result = new Integer(yFuture.get());
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Point> commandCoarseTiltMirror(Point coarseTiltCmd) throws Exception {
		Future<Integer> xFuture = cameraMgmtAsync.commandCoarseTiltMirrorX((int) coarseTiltCmd.x);
		Future<Integer> yFuture = cameraMgmtAsync.commandCoarseTiltMirrorY((int) coarseTiltCmd.y);

		while (!xFuture.isDone() || !yFuture.isDone()) {
			logger.debug("waiting on coarse Tilt Mirror xDone = " + xFuture.isDone() + ", yDone = " + yFuture.isDone());
			Thread.sleep(300);
		}
		Point result = new Point(xFuture.get(), yFuture.get());
		return new AsyncResult<Point>(result);
	}

	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorX(Integer coarseTiltCmdX) throws Exception {
		Future<Integer> xFuture = cameraMgmtAsync.commandCoarseTiltMirrorX((int) coarseTiltCmdX);

		while (!xFuture.isDone()) {
			logger.debug("waiting on coarse Tilt Mirror xDone = " + xFuture.isDone());
			Thread.sleep(300);
		}
		Integer result = new Integer(xFuture.get());
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorY(Integer coarseTiltCmdY) throws Exception {
		Future<Integer> yFuture = cameraMgmtAsync.commandCoarseTiltMirrorY((int) coarseTiltCmdY);

		while (!yFuture.isDone()) {
			logger.debug("waiting on coarse Tilt Mirror yDone = " + yFuture.isDone());
			Thread.sleep(300);
		}
		Integer result = new Integer(yFuture.get());
		return new AsyncResult<Integer>(result);
	}


	
	
	
	public void addCameraQueryListener(int deviceCode, CameraQueryListener l) throws Exception {
		extInfFactory.getCameraCommand().addCameraQueryListener(deviceCode, l);
	}


	public void addCameraStatusListener(CameraStatusListener l) throws Exception {
		extInfFactory.getCameraCommand().addCameraStatusListener(l);
	}

	@Asynchronous
	public Future<Integer> initializeCamera() throws Exception {
		extInfFactory.getCameraCommand().initializeCamera();
		return new AsyncResult<Integer>(1);
	}

	@Asynchronous
	public Future<Integer> stowCamera() throws Exception {
		extInfFactory.getCameraCommand().stowCamera();
		return new AsyncResult<Integer>(1);
	}


	@Asynchronous
	public Future<Integer> commandOverallPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandOverallPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandGalilPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandGalilPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> setPurgeAirState(int purgeAirState) throws Exception {
		extInfFactory.getCameraCommand().setPurgeAirState(purgeAirState);
		
		return new AsyncResult<Integer>(purgeAirState);
	}


	
	
	
	
	/**
	 * Refreshes the camera status values into the {@link Instrument} state of the {@link PhysicalModel}.
	 */
	@Asynchronous
	public Future<Boolean> refreshStatus() throws Exception {

		try {
		
			Instrument instrument = physicalModel.getInstrument();
			
			CameraStatus cameraStatus = queryCameraStatus();
	
			instrument.updateState(cameraStatus);
			
			//System.out.println("updating physical model with camera status: " + cameraStatus);

			// set heartbeat status to true
			extInfConfigState.getExtInfConnectConfig().setCameraHeartbeatStatus(true);
			
		} catch (Throwable t) {
			// set heartbeat status to false
			extInfConfigState.getExtInfConnectConfig().setCameraHeartbeatStatus(false);
			logger.error(t.getMessage());
			t.printStackTrace();
		}
				
		return new AsyncResult<Boolean>(true);
	}
	
	





}
