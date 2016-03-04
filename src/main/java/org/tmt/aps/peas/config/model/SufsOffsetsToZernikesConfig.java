/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;

@Entity
@Table(name = "SufsOffsetsToZernikesConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class SufsOffsetsToZernikesConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long sufsOffsetsToZernikesConfigId;
	
	@Column (name="numberOfZernikes")
	private String numberOfZernikesEncoded;

	
	public SufsOffsetsToZernikesConfig() {
		
	}

	public SufsOffsetsToZernikesConfig(SufsOffsetsToZernikesConfig source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.sufsOffsetsToZernikesConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public Long getSufsOffsetsToZernikesConfigId() {
		return sufsOffsetsToZernikesConfigId;
	}

	public void setSufsOffsetsToZernikesConfigId(Long sufsOffsetsToZernikesConfigId) {
		this.sufsOffsetsToZernikesConfigId = sufsOffsetsToZernikesConfigId;
	}

	public String getNumberOfZernikesEncoded() {
		return numberOfZernikesEncoded;
	}

	public void setNumberOfZernikesEncoded(String numberOfZernikesEncoded) {
		this.numberOfZernikesEncoded = numberOfZernikesEncoded;
	}

	@Transient
	Integer[] numberOfZernikes;
	
	public int getNumberOfZernikes(int segmentNumber) {
		
		if (numberOfZernikes == null) {	
			numberOfZernikes = IntegerListEncoder.decodeList(numberOfZernikesEncoded).toArray(new Integer[0]);
		}
		return numberOfZernikes[segmentNumber].intValue();
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("SufsOffsetsToZernikesConfig:");
		buf.append("\nnumberOfZernikesEncoded = " + numberOfZernikesEncoded);
		buf.append("\n");

		return buf.toString();
	}
}
