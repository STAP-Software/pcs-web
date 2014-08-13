package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;

import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.InstrumentInterface;
import org.tmt.aps.peas.extinf.TimeoutException;

public class InstrumentCommandSimulator implements InstrumentInterface {

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
	public int commandPupilMask(int pupilMaskPosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandPupilMask(pupilMaskPosition);
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandFilterWheel(filterWheelPosition);
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandXTiltPlate(xTiltPlatePosition);
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandYTiltPlate(yTiltPlatePosition);
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandTwoPositionDevice(twoPositionDevicePosition);
	}

	@Override
	public void commandCcdShutterExposure(int ccdExposureTime) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		cameraCommandSimulator.commandCcdShutterExposure(ccdExposureTime);
	}

	@Override
	public int commandCcdShutterState(int ccdShutterState) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandCcdShutterState(ccdShutterState);
	}

	@Override
	public void commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		cameraCommandSimulator.commandReferenceBeamState(referenceBeamCommand);
	}

	@Override
	public int commandCcdPowerState(int ccdPowerState) throws CommunicationException, TimeoutException, CommandFailureException,
			RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandCcdPowerState(ccdPowerState);
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandXSteeringMirror(xSteeringMirrorPosition);
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException,
			CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		return cameraCommandSimulator.commandYSteeringMirror(ySteeringMirrorPosition);
	}

	@Override
	public void setGain(int channel, double gain) throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOffset(int channel, double offset) throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getImageWidth() throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		return 1025;
	}

	@Override
	public int getImageHeight() throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		return 1025;
	}

	@Override
	public double getPlateScale() throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		return 1.234;
	}

	@Override
	public void setBinning(int x, int y) throws CommandFailureException, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[][] getImage(double exposureTime, boolean useShutter) throws CommandFailureException, CommunicationException,
			TimeoutException {
		// TODO Auto-generated method stub
		return ccdCommandSimulator.getImage();
	}

	@Override
	public CameraStatus getCameraStatus() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}



}
