package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class CameraCommandSimulator implements CameraCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	public CameraCommandSimulator() {

	}

	
	@Override
	public CameraQueryResult queryCamera(int deviceCode) throws CommunicationException, TimeoutException, CommandFailureException {
		
		logger.info(MessageGenerator.generateMessage("command.start", "queryCamera::SIMULATOR"));

		
		int state = (int)(System.currentTimeMillis() % 6) + 1;
		int tState = (int)((System.currentTimeMillis() % 200)/10.0);
		
		CameraQueryResult result = null;
		switch (deviceCode) {
		
		case DEVICE_CODE_CCD_POWER:
			result =  new CameraQueryResult(1,0);
			break;
		case DEVICE_CODE_CCD_SHUTTER:
			result =  new CameraQueryResult(0,0);
			break;
		case DEVICE_CODE_CCD_TEMPERATURE:
			result =  new CameraQueryResult(0,103);
			break;
		case DEVICE_CODE_ELECTONICS_BOX_TEMPERATURE:
			result =  new CameraQueryResult(0,tState);
			break;
		case DEVICE_CODE_FILTER_WHEEL: 
			result =  new CameraQueryResult(1, state);
			break;
		case DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE:
			result =  new CameraQueryResult(0,tState);
			break;
		case DEVICE_CODE_PUPIL_WHEEL:
			result =  new CameraQueryResult(1,state);
			break;
		case DEVICE_CODE_REFERENCE_BEAMS:
			result =  new CameraQueryResult(0,0);
			break;
		case DEVICE_CODE_TWO_POSITION_DEVICE:
			result =  new CameraQueryResult(0,0);
			break;
		case DEVICE_CODE_X_STEERING_MIRROR:
			result =  new CameraQueryResult(1, tState);
			break;
		case DEVICE_CODE_Y_STEERING_MIRROR:
			result =  new CameraQueryResult(1, tState);
			break;
		case DEVICE_CODE_X_TILT_PLATE:
			result =  new CameraQueryResult(1, tState);
			break;
		case DEVICE_CODE_Y_TILT_PLATE:
			result =  new CameraQueryResult(1, tState);
			break;
		default:
			result =  new CameraQueryResult(0,0);
		}
		
		logger.info(MessageGenerator.generateMessage("command.success", "queryCamera::SIMULATOR"));

		return result;
	}
	
	

	@Override
	public void resetCamera() throws CommunicationException, CommandFailureException, RemoteException {
		logger.info(MessageGenerator.generateMessage("command.start", "resetCamera::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "resetCamera::SIMULATOR"));
		
	}


	@Override
	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		
		logger.info(MessageGenerator.generateMessage("command.start", "commandPupilMask::SIMULATOR"));
		Utils.waitFor(10000);
		logger.info(MessageGenerator.generateMessage("command.success", "commandPupilMask::SIMULATOR"));
		return pupilMaskPosition;
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandFilterWheel::SIMULATOR"));
		Utils.waitFor(750);
		logger.info(MessageGenerator.generateMessage("command.success", "commandFilterWheel::SIMULATOR"));
		return filterWheelPosition;
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandXTiltPlate::SIMULATOR"));
		Utils.waitFor(10000);
		logger.info(MessageGenerator.generateMessage("command.success", "commandXTiltPlate::SIMULATOR"));
		//throw new CommandFailureException("Tilt plate command failed");
		return xTiltPlatePosition;
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandYTiltPlate::SIMULATOR"));
		Utils.waitFor(10000);
		logger.info(MessageGenerator.generateMessage("command.success", "commandYTiltPlate::SIMULATOR"));
		return yTiltPlatePosition;
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandTwoPositionDevice::SIMULATOR"));
		Utils.waitFor(750);
		logger.info(MessageGenerator.generateMessage("command.success", "commandTwoPositionDevice::SIMULATOR"));
		return twoPositionDevicePosition;
	}

	@Override
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandCcdShutterExposure::SIMULATOR"));
		Utils.waitFor(ccdExposureTime);
		logger.info(MessageGenerator.generateMessage("command.success", "commandCcdShutterExposure::SIMULATOR"));
	}

	@Override
	public int commandCcdShutterState(int ccdShutterState) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandCcdShutterState::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "commandCcdShutterState::SIMULATOR"));
		return ccdShutterState;
	}

	@Override
	public void commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandReferenceBeamState::SIMULATOR"));
		Utils.waitFor(750);		
		logger.info(MessageGenerator.generateMessage("command.success", "commandReferenceBeamState::SIMULATOR"));
	}

	@Override
	public int commandCcdPowerState(int ccdPowerState) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandCcdPowerState::SIMULATOR"));
		Utils.waitFor(750);
		logger.info(MessageGenerator.generateMessage("command.success", "commandCcdPowerState::SIMULATOR"));
		return ccdPowerState;
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandXSteeringMirror::SIMULATOR"));
		Utils.waitFor(750);
		logger.info(MessageGenerator.generateMessage("command.success", "commandXSteeringMirror::SIMULATOR"));
		return xSteeringMirrorPosition;
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandYSteeringMirror::SIMULATOR"));
		Utils.waitFor(750);
		logger.info(MessageGenerator.generateMessage("command.success", "commandYSteeringMirror::SIMULATOR"));
		return ySteeringMirrorPosition;
	}


	@Override
	public CameraStatus getCameraStatus() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {

		logger.trace(MessageGenerator.generateMessage("command.start", "getCameraStatus::SIMULATOR"));

		CameraStatus cameraStatus = new CameraStatus();
		cameraStatus.benchTemp = 12.3;
		cameraStatus.boxTemp = 33.0;
		cameraStatus.ccdPowerState = CameraCommand.ON;
		cameraStatus.ccdTemp = -2.3;
		cameraStatus.filterWheelPos = 2;
		cameraStatus.prismWheelPos = 3;
		cameraStatus.refBeamPos = 2;
		cameraStatus.shutterState = CameraCommand.CLOSED;
		cameraStatus.twoPosDevPos = CameraCommand.EXTENDED;
		
		cameraStatus.steeringMirrorX = 44;
		cameraStatus.steeringMirrorY = -55;
		cameraStatus.tiltPlateX = 301;
		cameraStatus.tiltPlateY = -404;

		cameraStatus.filterWheelIsInTransit = false;
		cameraStatus.prismWheelIsInTransit = false;
		Random random = new Random();
		
		cameraStatus.steeringMirrorX = random.nextInt(100);
		cameraStatus.steeringMirrorY = random.nextInt(100);
				
		//logger.debug("cameraStatus.steeringMirrorX = " + cameraStatus.steeringMirrorX);
		
		cameraStatus.steeringMirrorXIsInTransit = false;
		
		logger.trace(MessageGenerator.generateMessage("command.success", "getCameraStatus::SIMULATOR"));
		return cameraStatus;
	}



}
