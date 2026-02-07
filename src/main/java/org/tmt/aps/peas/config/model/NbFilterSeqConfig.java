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
 * Configuration entity class representing the NbFilterSeqConfig table
 * @author smichaels
 */
@Entity
@Table(name = "NbFilterSeqConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class NbFilterSeqConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@SequenceGenerator(
		    name = "nbFilterSeqConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "nbFilterSeqConfig_gen"
		)
	private Long nbFilterSeqConfigId;
	
	private float edgeHeightSearchRange;


	public NbFilterSeqConfig() {
		
	}


	public NbFilterSeqConfig(NbFilterSeqConfig source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.nbFilterSeqConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}


	public Long getNbFilterSeqConfigId() {
		return nbFilterSeqConfigId;
	}


	public void setNbFilterSeqConfigId(Long nbFilterSeqConfigId) {
		this.nbFilterSeqConfigId = nbFilterSeqConfigId;
	}


	public float getEdgeHeightSearchRange() {
		return edgeHeightSearchRange;
	}


	public void setEdgeHeightSearchRange(float edgeHeightSearchRange) {
		this.edgeHeightSearchRange = edgeHeightSearchRange;
	}


	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("NbFilterSeqConfig:");
		buf.append("\nedgeHeightSearchRange = " + edgeHeightSearchRange);
		buf.append("\n");

		return buf.toString();
	}


}
