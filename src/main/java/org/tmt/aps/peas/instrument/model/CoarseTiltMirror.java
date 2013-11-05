/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;

@Entity
@Table(name = "CoarseTiltMirror")
public class CoarseTiltMirror {

	@Id
	private Long coarseTiltMirrorId;
	
	private float mechanismLeverArmX;
	private float mechanismLeverArmY;
	private float orafactor;

	@Transient
	private FloatPoint currentPosition;


	@OneToOne
	@JoinColumn(name = "cameraId")
	private Camera camera;

	
	public CoarseTiltMirror(FloatPoint currentPosition) {
		this.currentPosition = currentPosition;
	}
	public CoarseTiltMirror() {
	}
	
	public Long getCoarseTiltMirrorId() {
		return coarseTiltMirrorId;
	}

	public void setCoarseTiltMirrorId(Long coarseTiltMirrorId) {
		this.coarseTiltMirrorId = coarseTiltMirrorId;
	}

	public FloatPoint getMechanismLeverArm() {
		return new FloatPoint(mechanismLeverArmX, mechanismLeverArmY);
	}

	public void setMechanismLeverArm(FloatPoint mechanismLeverArm) {
		this.mechanismLeverArmX = mechanismLeverArm.getX();
		this.mechanismLeverArmY = mechanismLeverArm.getY();
	}

	public float getOrafactor() {
		return orafactor;
	}

	public void setOrafactor(float orafactor) {
		this.orafactor = orafactor;
	}

	public FloatPoint getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(FloatPoint currentPosition) {
		this.currentPosition = currentPosition;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
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
	
}