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
 * Configuration entity class representing the AutoRefMapConfig table
 * @author smichaels
 */
@Entity
@Table(name = "AutoRefMapConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class AutoRefMapConfig {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long autoRefMapConfigId;

	private int numTrialsLimit; 
	private int refMapExpirationAge;
	private float coarseTiltChangeThresh;
	private float fineTiltChangeThresh;
	private float ccdTempChangeThresh;
	private int ccdGainNumber;
	
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

	public int getCcdGainNumber() {
		return ccdGainNumber;
	}

	public void setCcdGainNumber(int ccdGainNumber) {
		this.ccdGainNumber = ccdGainNumber;
	}


	
	
}
	