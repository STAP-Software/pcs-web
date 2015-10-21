package org.tmt.aps.peas.computation.model;

public class CalcDesiredActDeltasRmsStdResult {

	float desiredActDeltasRmsStd;
	float desiredActDeltasFmRmsStd;
	float desiredActDeltasNoFmRmsStd;
	
	public CalcDesiredActDeltasRmsStdResult(float desiredActDeltasRmsStd, float desiredActDeltasFmRmsStd, float desiredActDeltasNoFmRmsStd) {
		this.desiredActDeltasRmsStd = desiredActDeltasRmsStd;
		this.desiredActDeltasFmRmsStd = desiredActDeltasFmRmsStd;
		this.desiredActDeltasNoFmRmsStd = desiredActDeltasNoFmRmsStd;
	}
	
	public CalcDesiredActDeltasRmsStdResult() {}
	
	public float getDesiredActDeltasRmsStd() {
		return desiredActDeltasRmsStd;
	}

	public void setDesiredActDeltasRmsStd(float desiredActDeltasRmsStd) {
		this.desiredActDeltasRmsStd = desiredActDeltasRmsStd;
	}

	public float getDesiredActDeltasFmRmsStd() {
		return desiredActDeltasFmRmsStd;
	}

	public void setDesiredActDeltasFmRmsStd(float desiredActDeltasFmRmsStd) {
		this.desiredActDeltasFmRmsStd = desiredActDeltasFmRmsStd;
	}

	public float getDesiredActDeltasNoFmRmsStd() {
		return desiredActDeltasNoFmRmsStd;
	}

	public void setDesiredActDeltasNoFmRmsStd(float desiredActDeltasNoFmRmsStd) {
		this.desiredActDeltasNoFmRmsStd = desiredActDeltasNoFmRmsStd;
	}
	
	
}
