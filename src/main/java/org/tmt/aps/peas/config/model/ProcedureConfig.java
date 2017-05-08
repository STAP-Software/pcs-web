/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.instrument.model.CcdGain;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

/**
 * Configuration entity class representing the ProcedureConfig table
 * @author smichaels
 */
@Entity
@Table(name = "ProcedureConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class ProcedureConfig {

	public static final int FRAME_SOURCE_CCD = 1;
	public static final int FRAME_SOURCE_FILE = 2;
	
	public static final int LIGHT_SOURCE_STAR = 1;
	public static final int LIGHT_SOURCE_LED = 2;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureConfigId;

	private boolean defaultFlg;

		
	private Float integrationTime;
	@Column(name="numTrials")
	private int numberOfTrials = 1;
	private int frameSource;
	private int lightSource;

	private Integer ufsSegment;
	private Integer sufsGroup;
	
	private Integer coarsePhasingOption; 
	private Integer phasingSteps; 
	private Float phasingStepSize; 


	@Column(name="imageScaleRotationRemoval")
	private int frameScaleRotationRemoval;
	@Column(name="autoCenterTelescopeFlg")
	private int autoCenterTelescope;
	@Column(name="autoCenterPupilFlg")
	private int autoCenterPupil;
	private int autoCenterPupilMechanism;
	@Column(name="autoSendActuatorCmdsFlg")
	private int autoSendActuatorCmds;
	@Column(name="autoCommandSecondaryFlg")
	private int autoCommandSecondary;
	@Column(name="autoTakeRefBeamFlg")
	private int autoTakeRefBeam;
	
	private boolean autoDisplayCentroids;
	private boolean autoDisplayCentroidOffsets;
	private boolean autoDisplayAvgPtCentroidOffsets;
	private boolean autoDisplayAvgFsCentroidOffsets;
	private boolean autoDisplayAvgSufsCentroidOffsets;
	private boolean autoDisplayActuatorDeltas;
	private boolean autoDisplayEdgeHeights;
	private boolean autoDisplaySingleFilterEdgeHeights;
	private boolean autoDisplayResiduals;
	private boolean autoDisplaySubimageIntensityWarning;
	
	boolean removeBadPixels;
	
	int autoPointTelescopeSufsGroup;
	private int ccdGainNumber;

	
	@Column(nullable=false, length=255)
	String intTimeSelectOptions;
	
	@Column(nullable=false, length=255)
	String numTrialsSelectOptions;
	
	@Transient
	List<Float> integrationTimeList; 
	@Transient
	List<Integer> numTrialsList; 


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "filterTypeId")
	FilterType filterType;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "pupilMaskTypeId")
	PupilMaskType pupilMaskType;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "pupilMaskId")
	PupilMask pupilMask;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "filterId")
	Filter filter;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "referenceBeamId")
	ReferenceBeam referenceBeam;

	public ProcedureConfig() {
		
	}
	
	public ProcedureConfig(ProcedureConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.procedureConfigId = null;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Float getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(Float integrationTime) {
		this.integrationTime = integrationTime;
	}

	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(int numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}

	public int getFrameSource() {
		return frameSource;
	}

	public void setFrameSource(int frameSource) {
		this.frameSource = frameSource;
	}

	public Long getProcedureConfigId() {
		return procedureConfigId;
	}

	public void setProcedureConfigId(Long procedureConfigId) {
		this.procedureConfigId = procedureConfigId;
	}

	public int getFrameScaleRotationRemoval() {
		return frameScaleRotationRemoval;
	}

	public void setFrameScaleRotationRemoval(int frameScaleRotationRemoval) {
		this.frameScaleRotationRemoval = frameScaleRotationRemoval;
	}

	public int getAutoCenterTelescope() {
		return autoCenterTelescope;
	}

	public void setAutoCenterTelescope(int autoCenterTelescope) {
		this.autoCenterTelescope = autoCenterTelescope;
	}

	public int getAutoCenterPupil() {
		return autoCenterPupil;
	}

	public void setAutoCenterPupil(int autoCenterPupil) {
		this.autoCenterPupil = autoCenterPupil;
	}

	public int getAutoCenterPupilMechanism() {
		return autoCenterPupilMechanism;
	}

	public void setAutoCenterPupilMechanism(int autoCenterPupilMechanism) {
		this.autoCenterPupilMechanism = autoCenterPupilMechanism;
	}

	public int getAutoSendActuatorCmds() {
		return autoSendActuatorCmds;
	}

	public void setAutoSendActuatorCmds(int autoSendActuatorCmds) {
		this.autoSendActuatorCmds = autoSendActuatorCmds;
	}

	public int getAutoCommandSecondary() {
		return autoCommandSecondary;
	}

	public void setAutoCommandSecondary(int autoCommandSecondary) {
		this.autoCommandSecondary = autoCommandSecondary;
	}

	public Integer getCoarsePhasingOption() {
		return coarsePhasingOption;
	}

	public void setCoarsePhasingOption(Integer coarsePhasingOption) {
		this.coarsePhasingOption = coarsePhasingOption;
	}

	public Integer getPhasingSteps() {
		return phasingSteps;
	}

	public void setPhasingSteps(Integer phasingSteps) {
		this.phasingSteps = phasingSteps;
	}

	public Float getPhasingStepSize() {
		return phasingStepSize;
	}

	public void setPhasingStepSize(Float phasingStepSize) {
		this.phasingStepSize = phasingStepSize;
	}

	public boolean isDefaultFlg() {
		return defaultFlg;
	}

	public void setDefaultFlg(boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}

	public int getAutoTakeRefBeam() {
		return autoTakeRefBeam;
	}

	public void setAutoTakeRefBeam(int autoTakeRefBeam) {
		this.autoTakeRefBeam = autoTakeRefBeam;
	}


	public Integer getUfsSegment() {
		return ufsSegment;
	}

	public void setUfsSegment(Integer ufsSegment) {
		this.ufsSegment = ufsSegment;
	}

	public Integer getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(Integer sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public int getLightSource() {
		return lightSource;
	}

	public void setLightSource(int lightSource) {
		this.lightSource = lightSource;
	}

	public PupilMask getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(PupilMask pupilMask) {
		this.pupilMask = pupilMask;
	}

	public ReferenceBeam getReferenceBeam() {
		return referenceBeam;
	}

	public void setReferenceBeam(ReferenceBeam referenceBeam) {
		this.referenceBeam = referenceBeam;
	}

	public String getIntTimeSelectOptions() {
		return intTimeSelectOptions;
	}

	public void setIntTimeSelectOptions(String intTimeSelectOptions) {
		this.intTimeSelectOptions = intTimeSelectOptions;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public boolean isAutoDisplayCentroids() {
		return autoDisplayCentroids;
	}

	public void setAutoDisplayCentroids(boolean autoDisplayCentroids) {
		this.autoDisplayCentroids = autoDisplayCentroids;
	}

	public boolean isAutoDisplayCentroidOffsets() {
		return autoDisplayCentroidOffsets;
	}

	public void setAutoDisplayCentroidOffsets(boolean autoDisplayCentroidOffsets) {
		this.autoDisplayCentroidOffsets = autoDisplayCentroidOffsets;
	}

	public boolean isAutoDisplayAvgPtCentroidOffsets() {
		return autoDisplayAvgPtCentroidOffsets;
	}

	public void setAutoDisplayAvgPtCentroidOffsets(boolean autoDisplayAvgPtCentroidOffsets) {
		this.autoDisplayAvgPtCentroidOffsets = autoDisplayAvgPtCentroidOffsets;
	}

	public boolean isAutoDisplayAvgFsCentroidOffsets() {
		return autoDisplayAvgFsCentroidOffsets;
	}

	public void setAutoDisplayAvgFsCentroidOffsets(boolean autoDisplayAvgFsCentroidOffsets) {
		this.autoDisplayAvgFsCentroidOffsets = autoDisplayAvgFsCentroidOffsets;
	}

	public boolean isAutoDisplayAvgSufsCentroidOffsets() {
		return autoDisplayAvgSufsCentroidOffsets;
	}

	public void setAutoDisplayAvgSufsCentroidOffsets(boolean autoDisplayAvgSufsCentroidOffsets) {
		this.autoDisplayAvgSufsCentroidOffsets = autoDisplayAvgSufsCentroidOffsets;
	}

	public boolean isAutoDisplayActuatorDeltas() {
		return autoDisplayActuatorDeltas;
	}

	public void setAutoDisplayActuatorDeltas(boolean autoDisplayActuatorDeltas) {
		this.autoDisplayActuatorDeltas = autoDisplayActuatorDeltas;
	}

	public boolean isAutoDisplayEdgeHeights() {
		return autoDisplayEdgeHeights;
	}

	public void setAutoDisplayEdgeHeights(boolean autoDisplayEdgeHeights) {
		this.autoDisplayEdgeHeights = autoDisplayEdgeHeights;
	}

	public boolean isAutoDisplaySingleFilterEdgeHeights() {
		return autoDisplaySingleFilterEdgeHeights;
	}

	public void setAutoDisplaySingleFilterEdgeHeights(boolean autoDisplaySingleFilterEdgeHeights) {
		this.autoDisplaySingleFilterEdgeHeights = autoDisplaySingleFilterEdgeHeights;
	}

	public boolean isAutoDisplayResiduals() {
		return autoDisplayResiduals;
	}

	public void setAutoDisplayResiduals(boolean autoDisplayResiduals) {
		this.autoDisplayResiduals = autoDisplayResiduals;
	}

	public boolean isAutoDisplaySubimageIntensityWarning() {
		return autoDisplaySubimageIntensityWarning;
	}

	public void setAutoDisplaySubimageIntensityWarning(boolean autoDisplaySubimageIntensityWarning) {
		this.autoDisplaySubimageIntensityWarning = autoDisplaySubimageIntensityWarning;
	}

	public boolean isRemoveBadPixels() {
		return removeBadPixels;
	}

	public void setRemoveBadPixels(boolean removeBadPixels) {
		this.removeBadPixels = removeBadPixels;
	}

	public int getAutoPointTelescopeSufsGroup() {
		return autoPointTelescopeSufsGroup;
	}

	public void setAutoPointTelescopeSufsGroup(int autoPointTelescopeSufsGroup) {
		this.autoPointTelescopeSufsGroup = autoPointTelescopeSufsGroup;
	}

	public String getNumTrialsSelectOptions() {
		return numTrialsSelectOptions;
	}

	public void setNumTrialsSelectOptions(String numTrialsSelectOptions) {
		this.numTrialsSelectOptions = numTrialsSelectOptions;
	}

	public int getCcdGainNumber() {
		return ccdGainNumber;
	}

	public void setCcdGainNumber(int ccdGainNumber) {
		this.ccdGainNumber = ccdGainNumber;
	}

	public boolean isFrameFromFile() {
		return frameSource == FRAME_SOURCE_FILE;
	}

	public boolean isFrameFromCcd() {
		return frameSource == FRAME_SOURCE_CCD;
	}

	public List<Float> getIntegrationTimeList() {
		if (intTimeSelectOptions != null) {
			integrationTimeList =  FloatListEncoder.decodeList(intTimeSelectOptions);
			Collections.sort(integrationTimeList);
		}
		return integrationTimeList;
	}
	public void setIntegrationTimeList(List<Float> integrationTimeList) {
		this.integrationTimeList = integrationTimeList;
	}

	public List<Integer> getNumTrialsList() {
		
		if (numTrialsSelectOptions != null) {
			numTrialsList =  IntegerListEncoder.decodeList(numTrialsSelectOptions);
			Collections.sort(numTrialsList);
		}

		return numTrialsList;
	}

	public void setNumTrialsList(List<Integer> numTrialsList) {
		this.numTrialsList = numTrialsList;
	}

	public boolean isLightSourceLed() {
		// TODO Auto-generated method stub
		return lightSource == LIGHT_SOURCE_LED;
	}


}
