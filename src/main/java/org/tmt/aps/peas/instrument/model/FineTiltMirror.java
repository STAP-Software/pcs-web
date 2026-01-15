/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;

/**
 * Instrument configuration Entity class representing the FineTiltMirror table.  
 * Contains <code>@Transient</code> fields used to store current state information for current state and current position values.
 * @author smichaels
 */
@Entity
@Table(name = "FineTiltMirror")
public class FineTiltMirror implements DeviceStates {
	
	@Id
	private Long fineTiltMirrorId;
	
	private float mechanismLeverArmX;
	private float mechanismLeverArmY;
	private float windowThickness;
	private float xbk7;
	private float pupilMagnification;
	private int offloadThreshold;

	@Transient
	private Point currentPosition;
	@Transient 
	int stateX;
	@Transient
	int stateY;
	

	@OneToOne
	@JoinColumn(name = "cameraId")
	private Camera camera;

	
	public FineTiltMirror(Point currentPosition) {
		this.currentPosition = currentPosition;
	}
	public FineTiltMirror() {
	}

	public String getStateXDisplayString() {
		return (stateX == STATE_IN_TRANSIT) ? "In Transit" : "" + currentPosition.x;
	}

	public String getStateYDisplayString() {
		return (stateY == STATE_IN_TRANSIT) ? "In Transit" : "" + currentPosition.y;
	}

	
	public FloatPoint getMechanismLeverArm() {
		return new FloatPoint(mechanismLeverArmX, mechanismLeverArmY);
	}

	public void setMechanismLeverArm(FloatPoint mechanismLeverArm) {
		this.mechanismLeverArmX = mechanismLeverArm.getX();
		this.mechanismLeverArmY = mechanismLeverArm.getY();
	}
	
	
	public Long getFineTiltMirrorId() {
		return fineTiltMirrorId;
	}
	
	public void setFineTiltMirrorId(Long fineTiltMirrorId) {
		this.fineTiltMirrorId = fineTiltMirrorId;
	}
	
	public float getMechanismLeverArmX() {
		return mechanismLeverArmX;
	}
	
	public void setMechanismLeverArmX(float mechanismLeverArmX) {
		this.mechanismLeverArmX = mechanismLeverArmX;
	}
	
	public float getMechanismLeverArmY() {
		return mechanismLeverArmY;
	}
	
	public void setMechanismLeverArmY(float mechanismLeverArmY) {
		this.mechanismLeverArmY = mechanismLeverArmY;
	}
	
	public float getWindowThickness() {
		return windowThickness;
	}
	
	public void setWindowThickness(float windowThickness) {
		this.windowThickness = windowThickness;
	}
	
	public float getXbk7() {
		return xbk7;
	}
	
	public void setXbk7(float xbk7) {
		this.xbk7 = xbk7;
	}
	
	public float getPupilMagnification() {
		return pupilMagnification;
	}
	
	public void setPupilMagnification(float pupilMagnification) {
		this.pupilMagnification = pupilMagnification;
	}
	
	public int getOffloadThreshold() {
		return offloadThreshold;
	}
	public void setOffloadThreshold(int offloadThreshold) {
		this.offloadThreshold = offloadThreshold;
	}
	public Point getCurrentPosition() {
		return currentPosition;
	}
	
	public void setCurrentPosition(Point currentPosition) {
		this.currentPosition = currentPosition;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public int getStateX() {
		return stateX;
	}
	public void setStateX(int stateX) {
		this.stateX = stateX;
	}
	public int getStateY() {
		return stateY;
	}
	public void setStateY(int stateY) {
		this.stateY = stateY;
	}	

	public String getRawStateXDisplayString() {
		if (stateX > 1 || stateX < 0) {
			return "Unknown";
		} else {
			return (stateX == STATE_IN_TRANSIT) ? "In Transit" : "In Position";
		}

	}

	public String getRawStateYDisplayString() {
		if (stateY > 1 || stateY < 0) {
			return "Unknown";
		} else {
			return (stateY == STATE_IN_TRANSIT) ? "In Transit" : "In Position";
		}

	}


	

}