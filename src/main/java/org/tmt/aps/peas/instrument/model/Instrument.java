/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraStatus;

/**
 * Entity class representing the Instrument class.  Query defined on this class joins instrument with Ccd, Camera, referenceBeamSet, coarseTiltMirror, 
 * fineTiltMirror, pupilWheel, filterWheel, pupilMaskSet and filterMaskSet.  This creates one data structure where all configuration can be accessed 
 * and state can be written/read to transient fields in each class. 
 * @author smichaels
 *
 */
@Entity
@Table(name = "Instrument")
@NamedQueries({
	@NamedQuery(name = "findAllInstruments", query = "SELECT o from Instrument o" ),
	@NamedQuery(name = "findInstrument", query = "SELECT DISTINCT o from Instrument o INNER JOIN FETCH o.camera c "
			+ "LEFT OUTER JOIN FETCH c.referenceBeamSet "
			+ "INNER JOIN FETCH c.coarseTiltMirror INNER JOIN FETCH c.fineTiltMirror "
			+ "INNER JOIN FETCH c.pupilWheel pw INNER JOIN FETCH c.filterWheel fw "
			+ "LEFT OUTER JOIN FETCH pw.pupilMaskSet pml "
			+ "LEFT OUTER JOIN FETCH fw.filterSet "
			+ "LEFT OUTER JOIN FETCH o.ccd d LEFT OUTER JOIN d.ccdType LEFT OUTER JOIN FETCH d.ccdGain0 "
			+ "LEFT OUTER JOIN FETCH d.ccdGain1 LEFT OUTER JOIN FETCH d.ccdGain2 LEFT OUTER JOIN FETCH d.ccdGain3 "
			+ "where o.instrumentId = :instrumentId" )
})

public class Instrument {

	@Id
	private Long instrumentId;
	
	@Column(nullable=false, length=100)
	private String instrumentName;
	
	@OneToOne (mappedBy="instrument")
	private Camera camera;
	
	@OneToOne (mappedBy="instrument")
	private Ccd ccd;
	
	public Instrument() {
		
	}
	
	public Instrument (String instrumentName) {
		this.instrumentName = instrumentName;
	}
	
