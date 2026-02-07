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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;

/**
 * Configuration entity class representing the FindCentConfig table
 * @author smichaels
 */
@Entity
@Table(name = "FindCentConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class FindCentConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@SequenceGenerator(
		    name = "findCentConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
		@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "findCentConfig_gen"
		)
	private Long findCentConfigId;
	
	private int irad;
	private int imargin;
	private int ngauss;
	private int itermax;
	private float subimageIntensityThreshold;
	private boolean ignoreNdectZeroSpots;
	private int maxNonLinearPeakCount;

	
	public FindCentConfig() {
		
	}

	public FindCentConfig(FindCentConfig source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.findCentConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	
	public Long getFindCentConfigId() {
		return findCentConfigId;
	}

	public void setFindCentConfigId(Long findCentConfigId) {
		this.findCentConfigId = findCentConfigId;
	}

	public int getIrad() {
		return irad;
	}

	public void setIrad(int irad) {
		this.irad = irad;
	}

	public int getImargin() {
		return imargin;
	}

	public void setImargin(int imargin) {
		this.imargin = imargin;
	}

	public int getNgauss() {
		return ngauss;
	}

	public void setNgauss(int ngauss) {
		this.ngauss = ngauss;
	}

	public int getItermax() {
		return itermax;
	}

	public void setItermax(int itermax) {
		this.itermax = itermax;
	}

	public float getSubimageIntensityThreshold() {
		return subimageIntensityThreshold;
	}

	public void setSubimageIntensityThreshold(float subimageIntensityThreshold) {
		this.subimageIntensityThreshold = subimageIntensityThreshold;
	}

	public boolean isIgnoreNdectZeroSpots() {
		return ignoreNdectZeroSpots;
	}

	public void setIgnoreNdectZeroSpots(boolean ignoreNdectZeroSpots) {
		this.ignoreNdectZeroSpots = ignoreNdectZeroSpots;
	}

	public int getMaxNonLinearPeakCount() {
		return maxNonLinearPeakCount;
	}

	public void setMaxNonLinearPeakCount(int maxNonLinearPeakCount) {
		this.maxNonLinearPeakCount = maxNonLinearPeakCount;
	}
	

	@Override
	public String toString() {
		return "FindCentConfig [logger=" + logger + ", findCentConfigId=" + findCentConfigId + ", irad=" + irad + ", imargin=" + imargin
				+ ", ngauss=" + ngauss + ", itermax=" + itermax + ", subimageIntensityThreshold=" + subimageIntensityThreshold
				+ ", ignoreNdectZeroSpots=" + ignoreNdectZeroSpots + ", maxNonLinearPeakCount=" + maxNonLinearPeakCount + "]";
	}
}
