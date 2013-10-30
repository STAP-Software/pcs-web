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

	private int isize;
	private int subImageIntensityRadius;
	
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

	public int getIsize() {
		return isize;
	}

	public void setIsize(int isize) {
		this.isize = isize;
	}
	
	public int getSubImageIntensityRadius() {
		return subImageIntensityRadius;
	}

	public void setSubImageIntensityRadius(int subImageIntensityRadius) {
		this.subImageIntensityRadius = subImageIntensityRadius;
	}

	
	
	public boolean isNewRecord() {
		return pupilMaskTypeId == null;
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

}
