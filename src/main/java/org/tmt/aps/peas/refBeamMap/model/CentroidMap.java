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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

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
	
	@Column
	String centroidMapData;
	
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


	@Transient
	protected List<FloatPoint> values;

	public List<FloatPoint> getValues() {
		return values;
	}

	public void setValues(List<FloatPoint> values) {
		this.values = values;
	}


	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("\nvalues = ");
		for (int i=0; i<values.size(); i++) {
			buf.append(values.get(i) + ", ");
		}
		buf.append("\n");
		return buf.toString();

	}

	public boolean isNewRecord() {
		return centroidMapId == null;
	}


}