	public Long getInstrumentId() {
		return instrumentId;
	}
	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}
	public String getInstrumentName() {
		return instrumentName;
	}
	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public Ccd getCcd() {
		return ccd;
	}
	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
	}
	
	/**
	 * Given a CameraStatus object, descends the reference chain to the PupilMask, Filter, Ref Beam, Shutter, fine and coarse tilt mirrors,
	 * two position mechanism, CCD power and temperatures and populates each of these with current states.
	 * @param cameraStatus the cameraStatus object to read from and apply.  The source of the cameraStatus object is a status query to the 
	 * PCS camera.
	 */
	public void updateState(CameraStatus cameraStatus) {
		
		// Pupil Mask
		camera.getPupilWheel().setState(cameraStatus.prismWheelIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getPupilWheel().setSelectedPupilMaskNumber(cameraStatus.prismWheelPos);

		// Filter
		camera.getFilterWheel().setState(cameraStatus.filterWheelIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFilterWheel().setSelectedFilterNumber(cameraStatus.filterWheelPos);

		// Ref Beam
		camera.setCurrentRefBeam(cameraStatus.refBeamPos);

		// Shutter
		camera.getShutter().setState(cameraStatus.shutterState == CameraCommand.CLOSED ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
		
		// Fine Tilt
		camera.getFineTiltMirror().setCurrentPosition(new Point(cameraStatus.tiltPlateX, cameraStatus.tiltPlateY));
		camera.getFineTiltMirror().setStateX(cameraStatus.tiltPlateXIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFineTiltMirror().setStateY(cameraStatus.tiltPlateYIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);

		// Coarse Tilt
		camera.getCoarseTiltMirror().setCurrentPosition(new Point(cameraStatus.steeringMirrorX, cameraStatus.steeringMirrorY));
		camera.getCoarseTiltMirror().setStateX(cameraStatus.steeringMirrorXIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getCoarseTiltMirror().setStateY(cameraStatus.steeringMirrorYIsInTransit ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);

		//logger.debug("###### Coarse Mirror: " + cameraStatus.steeringMirrorX + ", " + cameraStatus.steeringMirrorY + ", " + cameraStatus.steeringMirrorXIsInTransit + ", " + cameraStatus.steeringMirrorYIsInTransit);
		
		// Two Position Mech
		camera.getTwoPosMechanism().setState(cameraStatus.twoPosDevPos == CameraCommand.EXTENDED ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);

		// CCD Power
		ccd.setState(cameraStatus.ccdPowerState == CameraCommand.ON ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

		// CCD Temperature
		ccd.setTemperature(((float) cameraStatus.ccdTemp));

		// Instrument Temperature
		camera.setInstrumentTemperature(((float) cameraStatus.benchTemp));

		// Electronics Box Temperature
		camera.setElectronicsBoxTemperature(((float) cameraStatus.boxTemp));
	}
	
	/**
	 * Given a CameraState object, descends the reference chain to the PupilMask, Filter, Ref Beam, Shutter, fine and coarse tilt mirrors,
	 * two position mechanism, CCD power and temperatures and populates each of these with current states.
	 * @param cameraState the cameraState object to read from and apply.  The source of the cameraState object is a database query of the 
	 * state of the camera.
	 */
	public void updateState(CameraState cameraState) {
		
		if (cameraState != null) {
		
		// Pupil Mask
		camera.getPupilWheel().setState(cameraState.isPrismWheelIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getPupilWheel().setSelectedPupilMaskNumber(cameraState.getPrismWheelPos());

		// Filter
		camera.getFilterWheel().setState(cameraState.isFilterWheelIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFilterWheel().setSelectedFilterNumber(cameraState.getFilterWheelPos());

		// Ref Beam
		camera.setCurrentRefBeam(cameraState.getRefBeamPos());

		// Shutter
		camera.getShutter().setState(cameraState.getShutterState() == CameraCommand.CLOSED ? Shutter.STATE_CLOSE : Shutter.STATE_OPEN);
		
		// Fine Tilt
		camera.getFineTiltMirror().setCurrentPosition(new Point(cameraState.getTiltPlateX(), cameraState.getTiltPlateY()));
		camera.getFineTiltMirror().setStateX(cameraState.isTiltPlateXIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getFineTiltMirror().setStateY(cameraState.isTiltPlateYIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);

		// Coarse Tilt
		camera.getCoarseTiltMirror().setCurrentPosition(new Point(cameraState.getSteeringMirrorX(), cameraState.getSteeringMirrorY()));
		camera.getCoarseTiltMirror().setStateX(cameraState.isSteeringMirrorXIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		camera.getCoarseTiltMirror().setStateY(cameraState.isSteeringMirrorYIsInTransit() ? DeviceStates.STATE_IN_TRANSIT : DeviceStates.STATE_IN_POSITION);
		
		// Two Position Mech
		camera.getTwoPosMechanism().setState(cameraState.getTwoPosDevPos() == CameraCommand.EXTENDED ? TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND : TwoPosMechanism.TWO_POS_MECH_STATE_RETRACT);

		// CCD Power
		ccd.setState(cameraState.getCcdPowerState() == CameraCommand.ON ? Ccd.POWER_STATE_ON : Ccd.POWER_STATE_OFF);

		// CCD Temperature
		ccd.setTemperature((float) cameraState.getCcdTemp());

		// Instrument Temperature
		camera.setInstrumentTemperature(((float) cameraState.getBenchTemp()));

		// Electronics Box Temperature
		camera.setElectronicsBoxTemperature(((float) cameraState.getBoxTemp()));
	} else {
		camera.getPupilWheel().setState(DeviceStates.STATE_IN_TRANSIT);
		camera.getPupilWheel().setSelectedPupilMaskNumber(0);

		// Filter
		camera.getFilterWheel().setState(DeviceStates.STATE_IN_TRANSIT);
		camera.getFilterWheel().setSelectedFilterNumber(0);

		// Ref Beam
		camera.setCurrentRefBeam(0);

		// Shutter
		camera.getShutter().setState(Shutter.STATE_CLOSE);
		
		// Fine Tilt
		camera.getFineTiltMirror().setCurrentPosition(new Point(0, 0));
		camera.getFineTiltMirror().setStateX(DeviceStates.STATE_IN_TRANSIT);
		camera.getFineTiltMirror().setStateY(DeviceStates.STATE_IN_TRANSIT);

		// Coarse Tilt
		camera.getCoarseTiltMirror().setCurrentPosition(new Point(0, 0));
		camera.getCoarseTiltMirror().setStateX(DeviceStates.STATE_IN_TRANSIT);
		camera.getCoarseTiltMirror().setStateY(DeviceStates.STATE_IN_TRANSIT);
		
		// Two Position Mech
		camera.getTwoPosMechanism().setState(TwoPosMechanism.TWO_POS_MECH_STATE_EXTEND);

		// CCD Power
		ccd.setState(Ccd.POWER_STATE_OFF);

		// CCD Temperature
		ccd.setTemperature(0.0f);

		// Instrument Temperature
		camera.setInstrumentTemperature(0.0f);

		// Electronics Box Temperature
		camera.setElectronicsBoxTemperature(0.0f);
	
	}
	}
	
}
