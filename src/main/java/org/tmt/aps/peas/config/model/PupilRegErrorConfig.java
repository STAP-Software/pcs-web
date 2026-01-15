/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Configuration entity class representing the PupilRegErrorConfig table
 * @author smichaels
 */
@Entity
@Table(name = "PupilRegErrorConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class PupilRegErrorConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long pupilRegErrorConfigId;
	
	private int fractionalIntensityCalcMethod;
	private int nStart;
	private float centerPupilThresh;
	private float smallCommandGainFactor;  // meters
	private float largeCommandGainFactor; // meters
	private float smallLargeCommandThreshold; // meters
	private float frameOkThreshold; // mm
	private float pupilRotationThreshold;

	public PupilRegErrorConfig() {
		
	}


	public PupilRegErrorConfig(PupilRegErrorConfig source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.pupilRegErrorConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	
	public Long getPupilRegErrorConfigId() {
		return pupilRegErrorConfigId;
	}

	public void setPupilRegErrorConfigId(Long pupilRegErrorConfigId) {
		this.pupilRegErrorConfigId = pupilRegErrorConfigId;
	}

	public int getFractionalIntensityCalcMethod() {
		return fractionalIntensityCalcMethod;
	}

	public void setFractionalIntensityCalcMethod(int fractionalIntensityCalcMethod) {
		this.fractionalIntensityCalcMethod = fractionalIntensityCalcMethod;
	}

	public int getnStart() {
		return nStart;
	}

	public void setnStart(int nStart) {
		this.nStart = nStart;
	}

	public float getCenterPupilThresh() {
		return centerPupilThresh;
	}

	public void setCenterPupilThresh(float centerPupilThresh) {
		this.centerPupilThresh = centerPupilThresh;
	}

	public float getSmallCommandGainFactor() {
		return smallCommandGainFactor;
	}

	public void setSmallCommandGainFactor(float smallCommandGainFactor) {
		this.smallCommandGainFactor = smallCommandGainFactor;
	}

	public float getLargeCommandGainFactor() {
		return largeCommandGainFactor;
	}

	public void setLargeCommandGainFactor(float largeCommandGainFactor) {
		this.largeCommandGainFactor = largeCommandGainFactor;
	}

	public float getSmallLargeCommandThreshold() {
		return smallLargeCommandThreshold;
	}

	public void setSmallLargeCommandThreshold(float smallLargeCommandThreshold) {
		this.smallLargeCommandThreshold = smallLargeCommandThreshold;
	}


	public float getFrameOkThreshold() {
		return frameOkThreshold;
	}


	public void setFrameOkThreshold(float frameOkThreshold) {
		this.frameOkThreshold = frameOkThreshold;
	}


	public float getPupilRotationThreshold() {
		return pupilRotationThreshold;
	}


	public void setPupilRotationThreshold(float pupilRotationThreshold) {
		this.pupilRotationThreshold = pupilRotationThreshold;
	}


	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("PupilRegErrorConfig:");
		buf.append("\nfractionalIntensityCalcMethod = " + fractionalIntensityCalcMethod);
		buf.append("\nnStart = " + nStart);
		buf.append("\nsmallCommandGainFactor = " + smallCommandGainFactor);
		buf.append("\nlargeCommandGainFactor = " + largeCommandGainFactor);
		buf.append("\nsmallLargeCommandThreshold = " + smallLargeCommandThreshold);
		buf.append("\n");

		return buf.toString();
	}


}
