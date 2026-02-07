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
 * Configuration entity class representing the AutoCenterTelConfig table
 * @author smichaels
 *
 */
@Entity
@Table(name = "AutoCenterTelConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class AutoCenterTelConfig {

	
	@Id
	@SequenceGenerator(
		    name = "autoCenterTelConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "autoCenterTelConfig_gen"
		)
	private Long autoCenterTelConfigId;

	float moveTelFrameOkThreshold;
	float retakeFrameThreshold;
	float telMoveTooLargeThreshold;
	
	public AutoCenterTelConfig() {
		
	}
	
	public AutoCenterTelConfig(AutoCenterTelConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.autoCenterTelConfigId = null;
	}

	public Long getAutoCenterTelConfigId() {
		return autoCenterTelConfigId;
	}

	public void setAutoCenterTelConfigId(Long autoCenterTelConfigId) {
		this.autoCenterTelConfigId = autoCenterTelConfigId;
	}

	public float getMoveTelFrameOkThreshold() {
		return moveTelFrameOkThreshold;
	}

	public void setMoveTelFrameOkThreshold(float moveTelFrameOkThreshold) {
		this.moveTelFrameOkThreshold = moveTelFrameOkThreshold;
	}

	public float getRetakeFrameThreshold() {
		return retakeFrameThreshold;
	}

	public void setRetakeFrameThreshold(float retakeFrameThreshold) {
		this.retakeFrameThreshold = retakeFrameThreshold;
	}

	public float getTelMoveTooLargeThreshold() {
		return telMoveTooLargeThreshold;
	}

	public void setTelMoveTooLargeThreshold(float telMoveTooLargeThreshold) {
		this.telMoveTooLargeThreshold = telMoveTooLargeThreshold;
	}


	
}
	