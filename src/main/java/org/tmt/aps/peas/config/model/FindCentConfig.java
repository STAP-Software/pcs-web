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
@Table(name = "FindCentConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class FindCentConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long findCentConfigId;
	
	private int irad;
	private int imargin;
	private int ngauss;
	private int itermax;

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

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("FindCentConfig:");
		buf.append("\nirad = " + irad);
		buf.append("\nimargin = " + imargin);
		buf.append("\nngauss = " + ngauss);
		buf.append("\nitermax = " + itermax);
		buf.append("\n");

		return buf.toString();
	}
}
