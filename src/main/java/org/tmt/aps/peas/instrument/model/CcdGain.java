/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;
import org.tmt.aps.peas.extinf.Gain;

/**
 * Instrument configuration Entity class representing the CcdType database table.  
 * @author smichaels
 *
 */
@Entity
@Table(name = "CcdGain")
@NamedQueries({
	@NamedQuery(name = "findCcdGainByNumber", query = "SELECT o from CcdGain o where o.gainNumber = :gainNumber" )
})
public class CcdGain implements IterableEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long ccdGainId;
	
	private int gainNumber; 
	private float gainValue; 		// counts/photoelectron
	private int gainOffsetChannel0; // counts
	private int gainOffsetChannel1; // counts
	
	public CcdGain() {
	
	}
	
	public Long getCcdGainId() {
		return ccdGainId;
	}

	public void setCcdGainId(Long ccdGainId) {
		this.ccdGainId = ccdGainId;
	}

	public int getGainNumber() {
		return gainNumber;
	}

	public void setGainNumber(int gainNumber) {
		this.gainNumber = gainNumber;
	}

	public float getGainValue() {
		return gainValue;
	}

	public void setGainValue(float gainValue) {
		this.gainValue = gainValue;
	}

	public int getGainOffsetChannel0() {
		return gainOffsetChannel0;
	}

	public void setGainOffsetChannel0(int gainOffsetChannel0) {
		this.gainOffsetChannel0 = gainOffsetChannel0;
	}

	public int getGainOffsetChannel1() {
		return gainOffsetChannel1;
	}

	public void setGainOffsetChannel1(int gainOffsetChannel1) {
		this.gainOffsetChannel1 = gainOffsetChannel1;
	}
	
	public int[] getGainOffsets() {
		int[] offsets = {gainOffsetChannel0, gainOffsetChannel1};
		return offsets;
	}


	public boolean equals(Object obj) {
		if (obj instanceof CcdGain) {
			CcdGain candidate = (CcdGain)obj;
			if (candidate.getCcdGainId().longValue() == this.getCcdGainId().longValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}


	@Override
	public String getKeyFieldName() {
		return "gainNumber";
	}


	@Override
	public String getLabelFieldName() {

		return "gainNumber";
	}


	@Override
	public String getLabel() {
		return "Gain";
	}
	
	public String getGainDisplay() {
		return gainNumber + ": (" + gainValue + ")";
	}
	
}