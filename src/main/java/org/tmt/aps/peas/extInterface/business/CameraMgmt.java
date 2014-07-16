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
import org.tmt.aps.peas.extinf.CameraQueryResult;

@Stateless
public class CameraMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	ExtInfFactory extInfFactory;

	// All Camera Commands should be defined here


	public CameraQueryResult queryCamera(int deviceCode) throws Exception {
		return extInfFactory.getCameraCommand().queryCamera(deviceCode);
	}

	@Asynchronous
	public Future<Integer> commandPupilMask(int pupilMaskPosition) throws Exception {
		System.out.println("in commandPupilMask");
		int result = extInfFactory.getCameraCommand().commandPupilMask(pupilMaskPosition);
		System.out.println("returning");
		AsyncResult as = new AsyncResult<Integer>(result);
		System.out.println("completed");
		return as;
	}

	/**
	 * Move filter wheel  to specified location<br/>
	 * Timeout: 60 sec
	 * @param filterWheelPosition filter wheel position	1-6	
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
	 * @param twoPositionDevicePosition Desired mechanism position (0 = retracted,1=extended)	0 or 1		
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
	 * @param ccdExposureTime Desired exposure time	100 to 360000	milliseconds		
	 * @return  
	 */
	public void commandCcdShutterExposure(int ccdExposureTime) throws Exception {
		extInfFactory.getCameraCommand().commandCcdShutterExposure(ccdExposureTime);
	}
	
	/**
	 * Command the CCD shutter to open or close<br/>
	 * Timeout: 60 sec
	 * @param ccdShuterState Desired CCD shutter state (0 = closed, 1 = open)	0 or 1		
	 * @return achived CCD Shuter State (0 = closed, 1 = open)
	 */
	public int commandCcdShutterState(int ccdShuterState) throws Exception {
		return extInfFactory.getCameraCommand().commandCcdShutterState(ccdShuterState);
	}

	/**
	 * Command a specified reference beam to turn off or on<br/>
	 * Timeout: 60 sec
	 * @param referenceBeamCommand Number of the desired reference beam to turn on, 0 turns off all reference beams. Multiple reference beams can be on at the same time.	0-9		
	 * @return  
	 */
	public void commandReferenceBeamState(int referenceBeamCommand)	throws Exception {
		extInfFactory.getCameraCommand().commandReferenceBeamState(referenceBeamCommand);
	}

	/**
	 * Turn on or off the CCD Power <br/>
	 * Timeout: 60 sec <br/>
	 * On K2 this also turns on the thermal electrical cooler, but on K1 the thermal electrical cooler is a manual switch.
	 * 
	 * @param ccdPowerState Desired CCD power state (0 = off, 1 = on)	0 or 1		
	 * @return achieved CCD Power State (0 = off, 1 = on)
	 */
	public int commandCcdPowerState(int ccdPowerState)  throws Exception {
		return extInfFactory.getCameraCommand().commandCcdPowerState(ccdPowerState);
	}				

	@Asynchronous
	public Future<Point> commandFineTiltMirror(Point fineTiltCmd) throws Exception {
		Future<Integer> xFuture = commandFineTiltMirrorX((int)fineTiltCmd.x);
		Future<Integer> yFuture = commandFineTiltMirrorY((int)fineTiltCmd.y);
		
		while (!xFuture.isDone() || !yFuture.isDone()) {
			Thread.sleep(300);
		}
		Point result = new Point(xFuture.get(), yFuture.get());	
		return new AsyncResult<Point>(result);
	}
	
	@Asynchronous
	public Future<Integer> commandFineTiltMirrorX(int cmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXTiltPlate(cmd);
		return new AsyncResult<Integer>(xValue);
	}
	
	@Asynchronous
	public Future<Integer> commandFineTiltMirrorY(int cmd) throws Exception {
		int yValue = extInfFactory.getCameraCommand().commandYTiltPlate(cmd);
		return new AsyncResult<Integer>(yValue);
	}
	
	
	@Asynchronous
	public Future<Point> commandCoarseTiltMirror(Point coarseTiltCmd) throws Exception {
		Future<Integer> xFuture = commandCoarseTiltMirrorX((int)coarseTiltCmd.x);
		Future<Integer> yFuture = commandCoarseTiltMirrorY((int)coarseTiltCmd.y);
		
		while (!xFuture.isDone() || !yFuture.isDone()) {
			Thread.sleep(300);
		}
		Point result = new Point(xFuture.get(), yFuture.get());	
		return new AsyncResult<Point>(result);
	}
	
	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorX(int cmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXSteeringMirror(cmd);
		return new AsyncResult<Integer>(xValue);
	}
	
	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorY(int cmd) throws Exception {
		int yValue = extInfFactory.getCameraCommand().commandYSteeringMirror(cmd);
		return new AsyncResult<Integer>(yValue);
	}


}
