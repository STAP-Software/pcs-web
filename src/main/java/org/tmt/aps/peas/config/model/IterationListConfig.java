/**
 * @author Scott Michaels
 * Copyright (C) 2014 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Configuration entity class representing the IterationListConfig table
 * @author smichaels
 *
 */
@Entity
@Table(name = "IterationListConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class IterationListConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long iterationListConfigId;

	String iterationValueListEncoded;


	
	public IterationListConfig() {
		
	}
	
	public IterationListConfig(IterationListConfig source) throws Exception {
		
		if (source != null) {
			BeanUtils.copyProperties(this, source);
		}

		this.iterationListConfigId = null;
	}


	public Long getIterationListConfigId() {
		return iterationListConfigId;
	}

	public void setIterationListConfigId(Long iterationListConfigId) {
		this.iterationListConfigId = iterationListConfigId;
	}

	public String getIterationValueListEncoded() {
		return iterationValueListEncoded;
	}

	public void setIterationValueListEncoded(String iterationValueListEncoded) {
		this.iterationValueListEncoded = iterationValueListEncoded;
	}


	@Transient
	IterationValueList iterationValueList;

	public IterationValueList getIterationValueList() {
		return iterationValueList;
	}

	public void setIterationValueList(IterationValueList iterationValueList) {
		this.iterationValueList = iterationValueList;
	}
	
	@Transient
	String integrationTimeList;
	
	public String getIntegrationTimeList() {
		return integrationTimeList;
	}

	public void setIntegrationTimeList(String integrationTimeList) {
		this.integrationTimeList = integrationTimeList;
	}
	
	public void updateIntegrationTimeList(int lightSource) {
		// also populate the associated integration times
		StringBuffer buf = new StringBuffer();
		for (int index=0; index<getIterationValueList().getSize(); index++) {
			
			IterationValue iterationValue = getIterationValueList().getIterationValue(index);
			if (lightSource == ProcedureConfig.LIGHT_SOURCE_LED) {
				buf.append(((IntegrationTime)iterationValue.getIterableEntity("LedIntegrationTime")).integrationTime + ", ");
			} else {
				buf.append(((IntegrationTime)iterationValue.getIterableEntity("StarIntegrationTime")).integrationTime + ", ");					
			}
		}
		buf.deleteCharAt(buf.length()-1);
		buf.deleteCharAt(buf.length()-1);
		setIntegrationTimeList(buf.toString());
	}


	public boolean equals(Object obj) {
		if (obj instanceof IterationListConfig) {
			IterationListConfig candidate = (IterationListConfig)obj;
			// if the iteration value list encoded are the same, these are the same
			
			return (candidate.getIterationValueListEncoded().equals(getIterationValueListEncoded()));
		}
		return super.equals(obj);
	}
	


}








	