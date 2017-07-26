package org.tmt.aps.peas.extInterface.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;
import org.tmt.aps.peas.extinf.VoltageListener;
import org.tmt.aps.peas.extinf.Voltages;

/**
 * PCS Camera command simulator.  Generates dummy values for queries.
 * @author smichaels
 */
public class CameraCommandSimulator implements CameraCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	Map<Integer, List<CameraQueryListener>> deviceCodeToCameraChangeListenerList;
	Map<Integer, List<CameraQueryListener>> deviceCodeToCameraChangePeriodicListenerList;
	Map<Integer, List<CameraQueryListener>> deviceCodeToCameraPeriodicListenerList;
	List<CameraStatusListener> cameraStatusChangeListenerList;
	List<CameraStatusListener> cameraStatusChangePeriodicListenerList;
	List<CameraStatusListener> cameraStatusPeriodicListenerList;
	List<VoltageListener> voltageChangeListenerList;
	List<VoltageListener> voltageChangePeriodicListenerList;
	List<VoltageListener> voltagePeriodicListenerList;
	
	
	private int overallPowerState;
	private int ccdControllerPowerState;
	private int networkControllerPowerState;
	private int fanPowerState;
	private int galilPowerState;
	private int powerSuppliesPowerState;
	private int purgeAirState;

	private Voltages voltages;
	
	private int pupilWheelPosition;
	private int filterWheelPosition;
	private int xTiltPlatePosition;
	private int yTiltPlatePosition;
	private int twoPositionDevicePosition;
	private int ccdShutterState;
	private int referenceBeamCommand;
	private int xSteeringMirrorPosition;
	private int ySteeringMirrorPosition;
	
	
	
	
	
	public CameraCommandSimulator() throws Exception {
	
		deviceCodeToCameraChangeListenerList = new HashMap<Integer, List<CameraQueryListener>>();
		deviceCodeToCameraChangePeriodicListenerList = new HashMap<Integer, List<CameraQueryListener>>();
		deviceCodeToCameraPeriodicListenerList = new HashMap<Integer, List<CameraQueryListener>>();
		cameraStatusChangeListenerList = new ArrayList<CameraStatusListener>();
		cameraStatusChangePeriodicListenerList = new ArrayList<CameraStatusListener>();
		cameraStatusPeriodicListenerList = new ArrayList<CameraStatusListener>();
		voltageChangeListenerList = new ArrayList<VoltageListener>();
		voltageChangePeriodicListenerList = new ArrayList<VoltageListener>();
		voltagePeriodicListenerList = new ArrayList<VoltageListener>();
		
		voltages = new Voltages();
		updateVoltages();
		
		resetCamera();
	}

	
	private void updateVoltages() {
		
		Voltages tempVoltages = new Voltages();
		
		tempVoltages.setVoltage(Voltages.CAMERA_12_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.CAMERA_5_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.CAMERA_N12_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.LED_DRAW, 0.0);
		tempVoltages.setVoltage(Voltages.REFBEAM_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.VICOR_12_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.VICOR_5_SUPPLY, 0.0);
		tempVoltages.setVoltage(Voltages.STANDBY_SUPPLY, 0.0);
	
		
		if (ccdControllerPowerState == 1)
			tempVoltages.setVoltage(Voltages.CAMERA_12_SUPPLY, 12.0);
		if (networkControllerPowerState == 1) 
			tempVoltages.setVoltage(Voltages.CAMERA_5_SUPPLY, 5.0);
		if (fanPowerState == 1) 
			tempVoltages.setVoltage(Voltages.CAMERA_N12_SUPPLY, 12.0);
		if (galilPowerState == 1) {
			tempVoltages.setVoltage(Voltages.LED_DRAW, 6.0);
			tempVoltages.setVoltage(Voltages.REFBEAM_SUPPLY, 7.0);	
		}
		if (powerSuppliesPowerState == 1)
			tempVoltages.setVoltage(Voltages.STANDBY_SUPPLY, 8.0);
		if (galilPowerState == 1) {
			tempVoltages.setVoltage(Voltages.VICOR_12_SUPPLY, 12.0);
			tempVoltages.setVoltage(Voltages.VICOR_5_SUPPLY, 5.0);
		}

		voltages = tempVoltages;
	}
		
	
	@Override
	public CameraQueryResult queryCamera(int deviceCode) throws CommunicationException, TimeoutException, CommandFailureException {
		
		logger.info(MessageGenerator.generateMessage("command.start", "queryCamera::SIMULATOR"));

				
		CameraQueryResult result = null;
		switch (deviceCode) {
		
		case DEVICE_CODE_CCD_POWER:
			result =  new CameraQueryResult(overallPowerState | ccdControllerPowerState, 0);
			break;
		case DEVICE_CODE_CCD_SHUTTER:
			result =  new CameraQueryResult(ccdShutterState, 0);
			break;
		case DEVICE_CODE_CCD_TEMPERATURE:
			result =  new CameraQueryResult(random(-33.0, -14.0));
			break;
		case DEVICE_CODE_ELECTONICS_BOX_TEMPERATURE:
			result =  new CameraQueryResult(random(0.0, 25.0));
			break;
		case DEVICE_CODE_FILTER_WHEEL: 
			result =  new CameraQueryResult(1, filterWheelPosition);
			break;
		case DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE:
			result =  new CameraQueryResult(random(-6.0, 14.0));
			break;
		case DEVICE_CODE_PUPIL_WHEEL:
			result =  new CameraQueryResult(1, pupilWheelPosition);
			break;
		case DEVICE_CODE_REFERENCE_BEAMS:
			result =  new CameraQueryResult(0, referenceBeamCommand);
			break;
		case DEVICE_CODE_TWO_POSITION_DEVICE:
			result =  new CameraQueryResult(twoPositionDevicePosition, 0);
			break;
		case DEVICE_CODE_X_STEERING_MIRROR:
			result =  new CameraQueryResult(1, xSteeringMirrorPosition);
			break;
		case DEVICE_CODE_Y_STEERING_MIRROR:
			result =  new CameraQueryResult(1, ySteeringMirrorPosition);
			break;
		case DEVICE_CODE_X_TILT_PLATE:
			result =  new CameraQueryResult(1, xTiltPlatePosition);
			break;
		case DEVICE_CODE_Y_TILT_PLATE:
			result =  new CameraQueryResult(1, yTiltPlatePosition);
			break;
		case DEVICE_CODE_ELECTRONICS_RH:
			result = new CameraQueryResult(random(10.0, 80.0));
			break;
		case DEVICE_CODE_FAN_POWER:
			result = null;
			break;
		case DEVICE_CODE_GALIL_POWER:
			result = null;
			break;
		case DEVICE_CODE_NETWORK_POWER:
			result = null;
			break;
		case DEVICE_CODE_OPTICAL_BENCH_RH:						
			result = new CameraQueryResult(random(10.0, 80.0));
			break;
		case DEVICE_CODE_POWER_SUPPLIES:
			result = null;
			break;
		case DEVICE_CODE_TEMPERATURE_INTERLOCK:
			result = new CameraQueryResult(randomBool() ? 1 : 0);
			break;
		default:
			result =  new CameraQueryResult(0,0);
		}
		
		// FIXME:these should have device codes
		//cameraStatus.glycolFlowStatus = randomBool();
		//cameraStatus.purgeIsActive = (purgeAirState == 1);

		
		
		
		logger.info(MessageGenerator.generateMessage("command.success", "queryCamera::SIMULATOR"));

		return result;
	}
	
	

	@Override
	public void resetCamera() throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "resetCamera::SIMULATOR"));
		
		pupilWheelPosition = 1;
		filterWheelPosition = 1;
		xTiltPlatePosition = 1;
		yTiltPlatePosition = 2;
		twoPositionDevicePosition = 1;
		ccdShutterState = 0;
		referenceBeamCommand = 0;
		xSteeringMirrorPosition = 1;
		ySteeringMirrorPosition = 2;

		logger.info(MessageGenerator.generateMessage("command.success", "resetCamera::SIMULATOR"));
		
	}


	@Override
	public int commandPupilMask(int pupilWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		
		logger.info(MessageGenerator.generateMessage("command.start", "commandPupilMask::SIMULATOR"));
		Utils.waitFor(1000);
		this.pupilWheelPosition = pupilWheelPosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandPupilMask::SIMULATOR"));
		return pupilWheelPosition;
	}

	@Override
	public int commandFilterWheel(int filterWheelPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandFilterWheel::SIMULATOR"));
		Utils.waitFor(750);
		this.filterWheelPosition = filterWheelPosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandFilterWheel::SIMULATOR"));
		return filterWheelPosition;
	}

	@Override
	public int commandXTiltPlate(int xTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandXTiltPlate::SIMULATOR"));
		Utils.waitFor(10000);
		this.xTiltPlatePosition = xTiltPlatePosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandXTiltPlate::SIMULATOR"));
		//throw new CommandFailureException("Tilt plate command failed");
		return xTiltPlatePosition;
	}

	@Override
	public int commandYTiltPlate(int yTiltPlatePosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandYTiltPlate::SIMULATOR"));
		Utils.waitFor(10000);
		this.yTiltPlatePosition = yTiltPlatePosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandYTiltPlate::SIMULATOR"));
		return yTiltPlatePosition;
	}

	@Override
	public int commandTwoPositionDevice(int twoPositionDevicePosition) throws CommunicationException, TimeoutException,
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandTwoPositionDevice::SIMULATOR"));
		Utils.waitFor(750);
		this.twoPositionDevicePosition = twoPositionDevicePosition;
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
		this.ccdShutterState = ccdShutterState;
		logger.info(MessageGenerator.generateMessage("command.success", "commandCcdShutterState::SIMULATOR"));
		return ccdShutterState;
	}

	@Override
	public int commandReferenceBeamState(int referenceBeamCommand) throws CommunicationException, TimeoutException,
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandReferenceBeamState::SIMULATOR"));
		Utils.waitFor(750);		
		this.referenceBeamCommand = referenceBeamCommand;
		logger.info(MessageGenerator.generateMessage("command.success", "commandReferenceBeamState::SIMULATOR"));
		
		return referenceBeamCommand;
	}

	@Override
	public int commandXSteeringMirror(int xSteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandXSteeringMirror::SIMULATOR"));
		Utils.waitFor(750);
		this.xSteeringMirrorPosition = xSteeringMirrorPosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandXSteeringMirror::SIMULATOR"));
		return xSteeringMirrorPosition;
	}

	@Override
	public int commandYSteeringMirror(int ySteeringMirrorPosition) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandYSteeringMirror::SIMULATOR"));
		Utils.waitFor(750);
		this.ySteeringMirrorPosition = ySteeringMirrorPosition;
		logger.info(MessageGenerator.generateMessage("command.success", "commandYSteeringMirror::SIMULATOR"));
		return ySteeringMirrorPosition;
	}


	@Override
	public void addCameraQueryListener(int deviceCode, CameraQueryListener l) throws IllegalArgumentException {
		// maintain a list of listeners for each device code
		List<CameraQueryListener> listenerList = deviceCodeToCameraChangeListenerList.get(deviceCode);
		if (listenerList == null) {
			listenerList = new ArrayList<CameraQueryListener>();
			deviceCodeToCameraChangeListenerList.put(deviceCode, listenerList);
		}
		listenerList.add(l);
	}


	@Override
	public void addCameraQueryListener(int deviceCode, int period, CameraQueryListener l) throws IllegalArgumentException {
		// maintain a list of listeners for each device code
		List<CameraQueryListener> listenerList = deviceCodeToCameraChangePeriodicListenerList.get(deviceCode);
		if (listenerList == null) {
			listenerList = new ArrayList<CameraQueryListener>();
			deviceCodeToCameraChangePeriodicListenerList.put(deviceCode, listenerList);
		}
		listenerList.add(l);
		
	}


	@Override
	public void addPeriodicCameraQueryListener(int deviceCode, int period, CameraQueryListener l) {

		List<CameraQueryListener> listenerList = deviceCodeToCameraPeriodicListenerList.get(deviceCode);
		if (listenerList == null) {
			listenerList = new ArrayList<CameraQueryListener>();
			deviceCodeToCameraPeriodicListenerList.put(deviceCode, listenerList);
		}
		listenerList.add(l);

	}


	@Override
	public void removeCameraQueryListener(int deviceCode, CameraQueryListener l) throws IllegalArgumentException {
		List<CameraQueryListener> changeListenerList = deviceCodeToCameraChangeListenerList.get(deviceCode);
		if (changeListenerList != null) {
			changeListenerList.remove(l);
		}
		List<CameraQueryListener> changePeriodicListenerList = deviceCodeToCameraChangePeriodicListenerList.get(deviceCode);
		if (changePeriodicListenerList != null) {
			changePeriodicListenerList.remove(l);
		}
		List<CameraQueryListener> periodicListenerList = deviceCodeToCameraPeriodicListenerList.get(deviceCode);
		if (periodicListenerList != null) {
			periodicListenerList.remove(l);
		}
		
		
	}


	@Override
	public void addCameraStatusListener(CameraStatusListener l) {
		cameraStatusChangeListenerList.add(l);
		
	}


	@Override
	public void addCameraStatusListener(CameraStatusListener l, int period) {
		cameraStatusChangePeriodicListenerList.add(l);
		
	}


	@Override
	public void addPeriodicCameraStatusListener(CameraStatusListener l, int period) throws IllegalArgumentException {
		cameraStatusPeriodicListenerList.add(l);
	}


	@Override
	public void removeCameraStatusListener(CameraStatusListener l) {
		cameraStatusChangeListenerList.remove(l);
		cameraStatusChangePeriodicListenerList.remove(l);
		cameraStatusPeriodicListenerList.remove(l);
	}


	@Override
	public int commandOverallPowerState(int powerState) throws IllegalArgumentException, CommunicationException, CommandFailureException {

		if (powerState != 0 && powerState != 1) {
			throw new IllegalArgumentException("passed power state " + powerState + " must be either zero or one"); 
		}
		overallPowerState = powerState;
		
		updateVoltages();
		
		return overallPowerState;
	}


	@Override
	public int commandCcdControllerPowerState(int powerState)
			throws IllegalArgumentException, CommunicationException, CommandFailureException {
		
		if (powerState != 0 && powerState != 1) {
			throw new IllegalArgumentException("passed power state " + powerState + " must be either zero or one"); 
		}
		ccdControllerPowerState = powerState;
		updateVoltages();

		return ccdControllerPowerState;
	}


	@Override
	public int commandFanPowerState(int powerState) throws IllegalArgumentException, CommunicationException, CommandFailureException {

		if (powerState != 0 && powerState != 1) {
			throw new IllegalArgumentException("passed power state " + powerState + " must be either zero or one"); 
		}
		fanPowerState = powerState;
		updateVoltages();

		return fanPowerState;
	}


	@Override
	public int commandGalilPowerState(int powerState) throws IllegalArgumentException, CommunicationException, CommandFailureException {

		if (powerState != 0 && powerState != 1) {
			throw new IllegalArgumentException("passed power state " + powerState + " must be either zero or one"); 
		}
		galilPowerState = powerState;
		updateVoltages();

		return galilPowerState;
	}


	@Override
	public int commandPowerSuppliesPowerState(int powerState) {

	    if (powerState != 0 && powerState != 1) {
			throw new IllegalArgumentException("passed power state " + powerState + " must be either zero or one"); 
		}
		powerSuppliesPowerState = powerState;
		updateVoltages();

		return powerSuppliesPowerState;
	}


	@Override
	public int setPurgeAirState(int purgeAirState) throws IllegalArgumentException, CommunicationException, CommandFailureException {

		if (purgeAirState != 0 && purgeAirState != 1) {
			throw new IllegalArgumentException("passed purge air state " + purgeAirState + " must be either zero or one"); 
		}
		this.purgeAirState = purgeAirState;
		
		return purgeAirState;
		
	}


	@Override
	public Voltages getVoltages() throws CommunicationException, CommandFailureException {
		
		return voltages;
	}


	@Override
	public void addVoltageListener(VoltageListener l) {
		
		voltageChangeListenerList.add(l);
		
	}


	@Override
	public void addVoltageListener(VoltageListener l, int period) {
		
		voltageChangePeriodicListenerList.add(l);
		
	}


	@Override
	public void addPeriodicVoltageListener(VoltageListener l, int period) throws IllegalArgumentException {
		
		voltagePeriodicListenerList.add(l);
		
	}


	@Override
	public void removeVoltageListener(VoltageListener l) {
		
		voltageChangeListenerList.remove(l);
		voltageChangePeriodicListenerList.remove(l);
		voltagePeriodicListenerList.remove(l);
		
	}


	@Override
	public CameraStatus getCameraStatus() throws CommunicationException, TimeoutException, CommandFailureException {

		logger.trace(MessageGenerator.generateMessage("command.start", "getCameraStatus::SIMULATOR"));

		Math.random();
		
		CameraStatus cameraStatus = new CameraStatus();
		cameraStatus.benchTemp = random(-6.0, 14.0);
		cameraStatus.boxTemp = random(0.0, 25.0);
		cameraStatus.ccdPowerState = ccdControllerPowerState | overallPowerState;
		cameraStatus.ccdTemp = random(-33.0, -14.0);
		cameraStatus.filterWheelPos = filterWheelPosition;
		cameraStatus.prismWheelPos = pupilWheelPosition;
		cameraStatus.refBeamPos = referenceBeamCommand;
		cameraStatus.shutterState = ccdShutterState;
		cameraStatus.twoPosDevPos = twoPositionDevicePosition;
		
		cameraStatus.steeringMirrorX = xSteeringMirrorPosition;
		cameraStatus.steeringMirrorY = ySteeringMirrorPosition;
		cameraStatus.tiltPlateX = xTiltPlatePosition;
		cameraStatus.tiltPlateY = yTiltPlatePosition;
		cameraStatus.filterWheelIsInTransit = false;
		cameraStatus.prismWheelIsInTransit = false;
		cameraStatus.steeringMirrorXIsInTransit = false;
		
		cameraStatus.benchHumidity = random(10.0, 80.0);
		cameraStatus.boxHumidity = random(10.0, 80.0);
		cameraStatus.glycolFlowStatus = randomBool();
		cameraStatus.purgeIsActive = (purgeAirState == 1);
		cameraStatus.tempInterlockActive = randomBool();
		
		
		
		logger.trace(MessageGenerator.generateMessage("command.success", "getCameraStatus::SIMULATOR"));
		return cameraStatus;
	}

	
	
	public float random(double min, double max) {
		return (float) (min + (max - min) * Math.random());
	}

	public boolean randomBool() {
		return Math.random() > 0.5f;
	}

}
