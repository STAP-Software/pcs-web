/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanComparator;
import org.tmt.aps.peas.common.Point;

@Entity
@Table(name = "Camera")
@NamedQueries({
	@NamedQuery(name = "findCamera", query = "SELECT o from Camera o where o.cameraId = :cameraId" ),
})
public class Camera {

	
	public static final int CCD_POWER_STATE_ON = 1;
	public static final int CCD_POWER_STATE_OFF = 2;
	
	@Id
	private Long cameraId;
	
	@Transient
	private int currentRefBeam;
	@Transient
	private Shutter shutter = new Shutter();
	@Transient
	private TwoPosMechanism twoPosMechanism = new TwoPosMechanism();
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
	private FineTiltMirror fineTiltMirror;

	@OneToOne (mappedBy="camera")
	private PupilWheel pupilWheel;
	
	@OneToOne (mappedBy="camera")
	private FilterWheel filterWheel;
	
	@OneToMany (mappedBy="camera")
	Set<ReferenceBeam> referenceBeamSet;

	public Camera() {
		
	}
	
	public void setCurrentState(int selectedPupilMaskNum, int selectedFilterNum, int currentRefBeam, int shutterState, float shutterExposureTime, 
			float coarseTiltX, float coarseTiltY, float fineTiltX, float fineTiltY, int twoPosMechanismState, 
			float instrumentTemperature, float electronicsBoxTemperature) {
		
		this.pupilWheel.setSelectedPupilMaskNumber(selectedPupilMaskNum);
		this.filterWheel.setSelectedFilterNumber(selectedFilterNum);
		this.currentRefBeam = currentRefBeam;
		this.shutter = new Shutter(shutterState, shutterExposureTime);
		this.twoPosMechanism = new TwoPosMechanism(twoPosMechanismState);
		this.instrumentTemperature = instrumentTemperature;
		this.electronicsBoxTemperature = electronicsBoxTemperature;
		this.coarseTiltMirror.setCurrentPosition(new Point((int)coarseTiltX, (int)coarseTiltY));
		this.fineTiltMirror.setCurrentPosition(new Point((int)fineTiltX, (int)fineTiltY));
	}
	
	public String getCurrentRefBeamDisplayString() {
		if (currentRefBeam == 0) {
			return "Off";
		} else {
			return "" + currentRefBeam;
		}
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

	public FineTiltMirror getFineTiltMirror() {
		return fineTiltMirror;
	}

	public void setFineTiltMirror(FineTiltMirror fineTiltMirror) {
		this.fineTiltMirror = fineTiltMirror;
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


	public Set<ReferenceBeam> getReferenceBeamSet() {
		return referenceBeamSet;
	}

	public void setReferenceBeamSet(Set<ReferenceBeam> referenceBeamSet) {
		this.referenceBeamSet = referenceBeamSet;
	}

	public Long getCameraId() {
		return cameraId;
	}

	public void setCameraId(Long cameraId) {
		this.cameraId = cameraId;
	}

	// return the ref beam with the closest wavelength
	public ReferenceBeam getReferenceBeamByWavelength(float wavelength) {
		
		ReferenceBeam bestCandidate = null;
		float lowestDifference = 100000.0f;
		
		for (ReferenceBeam candidate : referenceBeamSet) {
			
			float difference = Math.abs(candidate.getWavelength() - wavelength);
			
			if (difference < lowestDifference) {
				lowestDifference = difference;
				bestCandidate = candidate;
			}
		}
		return bestCandidate;
	}

	public List<ReferenceBeam> getOrderedReferenceBeamList() {
		List<ReferenceBeam> refBeamList = new ArrayList<ReferenceBeam>(referenceBeamSet);
		
		Collections.sort(refBeamList, new BeanComparator("refBeamNum"));
		
		return refBeamList;
	}
	
}
