/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;

/**
 * Instrument metadata Entity class representing the PupilMaskType table.  
 * @author smichaels
 */
@Entity
@Table(name = "PupilMaskType")
@NamedQueries({ @NamedQuery(name = "findAllPupilMaskTypes", query = "SELECT o from PupilMaskType o") })
public class PupilMaskType implements IterableEntity {

	public static final Long PUPIL_MASK_TYPE_ID_36 = Long.valueOf(1);
	public static final Long PUPIL_MASK_TYPE_ID_160 = Long.valueOf(2);
	public static final Long PUPIL_MASK_TYPE_ID_508 = Long.valueOf(3);
	public static final Long PUPIL_MASK_TYPE_ID_UFS = Long.valueOf(4);
	public static final Long PUPIL_MASK_TYPE_ID_SUFS = Long.valueOf(5);
	
	@Id
	@SequenceGenerator(
		    name = "pupilMaskType_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "pupilMaskType_gen"
		)
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

	public String getClassName() {
		return this.getClass().getName();
	}


	@Override
	public String getKeyFieldName() {
		return "pupilMaskTypeId";
	}


	@Override
	public String getLabelFieldName() {
		return "pupilMaskTypeName";
	}


	@Override
	public String getLabel() {
		return "Pupil Mask";
	}
}
