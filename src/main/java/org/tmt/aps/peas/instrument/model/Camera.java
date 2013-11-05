/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;

@Entity
@Table(name = "Camera")
@NamedQueries({
	@NamedQuery(name = "findCamera", query = "SELECT o from Camera o where cameraId = :cameraId" ),
})
public class Camera {

	
	public static final int CCD_POWER_STATE_ON = 1;
	public static final int CCD_POWER_STATE_OFF = 2;
	
	@Id
	private Long cameraId;
	
	@Transient
	private int currentRefBeam;
	@Transient
	private Shutter shutter;
	@Transient
	private FineTiltMirror fineTilt;
	@Transient
	private TwoPosMechanism twoPosMechanism;
	@Transient
	private float instrumentTemperature;
	@Transient
	private float electronicsBoxTemperature;

	@OneToOne
	@JoinColumn(name="instrumentId")
	private Instrument instrument;

	@OneToOne (mappedBy="camera")
	private CoarseTiltMirror coarseTiltMirror;

	@OneToOne (mappedBy="camera")
	private PupilWheel pupilWheel;
	
	@OneToOne (mappedBy="camera")
	private FilterWheel filterWheel;
	
	@OneToMany (mappedBy="camera")
	List<ReferenceBeam> referenceBeamList;

	public Camera() {
		
	}
	
	public Camera (int pupilMask, int filter, int currentRefBeam, int shutterState, float shutterExposureTime, 
			float coarseTiltX, float coarseTiltY, float fineTiltX, float fineTiltY, int twoPosMechanismState, 
			float instrumentTemperature, float electronicsBoxTemperature) {
		
		//this.pupilMask = pupilMask;
		//this.filter = filter;
		this.currentRefBeam = currentRefBeam;
		this.shutter = new Shutter(shutterState, shutterExposureTime);
		this.fineTilt = new FineTiltMirror(fineTiltX, fineTiltY);
		this.twoPosMechanism = new TwoPosMechanism(twoPosMechanismState);
		this.instrumentTemperature = instrumentTemperature;
		this.electronicsBoxTemperature = electronicsBoxTemperature;
	}
	
	public int getCurrentRefBeam() {
		return currentRefBeam;
	}

	public void setCurrentRefBeam(int currentRefBeam) {
		this.currentRefBeam = currentRefBeam;
	}

	public Shutter getShutter() {
		return shutter;
	}

	public void setShutter(Shutter shutter) {
		this.shutter = shutter;
	}

	public CoarseTiltMirror getCoarseTiltMirror() {
		return coarseTiltMirror;
	}

	public void setCoarseTiltMirror(CoarseTiltMirror coarseTiltMirror) {
		this.coarseTiltMirror = coarseTiltMirror;
	}

	public FineTiltMirror getFineTilt() {
		return fineTilt;
	}

	public void setFineTilt(FineTiltMirror fineTilt) {
		this.fineTilt = fineTilt;
	}

	public TwoPosMechanism getTwoPosMechanism() {
		return twoPosMechanism;
	}

	public void setTwoPosMechanism(TwoPosMechanism twoPosMechanism) {
		this.twoPosMechanism = twoPosMechanism;
	}
	
	public float getInstrumentTemperature() {
		return instrumentTemperature;
	}

	public void setInstrumentTemperature(float instrumentTemperature) {
		this.instrumentTemperature = instrumentTemperature;
	}

	public float getElectronicsBoxTemperature() {
		return electronicsBoxTemperature;
	}

	public void setElectronicsBoxTemperature(float electronicsBoxTemperature) {
		this.electronicsBoxTemperature = electronicsBoxTemperature;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public PupilWheel getPupilWheel() {
		return pupilWheel;
	}

	public void setPupilWheel(PupilWheel pupilWheel) {
		this.pupilWheel = pupilWheel;
	}

	public FilterWheel getFilterWheel() {
		return filterWheel;
	}

	public void setFilterWheel(FilterWheel filterWheel) {
		this.filterWheel = filterWheel;
	}

	public List<ReferenceBeam> getReferenceBeamList() {
		return referenceBeamList;
	}

	public void setReferenceBeamList(List<ReferenceBeam> referenceBeamList) {
		this.referenceBeamList = referenceBeamList;
	}

	public Long getCameraId() {
		return cameraId;
	}

	public void setCameraId(Long cameraId) {
		this.cameraId = cameraId;
	}



}
