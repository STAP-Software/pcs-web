/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;

/**
 * Configuration entity class representing the GlobalConfig table
 * @author smichaels
 */
@Entity
@Table(name = "GlobalConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class GlobalConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long globalConfigId;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date updateDate;
		
	int coarseMirrorX;
	int coarseMirrorY;
	int fineMirrorX;
	int fineMirrorY;
	float pupilRegistrationOffsetX;
	float pupilRegistrationOffsetY;

	String sufsZernikeOrderListEncoded;
	String mirrorListEncoded;

	
	public GlobalConfig() {
		
	}
	
	public GlobalConfig(GlobalConfig source) {
		
		try {
			
			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			
			BeanUtils.copyProperties(this, source);
			
			this.globalConfigId = null;
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}


	public int getCoarseMirrorX() {
		return coarseMirrorX;
	}

	public void setCoarseMirrorX(int coarseMirrorX) {
		this.coarseMirrorX = coarseMirrorX;
	}

	public int getCoarseMirrorY() {
		return coarseMirrorY;
	}

	public void setCoarseMirrorY(int coarseMirrorY) {
		this.coarseMirrorY = coarseMirrorY;
	}

	public int getFineMirrorX() {
		return fineMirrorX;
	}

	public void setFineMirrorX(int fineMirrorX) {
		this.fineMirrorX = fineMirrorX;
	}

	public int getFineMirrorY() {
		return fineMirrorY;
	}

	public void setFineMirrorY(int fineMirrorY) {
		this.fineMirrorY = fineMirrorY;
	}
	

	public float getPupilRegistrationOffsetX() {
		return pupilRegistrationOffsetX;
	}

	public void setPupilRegistrationOffsetX(float pupilRegistrationOffsetX) {
		this.pupilRegistrationOffsetX = pupilRegistrationOffsetX;
	}

	public float getPupilRegistrationOffsetY() {
		return pupilRegistrationOffsetY;
	}

	public void setPupilRegistrationOffsetY(float pupilRegistrationOffsetY) {
		this.pupilRegistrationOffsetY = pupilRegistrationOffsetY;
	}

	public Long getGlobalConfigId() {
		return globalConfigId;
	}

	public void setGlobalConfigId(Long globalConfigId) {
		this.globalConfigId = globalConfigId;
	}



	public String getSufsZernikeOrderListEncoded() {
		return sufsZernikeOrderListEncoded;
	}

	public void setSufsZernikeOrderListEncoded(String sufsZernikeOrderListEncoded) {
		this.sufsZernikeOrderListEncoded = sufsZernikeOrderListEncoded;
	}

	public int[] getSufsZernikeOrderArray() {
		return IntegerListEncoder.decodeListToArray(sufsZernikeOrderListEncoded);
	}
	

	
	public String getMirrorListEncoded() {
		return mirrorListEncoded;
	}

	public void setMirrorListEncoded(String mirrorListEncoded) {
		this.mirrorListEncoded = mirrorListEncoded;
	}
	
	public Integer[] getMirrorList() {
		return IntegerListEncoder.decodeListToObjectArray(mirrorListEncoded);
	}
	
	public int[] getMirrorListInt() throws Exception {
		return IntegerListEncoder.decodeToIntArray(mirrorListEncoded);
	}
	
	public int getMirrorCount() {
		int count = 0;
		for (Integer mirror : getMirrorList()) {
			if (mirror.intValue() == 1) count++;
		}
		return count;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Point getCoarseMirrorDefault() {
		return new Point((int)coarseMirrorX, (int)coarseMirrorY);
	}
	
	public Point getFineMirrorDefault() {
		return new Point((int)fineMirrorX, (int)fineMirrorY);
	}
}
