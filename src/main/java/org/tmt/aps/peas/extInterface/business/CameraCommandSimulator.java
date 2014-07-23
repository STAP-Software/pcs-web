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
		
		int state = (int)(System.currentTimeMillis() % 6) + 1;
		int tState = (int)((System.currentTimeMillis() % 200)/10.0);
		
		switch (deviceCode) {
		
		case DEVICE_CODE_CCD_POWER:
			return new CameraQueryResult(1,0);
		case DEVICE_CODE_CCD_SHUTTER:
			return new CameraQueryResult(0,0);
		case DEVICE_CODE_CCD_TEMPERATURE:
			return new CameraQueryResult(0,103);
		case DEVICE_CODE_ELECTONICS_BOX_TEMPERATURE:
			return new CameraQueryResult(0,tState);
		case DEVICE_CODE_FILTER_WHEEL: 
			return new CameraQueryResult(1, state);
		case DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE:
			return new CameraQueryResult(0,tState);
		case DEVICE_CODE_PUPIL_WHEEL:
			System.out.println("pupil wheel = " + state);
			return new CameraQueryResult(1,state);
		case DEVICE_CODE_REFERENCE_BEAMS:
			return new CameraQueryResult(0,0);
		case DEVICE_CODE_TWO_POSITION_DEVICE:
			return new CameraQueryResult(0,0);
		case DEVICE_CODE_X_STEERING_MIRROR:
			return new CameraQueryResult(1, tState);
		case DEVICE_CODE_Y_STEERING_MIRROR:
			return new CameraQueryResult(1, tState);
		case DEVICE_CODE_X_TILT_PLATE:
			return new CameraQueryResult(1, tState);
		case DEVICE_CODE_Y_TILT_PLATE:
			return new CameraQueryResult(1, tState);
		default:
			return new CameraQueryResult(0,0);
		}
		
	}

	@Override
	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			
		}
		return pupilMaskPosition;
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		return filterWheelPosition;
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			
		}
		return xTiltPlatePosition;
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			
		}
		return yTiltPlatePosition;
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		return twoPositionDevicePosition;
	}

	@Override
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(ccdExposureTime * 1000);
		} catch (Exception e) {
			
		}
		
	}

	@Override
	public int commandCcdShutterState(int ccdShutterState) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return ccdShutterState;
	}

	@Override
	public void commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException {
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public int commandCcdPowerState(int ccdPowerState) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		return ccdPowerState;
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		return xSteeringMirrorPosition;
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(750);
		} catch (Exception e) {
			
		}
		return ySteeringMirrorPosition;
	}



}
