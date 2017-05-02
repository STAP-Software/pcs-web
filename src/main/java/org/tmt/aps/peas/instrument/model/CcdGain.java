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

/**
 * Instrument configuration Entity class representing the CcdType database table.  
 * @author smichaels
 *
 */
@Entity
@Table(name = "CcdGain")
@NamedQueries({
	@NamedQuery(name = "findCcdGain", query = "SELECT o from CcdGain o where o.ccdGainId = :ccdGainId" )
})
public class CcdGain {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long ccdGainId;
	
	private int gainNumber; 
	private float gainValue; 		// units?
	private int gainOffsetChannel1; // units? (pixels?)
	private int gainOffsetChannel2; // units? (pixels?)
	
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

	public int getGainOffsetChannel1() {
		return gainOffsetChannel1;
	}

	public void setGainOffsetChannel1(int gainOffsetChannel1) {
		this.gainOffsetChannel1 = gainOffsetChannel1;
	}

	public int getGainOffsetChannel2() {
		return gainOffsetChannel2;
	}

	public void setGainOffsetChannel2(int gainOffsetChannel2) {
		this.gainOffsetChannel2 = gainOffsetChannel2;
	}

}