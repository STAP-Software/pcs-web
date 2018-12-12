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
@Table(name = "CcdType")
@NamedQueries({
	@NamedQuery(name = "findCcdType", query = "SELECT o from CcdType o where o.ccdTypeId = :ccdTypeId" ),
	@NamedQuery(name = "findAllCcdTypes", query = "SELECT o from CcdType o" )
})
public class CcdType {

	
	public static final Long CCD_TYPE_ID_ORIG = new Long(1);
	public static final Long CCD_TYPE_ID_SCIMEAS = new Long(2);
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long ccdTypeId;
	private String ccdTypeName;	
	private String ccdTypeDescription;	
	private float pixelSize;		    // meters
	private int normalReadoutWidth;     // pixels
	private int normalReadoutHeight;    // pixels
	private int overscanReadoutWidth;   // pixels
	private int overscanReadoutHeight;  // pixels
	
	
	public CcdType() {
		
	}


	public Long getCcdTypeId() {
		return ccdTypeId;
	}

	public void setCcdTypeId(Long ccdTypeId) {
		this.ccdTypeId = ccdTypeId;
	}

	public String getCcdTypeName() {
		return ccdTypeName;
	}

	public void setCcdTypeName(String ccdTypeName) {
		this.ccdTypeName = ccdTypeName;
	}

	public String getCcdTypeDescription() {
		return ccdTypeDescription;
	}

	public void setCcdTypeDescription(String ccdTypeDescription) {
		this.ccdTypeDescription = ccdTypeDescription;
	}

	public float getPixelSize() {
		return pixelSize;
	}

	public void setPixelSize(float pixelSize) {
		this.pixelSize = pixelSize;
	}

	public int getNormalReadoutWidth() {
		return normalReadoutWidth;
	}

	public void setNormalReadoutWidth(int normalReadoutWidth) {
		this.normalReadoutWidth = normalReadoutWidth;
	}

	public int getNormalReadoutHeight() {
		return normalReadoutHeight;
	}

	public void setNormalReadoutHeight(int normalReadoutHeight) {
		this.normalReadoutHeight = normalReadoutHeight;
	}

	public int getOverscanReadoutWidth() {
		return overscanReadoutWidth;
	}

	public void setOverscanReadoutWidth(int overscanReadoutWidth) {
		this.overscanReadoutWidth = overscanReadoutWidth;
	}

	public int getOverscanReadoutHeight() {
		return overscanReadoutHeight;
	}

	public void setOverscanReadoutHeight(int overscanReadoutHeight) {
		this.overscanReadoutHeight = overscanReadoutHeight;
	}
	
	public boolean isTypeOrig() {
		return ccdTypeId.longValue() == CCD_TYPE_ID_ORIG;
	}

	public boolean isTypeSciMeas() {
		return ccdTypeId.longValue() == CCD_TYPE_ID_SCIMEAS;
	}
}
