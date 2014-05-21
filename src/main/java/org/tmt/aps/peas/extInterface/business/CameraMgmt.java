/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

@Stateless
public class CameraMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	ExtInfFactory extInfFactory;

	// All Camera Commands should be defined here


	public CameraQueryResult queryCamera(int deviceCode) throws Exception {
		return extInfFactory.getCameraCommand().queryCamera(deviceCode);
	}

	public int commandPupilMask(int pupilMaskPosition) throws Exception {
		return extInfFactory.getCameraCommand().commandPupilMask(pupilMaskPosition);
	}

	/**
	 * Move filter wheel  to specified location<br/>
	 * Timeout: 60 sec
	 * @param filterWheelPosition filter wheel position	1-6	
	 * @return achieved Filter Wheel Position 
	 */
	public int commandFilterWheel(int filterWheelPosition) throws Exception {	
		return extInfFactory.getCameraCommand().commandFilterWheel(filterWheelPosition);
	}
		
	/**
	 * Extend (into the beam) or Retract (out of the beam) the 45 Degree mirror (K1) or beam block (K2)<br/>
	 * Timeout: 60 sec
	 * @param twoPositionDevicePosition Desired mechanism position (0 = retracted,1=extended)	0 or 1		
	 * @return achieved Two Position Device Position (0 = retracted,1=extended)
	 */
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws Exception {
		return extInfFactory.getCameraCommand().commandTwoPositionDevice(twoPositionDevicePosition);
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

							

	public Point commandFineTiltMirror(Point fineTiltCmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXTiltPlate((int)fineTiltCmd.x);
		int yValue = extInfFactory.getCameraCommand().commandYTiltPlate((int)fineTiltCmd.y);
		
		return new Point(xValue, yValue);
		
	}

	public Point commandCoarseTiltMirror(Point coarseTiltCmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXSteeringMirror((int)coarseTiltCmd.x);
		int yValue = extInfFactory.getCameraCommand().commandYSteeringMirror((int)coarseTiltCmd.y);
		
		return new Point(xValue, yValue);
		
	}
							


}
