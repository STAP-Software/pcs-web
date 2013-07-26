package org.tmt.aps.peas.instrument.model;

public class CoarseTiltMirror {
	private float x;
	private float y;

	CoarseTiltMirror(float x, float y) {
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

}