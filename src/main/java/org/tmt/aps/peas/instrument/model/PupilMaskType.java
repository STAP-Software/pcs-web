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
 * Instrument metadata Entity class representing the PupilMaskType table.  
 * @author smichaels
 */
@Entity
@Table(name = "PupilMaskType")
@NamedQueries({ @NamedQuery(name = "findAllPupilMaskTypes", query = "SELECT o from PupilMaskType o") })
public class PupilMaskType {

	public static final Long PUPIL_MASK_TYPE_ID_36 = new Long(1);
	public static final Long PUPIL_MASK_TYPE_ID_160 = new Long(2);
	public static final Long PUPIL_MASK_TYPE_ID_508 = new Long(3);
	public static final Long PUPIL_MASK_TYPE_ID_UFS = new Long(4);
	public static final Long PUPIL_MASK_TYPE_ID_SUFS = new Long(5);
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long pupilMaskTypeId;

	private int numSpots;
	private String pupilMaskTypeName;

	private int subImageIntensityRadius;
	
	private int ccdToCartesianPixelX;
	private int ccdToCartesianPixelY;
	
	public Long getPupilMaskTypeId() {
		return pupilMaskTypeId;
	}

	public void setPupilMaskTypeId(Long pupilMaskTypeId) {
		this.pupilMaskTypeId = pupilMaskTypeId;
	}

	public int getNumSpots() {
		return numSpots;
	}

	public void setNumSpots(int numSpots) {
		this.numSpots = numSpots;
	}

	public String getPupilMaskTypeName() {
		return pupilMaskTypeName;
	}

	public void setPupilMaskTypeName(String pupilMaskTypeName) {
		this.pupilMaskTypeName = pupilMaskTypeName;
	}

	public int getSubImageIntensityRadius() {
		return subImageIntensityRadius;
	}

	public void setSubImageIntensityRadius(int subImageIntensityRadius) {
		this.subImageIntensityRadius = subImageIntensityRadius;
	}

	
	
	public int getCcdToCartesianPixelX() {
		return ccdToCartesianPixelX;
	}

	public void setCcdToCartesianPixelX(int ccdToCartesianPixelX) {
		this.ccdToCartesianPixelX = ccdToCartesianPixelX;
	}

	public int getCcdToCartesianPixelY() {
		return ccdToCartesianPixelY;
	}

	public void setCcdToCartesianPixelY(int ccdToCartesianPixelY) {
		this.ccdToCartesianPixelY = ccdToCartesianPixelY;
	}

	public boolean isNewRecord() {
		return pupilMaskTypeId == null;
	}

	public boolean isPupilMaskTypeSufs() {
		return pupilMaskTypeId.longValue() == PUPIL_MASK_TYPE_ID_SUFS.longValue();
	}
	
	public boolean isPupilMaskTypePt() {
		return pupilMaskTypeId.longValue() == PUPIL_MASK_TYPE_ID_36.longValue();
	}
	
	public boolean isPupilMaskTypePh() {
		return pupilMaskTypeId.longValue() == PUPIL_MASK_TYPE_ID_160.longValue();
	}
	
	public boolean isPupilMaskTypeFs() {
		return pupilMaskTypeId.longValue() == PUPIL_MASK_TYPE_ID_508.longValue();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof PupilMaskType) {
			PupilMaskType candidate = (PupilMaskType) obj;
			if (candidate.getPupilMaskTypeId().longValue() == this.getPupilMaskTypeId().longValue()) {
				return true;
			}
		}
		return false;
	}

	public boolean isPupilMaskTypeNone() {
		return !(isPupilMaskTypeSufs() || isPupilMaskTypePt() || isPupilMaskTypePh() || isPupilMaskTypeFs());
	}

}
