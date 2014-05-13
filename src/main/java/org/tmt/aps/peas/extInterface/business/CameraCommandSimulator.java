package org.tmt.aps.peas.extInterface.business;

import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class CameraCommandSimulator implements CameraCommand {

	public CameraCommandSimulator() {

	}

	@Override
	public CameraQueryResult queryCamera(int deviceCode) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return pupilMaskPosition;
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return filterWheelPosition;
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return xTiltPlatePosition;
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return yTiltPlatePosition;
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException {
		// TODO Auto-generated method stub
		return twoPositionDevicePosition;
	}

	@Override
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int commandCcdShutterState(int ccdShutterState) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return ccdShutterState;
	}

	@Override
	public void commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int commandCcdPowerState(int ccdPowerState) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return ccdPowerState;
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return xSteeringMirrorPosition;
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return ySteeringMirrorPosition;
	}



}
