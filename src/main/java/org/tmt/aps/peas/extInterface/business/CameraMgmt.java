/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

@Stateless
public class CameraMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	ExtInfFactory extInfFactory;

	// All ACS Commands should be defined here


	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandPupilMask(pupilMaskPosition);
	}

	/**
	 * Move filter wheel  to specified location<br/>
	 * Timeout: 60 sec
	 * @param filterWheelPosition filter wheel position	1-6	
	 * @return achieved Filter Wheel Position 
	 */
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {	
		return extInfFactory.getCameraCommand().commandFilterWheel(filterWheelPosition);
	}
	
	/**
	 * Move X axis of tilt plate to specified location<br/>
	 * Timeout: 90 sec
	 * @param xTiltPlatePosition Desired X tilt plate position	-5000 to 5000 (microns)	
	 * @return achieved X Tilt Plate Position (microns) 
	 */
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandXTiltPlate(xTiltPlatePosition);
	}
	
	/**
	 * Move Y axis of tilt plate to specified location<br/>
	 * Timeout: 90 sec
	 * @param yTiltPlatePosition Desired Y tilt plate position	-5000 to 5000 (microns)	
	 * @return achieved Y Tilt Plate Position (microns) 
	 */
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandYTiltPlate(yTiltPlatePosition);
	}
		
	/**
	 * Extend (into the beam) or Retract (out of the beam) the 45 Degree mirror (K1) or beam block (K2)<br/>
	 * Timeout: 60 sec
	 * @param twoPositionDevicePosition Desired mechanism position (0 = retracted,1=extended)	0 or 1		
	 * @return achieved Two Position Device Position (0 = retracted,1=extended)
	 */
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandTwoPositionDevice(twoPositionDevicePosition);
	}
	
	/**
	 * Open the shutter for the specified period of time<br/>
	 * Timeout: ccdExposureTime/1000 + 10 sec
	 * @param ccdExposureTime Desired exposure time	100 to 360000	milliseconds		
	 * @return  
	 */
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException {
		extInfFactory.getCameraCommand().commandCcdShutterExposure(ccdExposureTime);
	}
	
	/**
	 * Command the CCD shutter to open or close<br/>
	 * Timeout: 60 sec
	 * @param ccdShuterState Desired CCD shutter state (0 = closed, 1 = open)	0 or 1		
	 * @return achived CCD Shuter State (0 = closed, 1 = open)
	 */
	public int commandCcdShutterState(int ccdShuterState) throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandCcdShutterState(ccdShuterState);
	}

	/**
	 * Command a specified reference beam to turn off or on<br/>
	 * Timeout: 60 sec
	 * @param referenceBeamCommand Number of the desired reference beam to turn on, 0 turns off all reference beams. Multiple reference beams can be on at the same time.	0-9		
	 * @return  
	 */
	public void commandReferenceBeamState(int referenceBeamCommand)	throws CommunicationException, TimeoutException, CommandFailureException {
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
	public int commandCcdPowerState(int ccdPowerState)  throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandCcdPowerState(ccdPowerState);
	}

	/**
	 * Move X axis of steering mirror to specified location<br/>
	 * Timeout: 90 sec
	 * @param xSteeringMirrorPosition Desired X steering mirror position -5000 to 5000 microns		
	 * @return achieved X Steering Mirror Position (microns)
	 */
	public int commandXSteeringMirror(int xSteeringMirrorPosition)  throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandXSteeringMirror(xSteeringMirrorPosition);
	}
							
	/**
	 * Move Y axis of steering mirror to specified location<br/>
	 * Timeout: 90 sec
	 * @param ySteeringMirrorPosition Desired Y steering mirror position -5000 to 5000 microns		
	 * @return achieved Y Steering Mirror Position (microns)
	 */
	public int commandYSteeringMirror(int ySteeringMirrorPosition)  throws CommunicationException, TimeoutException, CommandFailureException {
		return extInfFactory.getCameraCommand().commandYSteeringMirror(ySteeringMirrorPosition);
	}
							


}
