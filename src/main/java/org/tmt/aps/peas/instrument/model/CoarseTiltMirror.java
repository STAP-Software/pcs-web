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

/**
 * Instrument configuration Entity class representing the CoarseTiltMirror table.  
 * Contains <code>@Transient</code> fields used to store current state information for current state and current position values.
 * @author smichaels
 */
@Entity
@Table(name = "CoarseTiltMirror")
public class CoarseTiltMirror implements DeviceStates {

	@Id
	private Long coarseTiltMirrorId;
	
	private float mechanismLeverArmX;
	private float mechanismLeverArmY;
	private float orafactor;
	private int minMove;

	@Transient
	private Point currentPosition;
	@Transient 
	int stateX;
	@Transient
	int stateY;


	@OneToOne
	@JoinColumn(name = "cameraId")
	private Camera camera;

	
	public CoarseTiltMirror(Point currentPosition) {
		this.currentPosition = currentPosition;
	}
	public CoarseTiltMirror() {
	}
	
	public String getStateXDisplayString() {
		return (stateX == STATE_IN_TRANSIT) ? "In Transit" : "" + currentPosition.x;
	}

	public String getStateYDisplayString() {
		return (stateY == STATE_IN_TRANSIT) ? "In Transit" : "" + currentPosition.y;
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

	public int getMinMove() {
		return minMove;
	}
	
	public void setMinMove(int minMove) {
		this.minMove = minMove;
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
	
}