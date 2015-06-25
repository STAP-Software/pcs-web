package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.InstrumentInterface;
import org.tmt.aps.peas.extinf.TimeoutException;

public class InstrumentCommandSimulator implements InstrumentInterface {

	Logger logger = Logger.getLogger(this.getClass());
	
	CcdCommandSimulator ccdCommandSimulator;
	CameraCommandSimulator cameraCommandSimulator;
	
	public InstrumentCommandSimulator() {
		ccdCommandSimulator = new CcdCommandSimulator();
		cameraCommandSimulator = new CameraCommandSimulator();
	}
	
	@Override
	public int[][] getImage() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		return ccdCommandSimulator.getImage();
	}

	@Override
	public void fastWipe() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		ccdCommandSimulator.fastWipe();
	}

	@Override
	public void wipeOn() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		ccdCommandSimulator.wipeOn();
	}

	@Override
	public CameraQueryResult queryCamera(int deviceCode) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.queryCamera(deviceCode);
	}

	
	@Override
	public void resetCamera() throws CommunicationException, CommandFailureException, RemoteException {
		logger.info(MessageGenerator.generateMessage("command.start", "resetCamera::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "resetCamera::SIMULATOR"));
		
	}

	@Override
	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandPupilMask(pupilMaskPosition);
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandFilterWheel(filterWheelPosition);
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandXTiltPlate(xTiltPlatePosition);
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandYTiltPlate(yTiltPlatePosition);
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		return cameraCommandSimulator.commandTwoPositionDevice(twoPositionDevicePosition);
	}

	@Override
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		cameraCommandSimulator.commandCcdShutterExposure(ccdExposureTime);
	}

	@Override
	public int commandCcdShutterState(int ccdShutterState) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandCcdShutterState(ccdShutterState);
	}

	@Override
	public void commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		cameraCommandSimulator.commandReferenceBeamState(referenceBeamCommand);
	}

	@Override
	public int commandCcdPowerState(int ccdPowerState) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		return cameraCommandSimulator.commandCcdPowerState(ccdPowerState);
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		return cameraCommandSimulator.commandXSteeringMirror(xSteeringMirrorPosition);
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		return cameraCommandSimulator.commandYSteeringMirror(ySteeringMirrorPosition);
	}

	@Override
	public void setGain(int channel, double gain) throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "setGain::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "setGain::SIMULATOR"));
		
	}

	@Override
	public void setOffset(int channel, double offset) throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "setOffset::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "setOffset::SIMULATOR"));
		
	}

	@Override
	public int getImageWidth() throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "getImageWidth::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "getImageWidth::SIMULATOR"));
		return 1025;
	}

	@Override
	public int getImageHeight() throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "getImageHeight::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "getImageHeight::SIMULATOR"));
		return 1025;
	}

	@Override
	public double getPlateScale() throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "getPlateScale::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "getPlateScale::SIMULATOR"));
		return 1.234;
	}

	@Override
	public void setBinning(int x, int y) throws CommandFailureException, CommunicationException {
		logger.info(MessageGenerator.generateMessage("command.start", "setBinning::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "setBinning::SIMULATOR"));
		
	}

	@Override
	public int[][] getImage(double exposureTime, boolean useShutter) throws CommandFailureException, CommunicationException,
			TimeoutException {
		return ccdCommandSimulator.getImage();
	}

	@Override
	public CameraStatus getCameraStatus() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		logger.info(MessageGenerator.generateMessage("command.start", "getCameraStatus::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.sucesss", "getCameraStatus::SIMULATOR"));
		return null;
	}



}
