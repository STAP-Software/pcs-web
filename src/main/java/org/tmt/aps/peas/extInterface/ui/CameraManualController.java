/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Shutter;
import org.tmt.aps.peas.instrument.model.TwoPosMechanism;

/**
 * JSF Controller class for PCS camera manual/diagnostic user interface.
 * @author smichaels
 */
@Named
@SessionScoped
public class CameraManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PhysicalModel physicalModel;

	@EJB
	CameraMgmt cameraMgmt;



	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	//Camera camera;
	//Ccd ccd;
	int commandSelection;
	int statusUpdateMethod = 0;
	int voltageUpdateMethod = 0;
	String[] queryUpdateMethod;
	int selectedQueryUpdateMethod = 0;
	String cameraQueryListenerDeviceCode = null;
	int voltagePeriod = 2;
	int statusPeriod = 2;
	// use this listener to update the instrument physical model
	DiagnosticCameraStatusListener dcsl;

	// listeners for each device code
	Map<Integer, CameraQueryListener> device2cameraQueryListener = new HashMap<Integer, CameraQueryListener>();
	Map<Integer, String> device2fieldNamePrefix = new HashMap<Integer, String>();
	
	
	int selectedPupilMaskPos = 1;
	int selectedFilterWheelPos = 1;
	int selectedRefBeam = 1;
	int shutterCmd = 1;
	double ccdExposureTime = 1.0;
	Point fineTiltCmd = new Point(0, 0);
	Point coarseTiltCmd = new Point(0, 0);
	int twoPosCmd = 0;
	int ccdPowerCmd = 0;
	int overallPowerStateCmd = 0;
	int networkControllerPowerStateCmd = 0;	
	int fanPowerStateCmd = 0;
	int galilPowerStateCmd = 0;
	int powerSuppliesPowerStateCmd = 0;
	int purgeAirStateCmd;

	int instrumentStateCmd = 0;
	

	@PostConstruct
	public void init() throws Exception {
		
		//physicalModel.refresh();
		//camera = physicalModel.getInstrument().getCamera();

		//camera.setCurrentState(1, 1, 1, 1, 23.0f, 6.22f, 0.43f, 7.54f, -0.32f, 1, 1, -43.2f);
		commandSelection = 1;

		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + physicalModel.getInstrument().getCcd());

		//ccd = physicalModel.getInstrument().getCcd();
		
		 dcsl = new DiagnosticCameraStatusListener(physicalModel.getInstrument());

		 
		 queryUpdateMethod = new String[CameraCommand.DEVICE_CODE_MAX];
		 
		// create all the cameraQueryListeners for each device code
		for (int deviceCode = 1; deviceCode < CameraCommand.DEVICE_CODE_MAX; deviceCode++) {
			
			DiagnosticCameraQueryListener dcql = new DiagnosticCameraQueryListener(physicalModel.getInstrument());
		
			device2cameraQueryListener.put(new Integer(deviceCode), dcql);
			
		}
		
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_CCD_POWER, "ccdPower");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_CCD_SHUTTER, "shutter");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_CCD_TEMPERATURE, "ccdTemp");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_ELECTRONICS_BOX_TEMPERATURE, "boxTemp");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_ELECTRONICS_RH, "boxHumid");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_FILTER_WHEEL, "filterWheel");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_GALIL_POWER, "galilPower");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_OPTICAL_BENCH_RH, "instHumid");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_OPTICAL_BENCH_TEMPERATURE, "instTemp");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_PUPIL_WHEEL, "pupilMask");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_REFERENCE_BEAMS, "refBeam");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_TEMPERATURE_INTERLOCK, "tempInterlock");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_TWO_POSITION_DEVICE, "twoPos");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_X_STEERING_MIRROR, "coarseX");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_X_TILT_PLATE, "fineX");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_Y_STEERING_MIRROR, "coarseY");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_Y_TILT_PLATE, "fineY");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_PURGE_AIR, "purge");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_GLYCOL_FLOW, "glycol");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_OVERALL_STATUS, "overallStat");
		device2fieldNamePrefix.put(CameraCommand.DEVICE_CODE_OVERALL_POWER, "overallPower");
		
	}
	
	

	public Camera getCamera() {
		return physicalModel.getInstrument().getCamera();
	}

	//public void setCamera(Camera camera) {
	//	this.camera = camera;
	//}

	public Ccd getCcd() {
		return physicalModel.getInstrument().getCcd();
	}

	//public void setCcd(Ccd ccd) {
	//	this.ccd = ccd;
	//}

	public int getCommandSelection() {
		return commandSelection;
	}

	public void setCommandSelection(int commandSelection) {
		this.commandSelection = commandSelection;
	}

	public int getSelectedPupilMaskPos() {
		return selectedPupilMaskPos;
	}

	public void setSelectedPupilMaskPos(int selectedPupilMaskPos) {
		this.selectedPupilMaskPos = selectedPupilMaskPos;
	}

	public int getSelectedFilterWheelPos() {
		return selectedFilterWheelPos;
	}

	public void setSelectedFilterWheelPos(int selectedFilterWheelPos) {
		this.selectedFilterWheelPos = selectedFilterWheelPos;
	}

	public int getSelectedRefBeam() {
		return selectedRefBeam;
	}

	public void setSelectedRefBeam(int selectedRefBeam) {
		this.selectedRefBeam = selectedRefBeam;
	}

	public int getShutterCmd() {
		return shutterCmd;
	}

	public void setShutterCmd(int shutterCmd) {
		this.shutterCmd = shutterCmd;
	}

	public double getCcdExposureTime() {
		return ccdExposureTime;
	}

	public void setCcdExposureTime(double ccdExposureTime) {
		this.ccdExposureTime = ccdExposureTime;
	}

	public Point getFineTiltCmd() {
		return fineTiltCmd;
	}

	public void setFineTiltCmd(Point fineTiltCmd) {
		this.fineTiltCmd = fineTiltCmd;
	}

	public Point getCoarseTiltCmd() {
		return coarseTiltCmd;
	}

	public void setCoarseTiltCmd(Point coarseTiltCmd) {
		this.coarseTiltCmd = coarseTiltCmd;
	}

	public int getTwoPosCmd() {
		return twoPosCmd;
	}

	public void setTwoPosCmd(int twoPosCmd) {
		this.twoPosCmd = twoPosCmd;
	}

	public int getCcdPowerCmd() {
		return ccdPowerCmd;
	}

	public void setCcdPowerCmd(int ccdPowerCmd) {
		this.ccdPowerCmd = ccdPowerCmd;
	}

	public int getInstrumentStateCmd() {
		return instrumentStateCmd;
	}

	public void setInstrumentStateCmd(int instrumentStateCmd) {
		this.instrumentStateCmd = instrumentStateCmd;
	}



	public int getOverallPowerStateCmd() {
		return overallPowerStateCmd;
	}

	public void setOverallPowerStateCmd(int overallPowerStateCmd) {
		this.overallPowerStateCmd = overallPowerStateCmd;
	}

	public int getNetworkControllerPowerStateCmd() {
		return networkControllerPowerStateCmd;
	}

	public void setNetworkControllerPowerStateCmd(int networkControllerPowerStateCmd) {
		this.networkControllerPowerStateCmd = networkControllerPowerStateCmd;
	}

	public int getFanPowerStateCmd() {
		return fanPowerStateCmd;
	}

	public void setFanPowerStateCmd(int fanPowerStateCmd) {
		this.fanPowerStateCmd = fanPowerStateCmd;
	}

	public int getGalilPowerStateCmd() {
		return galilPowerStateCmd;
	}

	public void setGalilPowerStateCmd(int galilPowerStateCmd) {
		this.galilPowerStateCmd = galilPowerStateCmd;
	}

	public int getPowerSuppliesPowerStateCmd() {
		return powerSuppliesPowerStateCmd;
	}

	public void setPowerSuppliesPowerStateCmd(int powerSuppliesPowerStateCmd) {
		this.powerSuppliesPowerStateCmd = powerSuppliesPowerStateCmd;
	}

	public int getPurgeAirStateCmd() {
		return purgeAirStateCmd;
	}

	public void setPurgeAirStateCmd(int purgeAirStateCmd) {
		this.purgeAirStateCmd = purgeAirStateCmd;
	}

	public int getStatusUpdateMethod() {
		return statusUpdateMethod;
	}

	public void setStatusUpdateMethod(int statusUpdateMethod) {
		this.statusUpdateMethod = statusUpdateMethod;
	}

	public int getVoltageUpdateMethod() {
		return voltageUpdateMethod;
	}

	public void setVoltageUpdateMethod(int voltageUpdateMethod) {
		this.voltageUpdateMethod = voltageUpdateMethod;
	}

	public String[] getQueryUpdateMethod() {
		return queryUpdateMethod;
	}

	public void setQueryUpdateMethod(String[] queryUpdateMethod) {
		this.queryUpdateMethod = queryUpdateMethod;
	}

	public int getSelectedQueryUpdateMethod() {
		return selectedQueryUpdateMethod;
	}

	public void setSelectedQueryUpdateMethod(int selectedQueryUpdateMethod) {
		this.selectedQueryUpdateMethod = selectedQueryUpdateMethod;
	}

	public String getCameraQueryListenerDeviceCode() {
		return cameraQueryListenerDeviceCode;
	}

	public void setCameraQueryListenerDeviceCode(String cameraQueryListenerDeviceCode) {
		this.cameraQueryListenerDeviceCode = cameraQueryListenerDeviceCode;
	}

	public int getStatusPeriod() {
		return statusPeriod;
	}

	public void setStatusPeriod(int statusPeriod) {
		this.statusPeriod = statusPeriod;
	}

	public int getVoltagePeriod() {
		return voltagePeriod;
	}

	public void setVoltagePeriod(int voltagePeriod) {
		this.voltagePeriod = voltagePeriod;
	}

	public boolean getRenderExposureTime() {
		return (commandSelection == 4) && (shutterCmd == Shutter.STATE_TIMED_EXPOSURE);
	}
	
	// Listener methods
	
	public void statusUpdateMethodChangeListener() throws Exception {
		
		// clear status listeners: disconnect reference
		dcsl = new DiagnosticCameraStatusListener(physicalModel.getInstrument());
		
		// clear camera query listeners
		removeAllCameraQueryListeners();
		
		switch (statusUpdateMethod) {
		case 1: // Status Change Listener
			cameraMgmt.addCameraStatusListener(dcsl);
			break;
		case 4: // Individual Camera Query Calls
			break;
		case 5: // getStatus() call
			break;
		default: // no method
			break;
		}
	}
	

	
	private void removeAllCameraQueryListeners() throws Exception {
		for (Integer deviceCode : device2cameraQueryListener.keySet()) {
			CameraQueryListener cql = device2cameraQueryListener.get(deviceCode);
			
			// Kill reference - weak reference will take care of removeListener
			cql = new DiagnosticCameraQueryListener(physicalModel.getInstrument());
			device2cameraQueryListener.put(new Integer(deviceCode), cql);

		}
	}
	
	public void queryUpdateMethodChangeListener(AjaxBehaviorEvent event) throws Exception {
		
		// TODO: allow user to specify period 
		
		//int deviceCode = new Integer(cameraQueryListenerDeviceCode);
		
		String deviceCd = (String)event.getComponent().getAttributes().get("deviceCode");
		
		int deviceCode = new Integer(deviceCd);
				
		// 1. remove current listener from all listeners
		CameraQueryListener cql = device2cameraQueryListener.get(deviceCode);
		// Kill reference - weak reference will take care of removeListener
		cql = new DiagnosticCameraQueryListener(physicalModel.getInstrument());
		device2cameraQueryListener.put(new Integer(deviceCode), cql);

		
		int method = new Integer(queryUpdateMethod[deviceCode]);
		
		// 2. add the device code to the selected listener type
		switch (method) {
		case 1: // Query Change Listener
			cameraMgmt.addCameraQueryListener(deviceCode, cql);
			break;
		case 4: // queryCamera() call
			break;
		default: // no method
			break;
			
		}

	}
	
	/**
	 * Checks all listeners to see if they have changes that require view updates
	 * and update the view if needed.
	 */
	public void pollListener() {
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		
		
		// check each listener to see if it is asking for an update of the view
		
		if (dcsl.isUpdateRequested()) {
			requestContext.update("cameraDiagForm:cameraStatusPanel");
			dcsl.setUpdateRequested(false);
			
			return; // don't need to update by device if we are updating all of them
		}
		
		
		// listeners for each device code
		for (Integer deviceCode : device2cameraQueryListener.keySet()) {
			
			DiagnosticCameraQueryListener dcql = (DiagnosticCameraQueryListener)device2cameraQueryListener.get(deviceCode);
						
			if (dcql.isUpdateRequested()) {
				
				String prefix = device2fieldNamePrefix.get(deviceCode);
				
				
				System.out.println(" Updating: cameraDiagForm:" + prefix + "Value");
				System.out.println(" Updating: cameraDiagForm:" + prefix + "Status");
				
				requestContext.update("cameraDiagForm:" + prefix + "Value"); 
				requestContext.update("cameraDiagForm:" + prefix + "Status"); 
									
				dcql.setUpdateRequested(false);
			}
			
		}

	}

	/**
	 * JSF Action method to render the Camera I/F manual/diagnostic user interface
	 * @return the JSF page to render
	 */
	public String doViewCameraDiagnostic() {

		try {
			breadcrumbMenuBean.addFirstItem("Camera Diagnostic", "/modules/diagnostic/cameraDiagnostic.xhtml");

			return "/modules/diagnostic/cameraDiagnostic.xhtml?faces-redirect=true";

		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying camera database", e.getMessage()));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		
	}

	public void doGetStatus() {
		// page updates from the Physical model
	}
	
	public void doGetVoltage() {
		// page updates from the Physical model
	}
	
	public void doCallQueryCamera(int deviceCode) {
		// page updates from the Physical model
	}
	
	/**
	 * JSF Action method handling user pressing the 'cancel' button
	 */
	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method handling when the user clicks the 'Send Command' button
	 */
	public void doSendCommand() {
			
		logger.info("command selection = " + commandSelection);
		
		switch (commandSelection) {

		case 1: // Pupil Mask
			doSendPupilMaskCommand();
			break;

		case 2: // Filter
			doSendFilterCommand();
			break;

		case 3: // Ref Beam
			doSendRefBeamCommand();
			break;

		case 4: // Shutter
			doSendShutterCommand();
			break;

		case 5: // Fine Tilt
			doSendFineCommandBoth();
			break;

		case 6: // Coarse Tilt
			doSendCoarseCommandBoth();
			break;

		case 7: // Two Position Mech
			doSendTwoPosMechCommand();
			break;

		case 8: // CCD Power
			doSendCcdPowerCommand();				
			break;

		default:

		}
	}

	
	public void doSendPupilMaskCommand() {
		
		try {

			Future<Integer> pupilCmdFuture = cameraMgmt.commandPupilMask(selectedPupilMaskPos);
			while (!pupilCmdFuture.isDone()) {
				logger.debug("Thread waiting");
				Thread.sleep(500);
			}
			logger.debug("PupilCmdFuture is Done");
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Pupil Mask"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendFilterCommand() {
		
		try {

			Future<Integer> future = cameraMgmt.commandFilterWheel(selectedFilterWheelPos);
			while (!future.isDone()) {
				logger.debug("Thread waiting");
				Thread.sleep(500);
			}
			logger.debug("FilterCmdFuture is Done");
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Filter"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	
	public void doSendRefBeamCommand() {
		
		try {

			Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(selectedRefBeam);
			
			while (!refBeamFuture.isDone()) {
				Thread.sleep(500);
			}
			refBeamFuture.get();
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Ref Beam"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendShutterCommand() {
		
		try {

			if (shutterCmd == Shutter.STATE_CLOSE) {
				int state = cameraMgmt.commandCcdShutterState(0);
				//getCamera().getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
			} else if (shutterCmd == Shutter.STATE_OPEN) {
				int state = cameraMgmt.commandCcdShutterState(1);
				//getCamera().getShutter().setState(state == 0 ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
			} else {
				// timed exposure
				cameraMgmt.commandCcdShutterExposure((int) (ccdExposureTime * 1000));
				//getCamera().getShutter().setState(Shutter.STATE_TIMED_EXPOSURE);
			}
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Shutter"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendFineCommandBoth() {
		
		try {
			Future<Point> future = cameraMgmt.commandFineTiltMirror(fineTiltCmd);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Point result = future.get();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Fine Tilt"));			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	public void doSendFineCommandX() {
		
		try {
			Future<Integer> future = cameraMgmt.commandFineTiltMirrorX(fineTiltCmd.x);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Integer result = future.get();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Fine Tilt X"));			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
		} catch (IllegalArgumentException e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.illegalArgumentMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	public void doSendFineCommandY() {
		
		try {
			Future<Integer> future = cameraMgmt.commandFineTiltMirrorY(fineTiltCmd.y);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Integer result = future.get();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Fine Tilt Y"));			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendCoarseCommandX() {
		
		try {
			Future<Integer> future = cameraMgmt.commandCoarseTiltMirrorX(coarseTiltCmd.x);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Integer result = future.get();									
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Coarse Tilt X"));
			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendCoarseCommandY() {
		
		try {
			Future<Integer> future = cameraMgmt.commandCoarseTiltMirrorY(coarseTiltCmd.y);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Integer result = future.get();									
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Coarse Tilt Y"));
			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendCoarseCommandBoth() {
		
		try {
			Future<Point> future = cameraMgmt.commandCoarseTiltMirror(coarseTiltCmd);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			Point result = future.get();									
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Coarse Tilt"));
			
		} catch (CommandFailureException e) {			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendTwoPosMechCommand() {
		
		try {

			int command = (twoPosCmd == TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND) ? 1 : 0;
			Future<Integer> twoPosFuture = cameraMgmt.commandTwoPositionDevice(command);
			while (!twoPosFuture.isDone()) {
				Thread.sleep(500);
			}
			int twoPosState = twoPosFuture.get();
			//getCamera().getTwoPosMechanism().setState(
			//		twoPosState == 1 ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Two Pos Mech"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	public void doSendCcdPowerCommand() {
		
		try {

			Future<Integer> ccdPowerFuture = cameraMgmt.commandCcdControllerPowerState(ccdPowerCmd == Ccd.POWER_STATE_ON ? 1 : 0);
			while (!ccdPowerFuture.isDone()) {
				Thread.sleep(500);
			}
			int ccdState = ccdPowerFuture.get();
			//getCcd().setState(ccdState == 1 ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Ccd Power"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}
	
	
	
	
	public void doSendInstrumentStateCommand() {
		
		try {

			Future<Integer> instFuture = null;
			String commandName = null;
			if (instrumentStateCmd == 1) {
				// init
				instFuture = cameraMgmt.initializeCamera();
				commandName = "Initialize Camera";
			} else if (instrumentStateCmd == 0) {
				// stow
				instFuture = cameraMgmt.stowCamera();
				commandName = "Stow Camera";
			}
			while (!instFuture.isDone()) {
				Thread.sleep(500);
			}
			instFuture.get();
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage(commandName));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}
	
	
	public void doSendOverallPowerStateCommand() {
		
		try {

			Future<Integer> powerFuture = cameraMgmt.commandOverallPowerState(overallPowerStateCmd);
			while (!powerFuture.isDone()) {
				Thread.sleep(500);
			}
			powerFuture.get();
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Overall Power State"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}
	
	

	public void doSendGalilPowerStateCommand() {
		
		try {

			Future<Integer> powerFuture = cameraMgmt.commandGalilPowerState(galilPowerStateCmd);
			while (!powerFuture.isDone()) {
				Thread.sleep(500);
			}
			powerFuture.get();
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Galil Power State"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}
	
	
	public void doSendPurgeAirStateCommand() {
		
		try {

			Future<Integer> future = cameraMgmt.setPurgeAirState(purgeAirStateCmd);
			while (!future.isDone()) {
				Thread.sleep(500);
			}
			future.get();
						
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Purge Air State"));

			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
	}
	
	
	

}
