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
 * Configuration entity class representing the AutoRefMapConfig table
 * @author smichaels
 */
@Entity
@Table(name = "AutoRefMapConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class AutoRefMapConfig {

	
	@Id
	@SequenceGenerator(
		    name = "autoRefMapConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "autoRefMapConfig_gen"
		)
	private Long autoRefMapConfigId;

	private int numTrialsLimit; 
	private int refMapExpirationAge;
	private float coarseTiltChangeThresh;
	private float fineTiltChangeThresh;
	private float ccdTempChangeThresh;

	
	public AutoRefMapConfig() {
		
	}
	
	public AutoRefMapConfig(AutoRefMapConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.autoRefMapConfigId = null;
	}

	public Long getAutoRefMapConfigId() {
		return autoRefMapConfigId;
	}

	public void setAutoRefMapConfigId(Long autoRefMapConfigId) {
		this.autoRefMapConfigId = autoRefMapConfigId;
	}

	public int getNumTrialsLimit() {
		return numTrialsLimit;
	}

	public void setNumTrialsLimit(int numTrialsLimit) {
		this.numTrialsLimit = numTrialsLimit;
	}

	public int getRefMapExpirationAge() {
		return refMapExpirationAge;
	}

	public void setRefMapExpirationAge(int refMapExpirationAge) {
		this.refMapExpirationAge = refMapExpirationAge;
	}

	public float getCoarseTiltChangeThresh() {
		return coarseTiltChangeThresh;
	}

	public void setCoarseTiltChangeThresh(float coarseTiltChangeThresh) {
		this.coarseTiltChangeThresh = coarseTiltChangeThresh;
	}

	public float getCcdTempChangeThresh() {
		return ccdTempChangeThresh;
	}

	public void setCcdTempChangeThresh(float ccdTempChangeThresh) {
		this.ccdTempChangeThresh = ccdTempChangeThresh;
	}

	public float getFineTiltChangeThresh() {
		return fineTiltChangeThresh;
	}

	public void setFineTiltChangeThresh(float fineTiltChangeThresh) {
		this.fineTiltChangeThresh = fineTiltChangeThresh;
	}
	
	
}
	