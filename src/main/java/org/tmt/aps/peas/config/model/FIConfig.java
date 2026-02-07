/**
 * @author Scott Michaels
 * Copyright (C) 2014 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Configuration entity class representing the FIConfig table
 * @author smichaels
 */
@Entity
@Table(name = "FIConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class FIConfig {

	
	public static final int FORCE_SOURCE_REF_MAP = 1;
	public static final int FORCE_SOURCE_USER_ENTERED = 2;
	
	@Id
	@SequenceGenerator(
		    name = "fiConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
		@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "fiConfig_gen"
		)

	private Long fiConfigId;


	//private float horizSubimageSpacing;
	//private float horizSubimageCount;
	private float uEst;
	private float uDelta0;
	

	private int matchbox;
	private int nThresh0;
	private float matchFineThresh;
	private int nPeakMinThresh;
	private int nPeakMaxThresh;
	private int lensletOrientation;
	private int spiralRingCount;

	private boolean forceScale;
	private boolean forceRotation;
	
	private float fracFilledThresh;
	private float fracFilledAnalysisThresh;
	private float fourierQualityThresh;
	
	private float forceRotationValue;
	private float forceScaleValue;
	private int forceRotationSource;
	private int forceScaleSource;

	public FIConfig() {
		
	}
	
	public FIConfig(FIConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.fiConfigId = null;
	}
	
	
	

	public Long getFiConfigId() {
		return fiConfigId;
	}

	public void setFiConfigId(Long fiConfigId) {
		this.fiConfigId = fiConfigId;
	}

	public float getuEst() {
		return uEst;
	}

	public void setuEst(float uEst) {
		this.uEst = uEst;
	}

	public float getuDelta0() {
		return uDelta0;
	}

	public void setuDelta0(float uDelta0) {
		this.uDelta0 = uDelta0;
	}

	public int getMatchbox() {
		return matchbox;
	}

	public void setMatchbox(int matchbox) {
		this.matchbox = matchbox;
	}

	public int getnThresh0() {
		return nThresh0;
	}

	public void setnThresh0(int nThresh0) {
		this.nThresh0 = nThresh0;
	}

	public float getMatchFineThresh() {
		return matchFineThresh;
	}

	public void setMatchFineThresh(float matchFineThresh) {
		this.matchFineThresh = matchFineThresh;
	}

	public int getnPeakMinThresh() {
		return nPeakMinThresh;
	}

	public void setnPeakMinThresh(int nPeakMinThresh) {
		this.nPeakMinThresh = nPeakMinThresh;
	}

	public int getnPeakMaxThresh() {
		return nPeakMaxThresh;
	}

	public void setnPeakMaxThresh(int nPeakMaxThresh) {
		this.nPeakMaxThresh = nPeakMaxThresh;
	}

	public int getLensletOrientation() {
		return lensletOrientation;
	}

	public void setLensletOrientation(int lensletOrientation) {
		this.lensletOrientation = lensletOrientation;
	}

	public int getSpiralRingCount() {
		return spiralRingCount;
	}

	public void setSpiralRingCount(int spiralRingCount) {
		this.spiralRingCount = spiralRingCount;
	}

	public boolean isForceScale() {
		return forceScale;
	}

	public void setForceScale(boolean forceScale) {
		this.forceScale = forceScale;
	}

	public boolean isForceRotation() {
		return forceRotation;
	}

	public void setForceRotation(boolean forceRotation) {
		this.forceRotation = forceRotation;
	}

	public float getFracFilledThresh() {
		return fracFilledThresh;
	}

	public void setFracFilledThresh(float fracFilledThresh) {
		this.fracFilledThresh = fracFilledThresh;
	}

	public float getFracFilledAnalysisThresh() {
		return fracFilledAnalysisThresh;
	}

	public void setFracFilledAnalysisThresh(float fracFilledAnalysisThresh) {
		this.fracFilledAnalysisThresh = fracFilledAnalysisThresh;
	}

	public float getFourierQualityThresh() {
		return fourierQualityThresh;
	}

	public void setFourierQualityThresh(float fourierQualityThresh) {
		this.fourierQualityThresh = fourierQualityThresh;
	}

	public float getForceRotationValue() {
		return forceRotationValue;
	}

	public void setForceRotationValue(float forceRotationValue) {
		this.forceRotationValue = forceRotationValue;
	}

	public float getForceScaleValue() {
		return forceScaleValue;
	}

	public void setForceScaleValue(float forceScaleValue) {
		this.forceScaleValue = forceScaleValue;
	}

	public int getForceRotationSource() {
		return forceRotationSource;
	}

	public void setForceRotationSource(int forceRotationSource) {
		this.forceRotationSource = forceRotationSource;
	}

	public int getForceScaleSource() {
		return forceScaleSource;
	}

	public void setForceScaleSource(int forceScaleSource) {
		this.forceScaleSource = forceScaleSource;
	}


	public String toString() {
		
			StringBuffer buf = new StringBuffer();
			buf.append("FIConfig:");
			buf.append("\nuEst = " + uEst);
			buf.append("\nuDelta0 = " + uDelta0);
			buf.append("\nmatchbox = " + matchbox);
			buf.append("\nnThresh0 = " + nThresh0);
			buf.append("\nmatchFineThresh = " + matchFineThresh);
			buf.append("\nnPeakMinThresh = " + nPeakMinThresh);
			buf.append("\nnPeakMaxThresh = " + nPeakMaxThresh);
			buf.append("\nlensletOrientation = " + lensletOrientation);
			buf.append("\nspiralRingCount = " + spiralRingCount);
			buf.append("\nforceScale = " + forceScale);
			buf.append("\nforceRotation = " + forceRotation);
			buf.append("\nfracFilledThresh = " + fracFilledThresh);
			buf.append("\nfourierQualityThresh = " + fourierQualityThresh);
			buf.append("\nforceRotationValue = " + forceRotationValue);
			buf.append("\nforceScaleValue = " + forceScaleValue);
			buf.append("\nforceRotationSource = " + forceRotationSource);
			buf.append("\nforceScaleSource = " + forceScaleSource);
			buf.append("\n");


		return buf.toString();
	

	}
	
}
