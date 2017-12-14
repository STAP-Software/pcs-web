/**
 * @author Scott Michaels
 * Copyright (C) 2014 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Configuration entity class representing the FrameCorrectionConfig table
 * @author smichaels
 *
 */
@Entity
@Table(name = "FrameCorrectionConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class FrameCorrectionConfig {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long frameCorrectionConfigId;

	int leftRightBiasThreshold;
	float badPixelIndexThreshold;
	int badPixelIntensityThreshold;
	
	public FrameCorrectionConfig() {
		
	}
	
	public FrameCorrectionConfig(FrameCorrectionConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.frameCorrectionConfigId = null;
	}

	public Long getFrameCorrectionConfigId() {
		return frameCorrectionConfigId;
	}

	public void setFrameCorrectionConfigId(Long frameCorrectionConfigId) {
		this.frameCorrectionConfigId = frameCorrectionConfigId;
	}

	public int getLeftRightBiasThreshold() {
		return leftRightBiasThreshold;
	}

	public void setLeftRightBiasThreshold(int leftRightBiasThreshold) {
		this.leftRightBiasThreshold = leftRightBiasThreshold;
	}

	public float getBadPixelIndexThreshold() {
		return badPixelIndexThreshold;
	}

	public void setBadPixelIndexThreshold(float badPixelIndexThreshold) {
		this.badPixelIndexThreshold = badPixelIndexThreshold;
	}

	public int getBadPixelIntensityThreshold() {
		return badPixelIntensityThreshold;
	}

	public void setBadPixelIntensityThreshold(int badPixelIntensityThreshold) {
		this.badPixelIntensityThreshold = badPixelIntensityThreshold;
	}




	
}
	