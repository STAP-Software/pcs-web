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
import jakarta.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.tmt.aps.peas.instrument.model.CcdGain;

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
	@SequenceGenerator(
		    name = "iterationListConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "iterationListConfig_gen"
		)

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
	
	public void updateDisplayLists(int lightSource) {
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
		
		
		// also populate the associated gains
		buf = new StringBuffer();
		for (int index=0; index<getIterationValueList().getSize(); index++) {
			
			IterationValue iterationValue = getIterationValueList().getIterationValue(index);
			if (lightSource == ProcedureConfig.LIGHT_SOURCE_LED) {
				CcdGain ccdGain = (CcdGain)iterationValue.getIterableEntity("LedGain");
				buf.append(ccdGain.getGainNumber() + ", ");
			} else {
				CcdGain ccdGain = (CcdGain)iterationValue.getIterableEntity("StarGain");
				buf.append(ccdGain.getGainNumber() + ", ");
			}
		}
		buf.deleteCharAt(buf.length()-1);
		buf.deleteCharAt(buf.length()-1);
		setCcdGainList(buf.toString());
	}


	@Transient
	String ccdGainList;
	
	public String getCcdGainList() {
		return ccdGainList;
	}

	public void setCcdGainList(String ccdGainList) {
		this.ccdGainList = ccdGainList;
	}
		
	
	public boolean equals(Object obj) {
		if (obj instanceof IterationListConfig) {
			IterationListConfig candidate = (IterationListConfig)obj;
			// if the iteration value list encoded are the same, these are the same
			
			if (this.getIterationValueList().getSize() == candidate.getIterationValueList().getSize()) {
				
				boolean match = true;
				
				// both have the same number of iterations, check that the filters match
				for (int i=0; i<this.getIterationValueList().getSize(); i++) {
					
					try {
					
						String matchString = this.getIterationValueList().findEntityLabelValue(i, "Filter");
						String candidateString = candidate.getIterationValueList().findEntityLabelValue(i, "Filter");
					
						if (!matchString.equals(candidateString)) {
							match = false;
						}
					
					
					} catch (Exception e) {
						match = false;
					}
					
				}
				
				if (match) {
					return true;
				}
									
			}
			
			
		}
		return false;
	}
	


}








	