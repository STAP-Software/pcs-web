/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.frame.model.CcdFrame;

/**
 * Database Entity representing the CameraState table.  Each record is a snapshot of the camera state, typically taken when a frame is being taken.
 * @author smichaels
 *
 */
@Entity
@Table(name = "CameraState")
public class CameraState {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long cameraStateId;
	
	private int prismWheelPos;
	private int filterWheelPos;
	private int twoPosDevPos;
	private int shutterState;
	private int refBeamPos;
	private int ccdPowerState;
	private float ccdTemp; // deg C
	private float boxTemp; // deg C
	private float benchTemp; // deg C
	private float boxHumidity; // deg C
	private float benchHumidity; // deg C
	
	private boolean temperatureInterlock;
	private boolean purgeState;
	private boolean glycolFlow;
	
	private int overallPowerState;
	private int galilPowerState;
	private int overallStatus;
	
	private int tiltPlateX; // microns
	private int tiltPlateY; // microns
	private int steeringMirrorX; // microns
	private int steeringMirrorY; // microns
	
	private boolean prismWheelIsInTransit;
	private boolean filterWheelIsInTransit;
	private boolean tiltPlateXIsInTransit;
	private boolean tiltPlateYIsInTransit;
	private boolean steeringMirrorXIsInTransit;
	private boolean steeringMirrorYIsInTransit;

	/**
	 * Default constructor
	 */
	public CameraState() {
		
	}

	/**
	 * Constructs a CameraState from a CameraStatus object. 
	 * @param cameraStatus the camera status read from the camera I/F
	 */
	public CameraState(CameraStatus cameraStatus) {
		this.prismWheelPos = cameraStatus.prismWheelPos;
		this.filterWheelPos = cameraStatus.filterWheelPos;
		this.twoPosDevPos = cameraStatus.twoPosDevPos;
		this.shutterState = cameraStatus.shutterState;
		this.refBeamPos = cameraStatus.refBeamPos;
		this.ccdPowerState = cameraStatus.ccdPowerState;
		this.ccdTemp = (float)cameraStatus.ccdTemp; 
		this.boxTemp = (float)cameraStatus.boxTemp; 
		this.benchTemp = (float)cameraStatus.benchTemp; 
		
		this.tiltPlateX = cameraStatus.tiltPlateX; 
		this.tiltPlateY = cameraStatus.tiltPlateY; 
		this.steeringMirrorX = cameraStatus.steeringMirrorX; 
		this.steeringMirrorY = cameraStatus.steeringMirrorY; 
	
		this.boxHumidity = (float)cameraStatus.boxHumidity; 
		this.benchHumidity = (float)cameraStatus.benchHumidity; 
		this.temperatureInterlock = cameraStatus.tempInterlockActive;
		this.purgeState = cameraStatus.purgeIsActive;
		this.glycolFlow = cameraStatus.glycolIsFlowing;
		this.overallPowerState = cameraStatus.overallPowerState;
		this.galilPowerState = cameraStatus.galilPowerState;
		this.overallStatus = cameraStatus.overallStatus;

		
		this.prismWheelIsInTransit = cameraStatus.prismWheelIsInTransit;
		this.filterWheelIsInTransit = cameraStatus.filterWheelIsInTransit;
		this.tiltPlateXIsInTransit = cameraStatus.tiltPlateXIsInTransit;
		this.tiltPlateYIsInTransit = cameraStatus.tiltPlateYIsInTransit;
		this.steeringMirrorXIsInTransit = cameraStatus.steeringMirrorXIsInTransit;
		this.steeringMirrorYIsInTransit = cameraStatus.steeringMirrorYIsInTransit;
	}
	
