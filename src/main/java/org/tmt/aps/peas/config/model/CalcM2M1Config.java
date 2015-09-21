/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "CalcM2M1Config")
@Inheritance(strategy=InheritanceType.JOINED)
public class CalcM2M1Config {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long calcM2M1ConfigId;
	
	private int calcMethod; // Ray Trace vs Zernike (TODO: add to UI)
	private float m2TTUnitPertibation; // arcsec
	private float m2PistonUnitPertibation; // microns

	
	
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
