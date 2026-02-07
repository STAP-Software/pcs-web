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
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.MessageGenerator;

/**
 * Configuration entity class representing the CalcM2M1Config table
 * @author smichaels
 */
@Entity
@Table(name = "CalcM2M1Config")
@Inheritance(strategy=InheritanceType.JOINED)
public class CalcM2M1Config {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@SequenceGenerator(
		    name = "calcM2M1Config_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "calcM2M1Config_gen"
		)
	private Long calcM2M1ConfigId;
	
	private int calcMethod; // Ray Trace vs Zernike
	private float m2TTUnitPertibation; // arcsec
	private float m2PistonUnitPertibation; // microns
	
	// Analytical
	private int analyticalCalcStartSeg; // start segment for analytical calc
	private int analyticalCalcEndSeg; // end segment for analytical calc


	
	public CalcM2M1Config() {
		
	}


	public CalcM2M1Config(CalcM2M1Config source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.calcM2M1ConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public Long getCalcM2M1ConfigId() {
		return calcM2M1ConfigId;
	}

	public void setCalcM2M1ConfigId(Long calcM2M1ConfigId) {
		this.calcM2M1ConfigId = calcM2M1ConfigId;
	}

	public int getCalcMethod() {
		return calcMethod;
	}

	public void setCalcMethod(int calcMethod) {
		this.calcMethod = calcMethod;
	}

	public float getM2TTUnitPertibation() {
		return m2TTUnitPertibation;
	}

	public void setM2TTUnitPertibation(float m2ttUnitPertibation) {
		m2TTUnitPertibation = m2ttUnitPertibation;
	}

	public float getM2PistonUnitPertibation() {
		return m2PistonUnitPertibation;
	}

	public void setM2PistonUnitPertibation(float m2PistonUnitPertibation) {
		this.m2PistonUnitPertibation = m2PistonUnitPertibation;
	}

	public int getAnalyticalCalcStartSeg() {
		return analyticalCalcStartSeg;
	}


	public void setAnalyticalCalcStartSeg(int analyticalCalcStartSeg) {
		this.analyticalCalcStartSeg = analyticalCalcStartSeg;
	}


	public int getAnalyticalCalcEndSeg() {
		return analyticalCalcEndSeg;
	}


	public void setAnalyticalCalcEndSeg(int analyticalCalcEndSeg) {
		this.analyticalCalcEndSeg = analyticalCalcEndSeg;
	}


	public boolean isCalcMethodRayTrace() {
		return calcMethod == Constants.CALC_M2_METHOD_RAY_TRACE;
	}

	public boolean isCalcMethodZernike() {
		return calcMethod == Constants.CALC_M2_METHOD_ZERNIKE;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("CalcM2M1Config:");
		buf.append("\ncalcMethod = " + calcMethod);
		buf.append("\nm2TTUnitPertibation = " + m2TTUnitPertibation);
		buf.append("\nm2PistonUnitPertibation = " + m2PistonUnitPertibation);
		buf.append("\n");

		return buf.toString();
	}

}
