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
import jakarta.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Configuration entity class representing the CentroidOffsetsConfig table
 * @author smichaels
 */
@Entity
@Table(name = "CentroidOffsetsConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class CentroidOffsetsConfig {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long centroidOffsetsConfigId;

	private boolean removeScale;
	private boolean removeRotation;
	private float sufsIgnoreSubimageThreshold;
	
	public CentroidOffsetsConfig() {
		
	}
	
	public CentroidOffsetsConfig(CentroidOffsetsConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.centroidOffsetsConfigId = null;
	}

	public Long getCentroidOffsetsConfigId() {
		return centroidOffsetsConfigId;
	}

	public void setCentroidOffsetsConfigId(Long centroidOffsetsConfigId) {
		this.centroidOffsetsConfigId = centroidOffsetsConfigId;
	}

	public boolean isRemoveScale() {
		return removeScale;
	}

	public void setRemoveScale(boolean removeScale) {
		this.removeScale = removeScale;
	}

	public boolean isRemoveRotation() {
		return removeRotation;
	}

	public void setRemoveRotation(boolean removeRotation) {
		this.removeRotation = removeRotation;
	}

	public float getSufsIgnoreSubimageThreshold() {
		return sufsIgnoreSubimageThreshold;
	}

	public void setSufsIgnoreSubimageThreshold(float sufsIgnoreSubimageThreshold) {
		this.sufsIgnoreSubimageThreshold = sufsIgnoreSubimageThreshold;
	}
	
	
}
	
	
	