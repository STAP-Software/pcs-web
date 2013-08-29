/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Transient;

public class FineTiltMirror {
	
	double xLeverArm;
	double windowThickness;
	double indexOfRefraction;
	

	@Transient
	private float x;
	@Transient
	private float y;

	FineTiltMirror(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}


	
	public double getxLeverArm() {
		return xLeverArm;
	}

	public void setxLeverArm(double xLeverArm) {
		this.xLeverArm = xLeverArm;
	}

	public double getWindowThickness() {
		return windowThickness;
	}

	public void setWindowThickness(double windowThickness) {
		this.windowThickness = windowThickness;
	}

	public double getIndexOfRefraction() {
		return indexOfRefraction;
	}

	public void setIndexOfRefraction(double indexOfRefraction) {
		this.indexOfRefraction = indexOfRefraction;
	}

	public double getxScale() {
		
		return xLeverArm/windowThickness * indexOfRefraction /(indexOfRefraction - 1.0);
	
	}

}