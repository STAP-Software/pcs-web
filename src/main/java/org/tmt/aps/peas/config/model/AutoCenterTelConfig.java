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
 * Configuration entity class representing the AutoCenterTelConfig table
 * @author smichaels
 *
 */
@Entity
@Table(name = "AutoCenterTelConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class AutoCenterTelConfig {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	