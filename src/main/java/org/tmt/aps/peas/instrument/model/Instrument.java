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

@Entity
@Table(name = "Instrument")
@NamedQueries({
	@NamedQuery(name = "findAllInstruments", query = "SELECT o from Instrument o" ),
	@NamedQuery(name = "findInstrument", query = "SELECT DISTINCT o from Instrument o INNER JOIN FETCH o.camera c "
			+ "LEFT OUTER JOIN FETCH c.referenceBeamSet "
			+ "INNER JOIN FETCH c.coarseTiltMirror INNER JOIN FETCH c.fineTiltMirror "
			+ "INNER JOIN FETCH c.pupilWheel pw INNER JOIN FETCH c.filterWheel fw "
			+ "LEFT OUTER JOIN FETCH pw.pupilMaskSet pml "
			+ "LEFT OUTER JOIN FETCH pml.sufsGroupSet "
			+ "LEFT OUTER JOIN FETCH fw.filterSet "
			+ "LEFT OUTER JOIN FETCH o.ccd "
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
	
}
