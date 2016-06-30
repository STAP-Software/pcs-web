/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/**
 * Instrument configuration Entity class representing the PupilMask table.  
 * @author smichaels
 */
@Entity
@Table(name = "PupilMask")
@NamedQueries({
	@NamedQuery(name = "findAllPupilMasks", query = "SELECT o from PupilMask o INNER JOIN FETCH o.pupilMaskType" ),
	@NamedQuery(name = "findByPupilMaskTypeAndWheel", query = "SELECT o from PupilMask o INNER JOIN FETCH o.pupilMaskType t INNER JOIN FETCH o.pupilWheel pw "
			+ "WHERE t.pupilMaskTypeId = :pupilMaskTypeId and pw.pupilWheelId = :pupilWheelId" )
})
public class PupilMask {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long pupilMaskId;

	private String maskName;

	private float maskRotation;

	private int wheelPosition;

	private float spotDiamInterior;
	private float spotDiamPeripheral;
	private float crossHairDiam;

	private float radPerPixel;
	private float secPerPixel;
	private float pcsFocusToAcs;

	@ManyToOne
	@JoinColumn (name="pupilWheelId")
	private PupilWheel pupilWheel;
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;
	

	public Long getPupilMaskId() {
		return pupilMaskId;
	}

	public void setPupilMaskId(Long pupilMaskId) {
		this.pupilMaskId = pupilMaskId;
	}

	public String getMaskName() {
		return maskName;
	}

	public void setMaskName(String maskName) {
		this.maskName = maskName;
	}

	public float getMaskRotation() {
		return maskRotation;
	}

	public void setMaskRotation(float maskRotation) {
		this.maskRotation = maskRotation;
	}

	public int getWheelPosition() {
		return wheelPosition;
	}

	public void setWheelPosition(int wheelPosition) {
		this.wheelPosition = wheelPosition;
	}

	public PupilWheel getPupilWheel() {
		return pupilWheel;
	}

	public void setPupilWheel(PupilWheel pupilWheel) {
		this.pupilWheel = pupilWheel;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public float getSpotDiamInterior() {
		return spotDiamInterior;
	}

	public void setSpotDiamInterior(float spotDiamInterior) {
		this.spotDiamInterior = spotDiamInterior;
	}

	public float getSpotDiamPeripheral() {
		return spotDiamPeripheral;
	}

	public void setSpotDiamPeripheral(float spotDiamPeripheral) {
		this.spotDiamPeripheral = spotDiamPeripheral;
	}

	public float getCrossHairDiam() {
		return crossHairDiam;
	}

	public void setCrossHairDiam(float crossHairDiam) {
		this.crossHairDiam = crossHairDiam;
	}

	public float getRadPerPixel() {
		return radPerPixel;
	}

	public void setRadPerPixel(float radPerPixel) {
		this.radPerPixel = radPerPixel;
	}

	public float getSecPerPixel() {
		return secPerPixel;
	}

	public void setSecPerPixel(float secPerPixel) {
		this.secPerPixel = secPerPixel;
	}

	public float getPcsFocusToAcs() {
		return pcsFocusToAcs;
	}

	public void setPcsFocusToAcs(float pcsFocusToAcs) {
		this.pcsFocusToAcs = pcsFocusToAcs;
	}

	
	
	public boolean isNewRecord() {
		return pupilMaskId == null;
	}


	public boolean equals(Object obj) {
		if (obj instanceof PupilMask) {
			PupilMask candidate = (PupilMask)obj;
			if (candidate.getPupilMaskId().longValue() == this.getPupilMaskId().longValue()) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "Pupil Mask: " + maskName;
	}
	
}
