package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PupilMask")
@NamedQueries({
	@NamedQuery(name = "findAllPupilMasks", query = "SELECT o from PupilMask o" )
})
public class PupilMask {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long pupilMaskId;

	private String maskName;

	private int numSpots;
	
	private float spotDiameter;

	private float maskRotation;

	private int wheelPosition;

	private float radPerPixel;
	private float secPerPixel;
	private float pcsFocusToAcs;

	@ManyToOne
	@JoinColumn (name="pupilWheelId")
	private PupilWheel pupilWheel;
	
	

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

	public int getNumSpots() {
		return numSpots;
	}

	public void setNumSpots(int numSpots) {
		this.numSpots = numSpots;
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

	public PupilWheel getPupilWheel() {
		return pupilWheel;
	}

	public void setPupilWheel(PupilWheel pupilWheel) {
		this.pupilWheel = pupilWheel;
	}
	
	public float getSpotDiameter() {
		return spotDiameter;
	}

	public void setSpotDiameter(float spotDiameter) {
		this.spotDiameter = spotDiameter;
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

	
	
}
