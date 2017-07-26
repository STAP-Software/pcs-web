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
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;
import org.tmt.aps.peas.extinf.VoltageListener;
import org.tmt.aps.peas.extinf.Voltages;
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


	
	
	
	public void addCameraQueryListener(int deviceCode, CameraQueryListener l) throws Exception {
		extInfFactory.getCameraCommand().addCameraQueryListener(deviceCode, l);
	}

	public void addCameraQueryListener(int deviceCode, int period, CameraQueryListener l) throws Exception {
		extInfFactory.getCameraCommand().addCameraQueryListener(deviceCode, period, l);
		
	}

	public void addPeriodicCameraQueryListener(int deviceCode, int period, CameraQueryListener l) throws Exception {
		extInfFactory.getCameraCommand().addPeriodicCameraQueryListener(deviceCode, period, l);
	}

	public void removeCameraQueryListener(int deviceCode, CameraQueryListener l) throws Exception {
		extInfFactory.getCameraCommand().removeCameraQueryListener(deviceCode, l);
	}

	public void addCameraStatusListener(CameraStatusListener l) throws Exception {
		extInfFactory.getCameraCommand().addCameraStatusListener(l);
	}

	public void addCameraStatusListener(CameraStatusListener l, int period) throws Exception {
		extInfFactory.getCameraCommand().addCameraStatusListener(l, period);
	}

	public void addPeriodicCameraStatusListener(CameraStatusListener l, int period) throws Exception {
		extInfFactory.getCameraCommand().addPeriodicCameraStatusListener(l, period);
	}

	public void removeCameraStatusListener(CameraStatusListener l) throws Exception {
		extInfFactory.getCameraCommand().removeCameraStatusListener(l);
	}


	@Asynchronous
	public Future<Integer> commandOverallPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandOverallPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandFanPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandFanPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandGalilPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandGalilPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> commandPowerSuppliesPowerState(int ccdPowerState) throws Exception {
		int result =  extInfFactory.getCameraCommand().commandPowerSuppliesPowerState(ccdPowerState);
		return new AsyncResult<Integer>(result);
	}

	@Asynchronous
	public Future<Integer> setPurgeAirState(int purgeAirState) throws Exception {
		extInfFactory.getCameraCommand().setPurgeAirState(purgeAirState);
		
		return new AsyncResult<Integer>(purgeAirState);
	}


	public Voltages getVoltages() throws Exception {
		
		return extInfFactory.getCameraCommand().getVoltages();
	}


	public void addVoltageListener(VoltageListener l) throws Exception {
		
		extInfFactory.getCameraCommand().addVoltageListener(l);
		
	}


	public void addVoltageListener(VoltageListener l, int period) throws Exception  {
		
		extInfFactory.getCameraCommand().addVoltageListener(l, period);
		
	}


	public void addPeriodicVoltageListener(VoltageListener l, int period) throws Exception {
		
		extInfFactory.getCameraCommand().addPeriodicVoltageListener(l, period);
		
	}


	public void removeVoltageListener(VoltageListener l) throws Exception  {
		
		extInfFactory.getCameraCommand().removeVoltageListener(l);
		
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

			// set heartbeat status to true
			extInfConfigState.getExtInfConnectConfig().setCameraHeartbeatStatus(true);
			
		} catch (Throwable t) {
			// set heartbeat status to false
			extInfConfigState.getExtInfConnectConfig().setCameraHeartbeatStatus(false);
		}
				
		return new AsyncResult<Boolean>(true);
	}



}
