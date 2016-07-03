/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Database entity representing a row in the CentroidMap table
 * Also contains a Transient derived value for FindCentroidsResult, which is derived from decoded strings in the database table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "CentroidMap")

public class CentroidMap {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long centroidMapId;

	@Column(insertable=false, updatable=false)
	private Long pupilMaskTypeId;
	
		
	float scale;
	float rotation;
	boolean forcedScaleFlg;
	Float forcedScale;
	boolean forcedRotationFlg;
	Float forcedRotation;
	float translationX;   
	float translationY;
	float fourierQuality;
	int numFilledBoxes;
	float fracFilledBoxes;
	Float medianPeakIntensity;
	
	@Column
	String fandiPredictedCentroidMapData; 
	
	@Column
	String fandiPeakCentroidMapData; 
	
	@Column
	String nDetectData; 
	
	int emptyBoxCount;
	
	int singleDetectBoxCount;
	int doubleDetectBoxCount;
	int manyDetectBoxCount;
	int translationSolutionCount;
	
	@Column
	String centroidMapData;
	@Column
	String intensityMapData;
	@Column
	String peakMapData;
	@Column
	String findCentStatusData;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	

	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	
	public Long getCentroidMapId() {
		return centroidMapId;
	}

	public void setCentroidMapId(Long centroidMapId) {
		this.centroidMapId = centroidMapId;
	}

	public Long getPupilMaskTypeId() {
		return pupilMaskTypeId;
	}

	public void setPupilMaskTypeId(Long pupilMaskTypeId) {
		this.pupilMaskTypeId = pupilMaskTypeId;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public boolean isForcedScaleFlg() {
		return forcedScaleFlg;
	}

	public void setForcedScaleFlg(boolean forcedScaleFlg) {
		this.forcedScaleFlg = forcedScaleFlg;
	}

	public Float getForcedScale() {
		return forcedScale;
	}

	public void setForcedScale(Float forcedScale) {
		this.forcedScale = forcedScale;
	}

	public boolean isForcedRotationFlg() {
		return forcedRotationFlg;
	}

	public void setForcedRotationFlg(boolean forcedRotationFlg) {
		this.forcedRotationFlg = forcedRotationFlg;
	}

	public Float getForcedRotation() {
		return forcedRotation;
	}

	public void setForcedRotation(Float forcedRotation) {
		this.forcedRotation = forcedRotation;
	}

	public String getCentroidMapData() {
		return centroidMapData;
	}

	public void setCentroidMapData(String centroidMapData) {
		this.centroidMapData = centroidMapData;
	}

	public void setFindCentStatusData(String findCentStatusData) {
		this.findCentStatusData = findCentStatusData;
	}

	public String getFindCentStatusData() {
		return findCentStatusData;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}


	public float getTranslationX() {
		return translationX;
	}

	public void setTranslationX(float translationX) {
		this.translationX = translationX;
	}

	public float getTranslationY() {
		return translationY;
	}

	public void setTranslationY(float translationY) {
		this.translationY = translationY;
	}

	public float getFourierQuality() {
		return fourierQuality;
	}

	public void setFourierQuality(float fourierQuality) {
		this.fourierQuality = fourierQuality;
	}

	public int getNumFilledBoxes() {
		return numFilledBoxes;
	}

	public void setNumFilledBoxes(int numFilledBoxes) {
		this.numFilledBoxes = numFilledBoxes;
	}

	public float getFracFilledBoxes() {
		return fracFilledBoxes;
	}

	public void setFracFilledBoxes(float fracFilledBoxes) {
		this.fracFilledBoxes = fracFilledBoxes;
	}

	public String getIntensityMapData() {
		return intensityMapData;
	}

	public void setIntensityMapData(String intensityMapData) {
		this.intensityMapData = intensityMapData;
	}

	public String getPeakMapData() {
		return peakMapData;
	}

	public void setPeakMapData(String peakMapData) {
		this.peakMapData = peakMapData;
	}

	public Float getMedianPeakIntensity() {
		return medianPeakIntensity;
	}

	public void setMedianPeakIntensity(Float medianPeakIntensity) {
		this.medianPeakIntensity = medianPeakIntensity;
	}


	public String getFandiPredictedCentroidMapData() {
		return fandiPredictedCentroidMapData;
	}

	public void setFandiPredictedCentroidMapData(String fandiPredictedCentroidMapData) {
		this.fandiPredictedCentroidMapData = fandiPredictedCentroidMapData;
	}

	public String getFandiPeakCentroidMapData() {
		return fandiPeakCentroidMapData;
	}

	public void setFandiPeakCentroidMapData(String fandiPeakCentroidMapData) {
		this.fandiPeakCentroidMapData = fandiPeakCentroidMapData;
	}

	public String getnDetectData() {
		return nDetectData;
	}

	public void setnDetectData(String nDetectData) {
		this.nDetectData = nDetectData;
	}

	public int getEmptyBoxCount() {
		return emptyBoxCount;
	}

	public void setEmptyBoxCount(int emptyBoxCount) {
		this.emptyBoxCount = emptyBoxCount;
	}

	public int getSingleDetectBoxCount() {
		return singleDetectBoxCount;
	}

	public void setSingleDetectBoxCount(int singleDetectBoxCount) {
		this.singleDetectBoxCount = singleDetectBoxCount;
	}

	public int getDoubleDetectBoxCount() {
		return doubleDetectBoxCount;
	}

	public void setDoubleDetectBoxCount(int doubleDetectBoxCount) {
		this.doubleDetectBoxCount = doubleDetectBoxCount;
	}

	public int getManyDetectBoxCount() {
		return manyDetectBoxCount;
	}

	public void setManyDetectBoxCount(int manyDetectBoxCount) {
		this.manyDetectBoxCount = manyDetectBoxCount;
	}

	public int getTranslationSolutionCount() {
		return translationSolutionCount;
	}

	public void setTranslationSolutionCount(int translationSolutionCount) {
		this.translationSolutionCount = translationSolutionCount;
	}


	@Transient
	protected FindCentroidsResult findCentroidsResult;

	/**
	 * Returns a derived FindCentroidsResult by decoding the centroid map data, the intensity map data, the peak map data and the find cent status data.
	 * @return the derived value
	 */
	public FindCentroidsResult getFindCentroidsResult() {
		
		if (findCentroidsResult == null) {
			// populate for first time
			// decode String into transient FloatPoint values
			List<FloatPoint> centroidList = FloatPointListEncoder.decodeList(getCentroidMapData());
			List<Float> intensityList = FloatListEncoder.decodeList(getIntensityMapData());
			List<Float> peakList = FloatListEncoder.decodeList(getPeakMapData());
			List<Integer> findCentStatus = IntegerListEncoder.decodeList(getFindCentStatusData());
			
			findCentroidsResult = new FindCentroidsResult(centroidList, intensityList, peakList, findCentStatus);
		}
		
		return findCentroidsResult;
	}

	public void setFindCentroidsResult(FindCentroidsResult findCentroidsResult) {
		this.findCentroidsResult = findCentroidsResult;
	}

	public String toString() {
		
		return findCentroidsResult.toString();

	}

	public boolean isNewRecord() {
		return centroidMapId == null;
	}


}
