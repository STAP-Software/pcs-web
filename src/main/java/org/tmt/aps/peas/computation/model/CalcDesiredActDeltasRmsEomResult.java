package org.tmt.aps.peas.computation.model;

public class CalcDesiredActDeltasRmsEomResult {

	float desiredActDeltasRmsEom;
	float desiredActDeltasFmRmsEom;
	float desiredActDeltasNoFmRmsEom;
	
	public CalcDesiredActDeltasRmsEomResult(float desiredActDeltasRmsEom, float desiredActDeltasFmRmsEom, float desiredActDeltasNoFmRmsEom) {
		this.desiredActDeltasRmsEom = desiredActDeltasRmsEom;
		this.desiredActDeltasFmRmsEom = desiredActDeltasFmRmsEom;
		this.desiredActDeltasNoFmRmsEom = desiredActDeltasNoFmRmsEom;
	}
	
	public CalcDesiredActDeltasRmsEomResult() {}
	
	public float getDesiredActDeltasRmsEom() {
		return desiredActDeltasRmsEom;
	}

	public void setDesiredActDeltasRmsEom(float desiredActDeltasRmsEom) {
		this.desiredActDeltasRmsEom = desiredActDeltasRmsEom;
	}

	public float getDesiredActDeltasFmRmsEom() {
		return desiredActDeltasFmRmsEom;
	}

	public void setDesiredActDeltasFmRmsEom(float desiredActDeltasFmRmsEom) {
		this.desiredActDeltasFmRmsEom = desiredActDeltasFmRmsEom;
	}

	public float getDesiredActDeltasNoFmRmsEom() {
		return desiredActDeltasNoFmRmsEom;
	}

	public void setDesiredActDeltasNoFmRmsEom(float desiredActDeltasNoFmRmsEom) {
		this.desiredActDeltasNoFmRmsEom = desiredActDeltasNoFmRmsEom;
	}
	
	
}
