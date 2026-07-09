/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;


/**
 * Instrument configuration Entity class representing the Camera table.  Contains <code>@Transient</code> fields used to store current state information for
 * the current ref beam, shutter state, two position mechanism state and instrument and electronics box temperatures.
 * @author smichaels
 *
 */
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
	@Transient
	private float instrumentHumidity;
	@Transient
	private float electronicsBoxHumidity;
	@Transient
	private boolean temperatureInterlock;
	@Transient
	private boolean purgeState;
	@Transient
	private boolean glycolFlow;
	@Transient
	private int overallStatus;
	@Transient
	private int overallPowerState;
	@Transient
	private int ccdPowerState;
	@Transient
	private int galilPowerState;
	


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
	
	public String getCurrentRefBeamDisplayString() {
		if (currentRefBeam == 0) {
			return "Off";
		} else {
			return "" + currentRefBeam;
		}
	}
	
	public String getOverallStatusDisplayString() {
		if (overallStatus == CameraQueryResult.READY) {
			return "Ready";
		} else if (overallStatus == CameraQueryResult.NOT_READY) {
			return "Not Ready";
		} else {
			return "Unknown";
		}
	}
	
	private String powerStateDisplayString(int powerState) {
		return (powerState == CameraCommand.ON) ? "On" : ((powerState == CameraCommand.OFF) ? "Off" : "Unknown");
	}
	
	public String getOverallPowerStateDisplayString() {
		return powerStateDisplayString(overallPowerState);
	}
	public String getCcdPowerStateDisplayString() {
		return powerStateDisplayString(ccdPowerState);
	}
	public String getGalilPowerStateDisplayString() {
		return powerStateDisplayString(galilPowerState);
	}
	
	
	public int getCurrentRefBeam() {
		return currentRefBeam;
	}

	public void setCurrentRefBeam(int currentRefBeam) {
		this.currentRefBeam = currentRefBeam;
	}

	public int getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(int overallStatus) {
		this.overallStatus = overallStatus;
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

	public float getInstrumentHumidity() {
		return instrumentHumidity;
	}

	public void setInstrumentHumidity(float instrumentHumidity) {
		this.instrumentHumidity = instrumentHumidity;
	}

	public float getElectronicsBoxHumidity() {
		return electronicsBoxHumidity;
	}

	public void setElectronicsBoxHumidity(float electronicsBoxHumidity) {
		this.electronicsBoxHumidity = electronicsBoxHumidity;
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

	public int getCcdPowerState() {
		return ccdPowerState;
	}

	public void setCcdPowerState(int ccdPowerState) {
		this.ccdPowerState = ccdPowerState;
	}



	public int getGalilPowerState() {
		return galilPowerState;
	}

	public void setGalilPowerState(int galilPowerState) {
		this.galilPowerState = galilPowerState;
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

	/**
	 * Returns the reference beam record with the closest wavelength to the passed wavelength
	 * @param wavelength the wavelength to compare with
	 * @return the reference beam closest to the passed wavelength
	 */
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

	/**
	 * @return a list of reference beam records ordered by number
	 */
	public List<ReferenceBeam> getOrderedReferenceBeamList() {
		List<ReferenceBeam> refBeamList = new ArrayList<ReferenceBeam>(referenceBeamSet);
		
		refBeamList.sort(Comparator.comparing(ReferenceBeam::getRefBeamNum));

		
		return refBeamList;
	}
	


}
