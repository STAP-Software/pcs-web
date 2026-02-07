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
import org.tmt.aps.peas.common.Point;

/**
 * Configuration entity class representing the SufsCoarseOffsetsConfig table
 * @author smichaels
 */
@Entity
@Table(name = "SufsCoarseOffsetsConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class SufsCoarseOffsetsConfig {

	
	// this should be a global type of config, maybe in global config, because when we change the defaults, they stay.
	// but we need to capture the current values with a procedure.
	
	@Transient
	Logger logger = Logger.getLogger(this.getClass());

	@Id
	@SequenceGenerator(
		    name = "sufsCoarseOffsetsConfig_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
	)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "sufsCoarseOffsetsConfig_gen"
	)
	private Long sufsCoarseOffsetsConfigId;
	
	int coarseMirrorOffsetDefaultX; 
	int coarseMirrorOffsetDefaultY;
	int coarseMirrorOffsetCurrentX; 
	int coarseMirrorOffsetCurrentY;

	
	public SufsCoarseOffsetsConfig() {
		
	}

	public SufsCoarseOffsetsConfig(SufsCoarseOffsetsConfig source) {
		
		try {
		BeanUtils.copyProperties(this, source);

		this.sufsCoarseOffsetsConfigId = null;
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}


	
	public Long getSufsCoarseOffsetsConfigId() {
		return sufsCoarseOffsetsConfigId;
	}

	public void setSufsCoarseOffsetsConfigId(Long sufsCoarseOffsetsConfigId) {
		this.sufsCoarseOffsetsConfigId = sufsCoarseOffsetsConfigId;
	}


	public int getCoarseMirrorOffsetDefaultX() {
		return coarseMirrorOffsetDefaultX;
	}

	public void setCoarseMirrorOffsetDefaultX(int coarseMirrorOffsetDefaultX) {
		this.coarseMirrorOffsetDefaultX = coarseMirrorOffsetDefaultX;
	}

	public int getCoarseMirrorOffsetDefaultY() {
		return coarseMirrorOffsetDefaultY;
	}

	public void setCoarseMirrorOffsetDefaultY(int coarseMirrorOffsetDefaultY) {
		this.coarseMirrorOffsetDefaultY = coarseMirrorOffsetDefaultY;
	}

	public int getCoarseMirrorOffsetCurrentX() {
		return coarseMirrorOffsetCurrentX;
	}

	public void setCoarseMirrorOffsetCurrentX(int coarseMirrorOffsetCurrentX) {
		this.coarseMirrorOffsetCurrentX = coarseMirrorOffsetCurrentX;
	}

	public int getCoarseMirrorOffsetCurrentY() {
		return coarseMirrorOffsetCurrentY;
	}

	public void setCoarseMirrorOffsetCurrentY(int coarseMirrorOffsetCurrentY) {
		this.coarseMirrorOffsetCurrentY = coarseMirrorOffsetCurrentY;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("SufsOffsetsToZernikesConfig:");
		buf.append("\ncoarseMirrorOffsetDefaultX = " + coarseMirrorOffsetDefaultY);
		buf.append("\ncoarseMirrorOffsetDefaultY = " + coarseMirrorOffsetDefaultY);
		buf.append("\ncoarseMirrorOffsetCurrentX = " + coarseMirrorOffsetCurrentX);
		buf.append("\ncoarseMirrorOffsetCurrentY = " + coarseMirrorOffsetCurrentY);
		buf.append("\n");

		return buf.toString();
	}

	public Point getCoarseMirrorOffsetCurrent() {
		
		return new Point(coarseMirrorOffsetCurrentX, coarseMirrorOffsetCurrentY);
	}


}
