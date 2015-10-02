package org.tmt.aps.peas.computation.model;

public class ScaleErrorResult {

	float scaleError;
	float slopeError;
	
	public ScaleErrorResult() {};
	
	public ScaleErrorResult(float scaleError, float slopeError) {
		this.scaleError = scaleError;
		this.slopeError = slopeError;
	}
	
	public float getScaleError() {
		return scaleError;
	}
	public void setScaleError(float scaleError) {
		this.scaleError = scaleError;
	}
	public float getSlopeError() {
		return slopeError;
	}
	public void setSlopeError(float slopeError) {
		this.slopeError = slopeError;
	}
	
	
}
