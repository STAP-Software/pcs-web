/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;

@Entity
@Table(name = "FineTiltMirror")
public class FineTiltMirror {
	
	@Id
	private Long fineTiltMirrorId;
	
	private float mechanismLeverArmX;
	private float mechanismLeverArmY;
	private float windowThickness;
	private float xbk7;
	private float pupilMagnification;

	@Transient
	private Point currentPosition;

	@OneToOne
	@JoinColumn(name = "cameraId")
	private Camera camera;

	
	public FineTiltMirror(Point currentPosition) {
		this.currentPosition = currentPosition;
	}
	public FineTiltMirror() {
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



	

}