	/**
	 * Constructs a CameraState from an Instrument.  Descends the instrument reference tree to each component and reads state information from transient fields into this instance.
	 * This is useful when creating a CameraState to assign to a CcdFrame: if the passed instrument is 'live', i.e. a reference to the Instrument in the {#link PhysicalModel} 
	 * then the most recent camera state can be stored without an additional I/F query, which could add time to CCD reads.
	 * @param instrument the instrument object to read state from
	 */
	public CameraState(Instrument instrument) {
		
		Camera camera = instrument.getCamera();
		Ccd ccd = instrument.getCcd();
		
		// Pupil Mask
		this.prismWheelPos = camera.getPupilWheel().getSelectedPupilMask().getWheelPosition();
		this.prismWheelIsInTransit = camera.getPupilWheel().getState() == DeviceStates.STATE_IN_TRANSIT;

		// Filter
		this.filterWheelPos = camera.getFilterWheel().getSelectedFilter().getWheelPosition();
		this.filterWheelIsInTransit = camera.getFilterWheel().getState() == DeviceStates.STATE_IN_TRANSIT;
		
		// Ref Beam
		this.refBeamPos = camera.getCurrentRefBeam();

		// Shutter
		this.shutterState = camera.getShutter().getState() == Shutter.STATE_CLOSE ? CameraCommand.CLOSED : CameraCommand.OPEN;
				
		// Fine Tilt
		Point finePos = camera.getFineTiltMirror().getCurrentPosition();
		this.tiltPlateX = finePos.x; 
		this.tiltPlateY = finePos.y; 
		this.tiltPlateXIsInTransit = camera.getFineTiltMirror().getStateX() == DeviceStates.STATE_IN_TRANSIT;
		this.tiltPlateYIsInTransit = camera.getFineTiltMirror().getStateY() == DeviceStates.STATE_IN_TRANSIT;

		// Coarse Tilt
		Point coarsePos = camera.getCoarseTiltMirror().getCurrentPosition();
		this.steeringMirrorX = coarsePos.x; 
		this.steeringMirrorY = coarsePos.y; 
		this.steeringMirrorXIsInTransit = camera.getCoarseTiltMirror().getStateX() == DeviceStates.STATE_IN_TRANSIT;
		this.steeringMirrorYIsInTransit = camera.getCoarseTiltMirror().getStateY() == DeviceStates.STATE_IN_TRANSIT;
		
		// Two Position Mech
		this.twoPosDevPos = camera.getTwoPosMechanism().getState() == TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND ? CameraCommand.EXTENDED : CameraCommand.RETRACTED;
				
		// CCD Power
		this.ccdPowerState = ccd.getState() == Ccd.POWER_STATE_ON ? CameraCommand.ON : CameraCommand.OFF;
				
		// CCD Temperature
		this.ccdTemp = ccd.getTemperature();

		// Instrument Temperature
		this.boxTemp = (float)camera.getInstrumentTemperature(); 

		// Electronics Box Temperature
		this.benchTemp = (float)camera.getElectronicsBoxTemperature(); 
		
		// Instrument Humidity
		this.benchHumidity = (float)camera.getInstrumentHumidity();
		
		// Electronics Box Humidity
		this.boxHumidity = (float)camera.getElectronicsBoxHumidity();
		
		// Temperature Interlock
		this.temperatureInterlock = camera.isTemperatureInterlock();
		
		// Purge State
		this.purgeState = camera.isPurgeState();
		
		// Glycol Flow
		this.glycolFlow = camera.isGlycolFlow();

		// Overall Power State
		this.overallPowerState = camera.getOverallPowerState();
				
		// Galil Power State
		this.galilPowerState = camera.getGalilPowerState();
		
		// Overall Status
		this.overallStatus = camera.getOverallStatus();

				
	}

	public Long getCameraStateId() {
		return cameraStateId;
	}

	public void setCameraStateId(Long cameraStateId) {
		this.cameraStateId = cameraStateId;
	}

	public int getPrismWheelPos() {
		return prismWheelPos;
	}

	public void setPrismWheelPos(int prismWheelPos) {
		this.prismWheelPos = prismWheelPos;
	}

	public int getFilterWheelPos() {
		return filterWheelPos;
	}

	public void setFilterWheelPos(int filterWheelPos) {
		this.filterWheelPos = filterWheelPos;
	}

	public int getTwoPosDevPos() {
		return twoPosDevPos;
	}

	public void setTwoPosDevPos(int twoPosDevPos) {
		this.twoPosDevPos = twoPosDevPos;
	}

	public int getShutterState() {
		return shutterState;
	}

	public void setShutterState(int shutterState) {
		this.shutterState = shutterState;
	}

	public int getRefBeamPos() {
		return refBeamPos;
	}

	public void setRefBeamPos(int refBeamPos) {
		this.refBeamPos = refBeamPos;
	}

	public int getCcdPowerState() {
		return ccdPowerState;
	}

	public void setCcdPowerState(int ccdPowerState) {
		this.ccdPowerState = ccdPowerState;
	}

