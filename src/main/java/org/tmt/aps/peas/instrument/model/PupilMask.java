/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
/**
 * Instrument configuration Entity class representing the PupilMask table.  
 * @author smichaels
 */
@Entity
@Table(name = "PupilMask")
@NamedQueries({
    @NamedQuery(
        name = "findAllPupilMasks",
        query = "SELECT o FROM PupilMask o " +
                "INNER JOIN FETCH o.pupilMaskType"
    ),
    @NamedQuery(
        name = "findByPupilMaskTypeAndWheel",
        query = "SELECT o FROM PupilMask o " +
                "INNER JOIN FETCH o.pupilMaskType " +   // no alias
                "INNER JOIN FETCH o.pupilWheel " +      // no alias
                "WHERE o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND o.pupilWheel.pupilWheelId = :pupilWheelId"
    )
})

public class PupilMask {

	@Id
	@SequenceGenerator(
		    name = "pupilMask_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "pupilMask_gen"
		)
	private Long pupilMaskId;

	private String maskName;

	private float maskRotation;

	private int wheelPosition;

	private float spotDiamInterior;
	private float spotDiamPeripheral;
	private float crossHairDiam;

	private float arcsecPerMeter;
	private float m1ToCcdScale;

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

	public float getArcsecPerMeter() {
		return arcsecPerMeter;
	}

	public void setArcsecPerMeter(float arcsecPerMeter) {
		this.arcsecPerMeter = arcsecPerMeter;
	}

	public float getPcsFocusToAcs() {
		return pcsFocusToAcs;
	}

	public void setPcsFocusToAcs(float pcsFocusToAcs) {
		this.pcsFocusToAcs = pcsFocusToAcs;
	}

	public float getM1ToCcdScale() {
		return m1ToCcdScale;
	}

	public void setM1ToCcdScale(float m1ToCcdScale) {
		this.m1ToCcdScale = m1ToCcdScale;
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