	public double getCcdTemp() {
		return ccdTemp;
	}

	public float getBoxTemp() {
		return boxTemp;
	}

	public void setBoxTemp(float boxTemp) {
		this.boxTemp = boxTemp;
	}

	public float getBenchTemp() {
		return benchTemp;
	}

	public void setBenchTemp(float benchTemp) {
		this.benchTemp = benchTemp;
	}

	public void setCcdTemp(float ccdTemp) {
		this.ccdTemp = ccdTemp;
	}

	public int getTiltPlateX() {
		return tiltPlateX;
	}

	public void setTiltPlateX(int tiltPlateX) {
		this.tiltPlateX = tiltPlateX;
	}

	public int getTiltPlateY() {
		return tiltPlateY;
	}

	public void setTiltPlateY(int tiltPlateY) {
		this.tiltPlateY = tiltPlateY;
	}

	public int getSteeringMirrorX() {
		return steeringMirrorX;
	}

	public void setSteeringMirrorX(int steeringMirrorX) {
		this.steeringMirrorX = steeringMirrorX;
	}

	public int getSteeringMirrorY() {
		return steeringMirrorY;
	}

	public void setSteeringMirrorY(int steeringMirrorY) {
		this.steeringMirrorY = steeringMirrorY;
	}

	public boolean isPrismWheelIsInTransit() {
		return prismWheelIsInTransit;
	}

	public void setPrismWheelIsInTransit(boolean prismWheelIsInTransit) {
		this.prismWheelIsInTransit = prismWheelIsInTransit;
	}

	public boolean isFilterWheelIsInTransit() {
		return filterWheelIsInTransit;
	}

	public void setFilterWheelIsInTransit(boolean filterWheelIsInTransit) {
		this.filterWheelIsInTransit = filterWheelIsInTransit;
	}

	public boolean isTiltPlateXIsInTransit() {
		return tiltPlateXIsInTransit;
	}

	public void setTiltPlateXIsInTransit(boolean tiltPlateXIsInTransit) {
		this.tiltPlateXIsInTransit = tiltPlateXIsInTransit;
	}

	public boolean isTiltPlateYIsInTransit() {
		return tiltPlateYIsInTransit;
	}

	public void setTiltPlateYIsInTransit(boolean tiltPlateYIsInTransit) {
		this.tiltPlateYIsInTransit = tiltPlateYIsInTransit;
	}

	public boolean isSteeringMirrorXIsInTransit() {
		return steeringMirrorXIsInTransit;
	}

	public void setSteeringMirrorXIsInTransit(boolean steeringMirrorXIsInTransit) {
		this.steeringMirrorXIsInTransit = steeringMirrorXIsInTransit;
	}

	public boolean isSteeringMirrorYIsInTransit() {
		return steeringMirrorYIsInTransit;
	}

	public void setSteeringMirrorYIsInTransit(boolean steeringMirrorYIsInTransit) {
		this.steeringMirrorYIsInTransit = steeringMirrorYIsInTransit;
	}

	public float getBoxHumidity() {
		return boxHumidity;
	}

	public void setBoxHumidity(float boxHumidity) {
		this.boxHumidity = boxHumidity;
	}

	public float getBenchHumidity() {
		return benchHumidity;
	}

	public void setBenchHumidity(float benchHumidity) {
		this.benchHumidity = benchHumidity;
	}

	public boolean isTemperatureInterlock() {
		return temperatureInterlock;
	}

	public void setTemperatureInterlock(boolean temperatureInterlock) {
		this.temperatureInterlock = temperatureInterlock;
	}

	public boolean isPurgeState() {
		return purgeState;
	}

	public void setPurgeState(boolean purgeState) {
		this.purgeState = purgeState;
	}

	public boolean isGlycolFlow() {
		return glycolFlow;
	}

	public void setGlycolFlow(boolean glycolFlow) {
		this.glycolFlow = glycolFlow;
	}

	public int getOverallPowerState() {
		return overallPowerState;
	}

	public void setOverallPowerState(int overallPowerState) {
		this.overallPowerState = overallPowerState;
	}

	public int getGalilPowerState() {
		return galilPowerState;
	}

	public void setGalilPowerState(int galilPowerState) {
		this.galilPowerState = galilPowerState;
	}

	public int getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(int overallStatus) {
		this.overallStatus = overallStatus;
	}


	
	
}
