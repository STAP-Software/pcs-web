package org.tmt.aps.peas.computation.model;

public class SufsZernikeResult {
	
	float[] bestFitZernikes;
	float[] theoreticalOffsets;
	float whFactor;
	
	public SufsZernikeResult() {
		
	}
	
	public SufsZernikeResult(float[] bestFitZernikes, float[] theoreticalOffsets, float whFactor) {
		this.bestFitZernikes = bestFitZernikes;
		this.theoreticalOffsets = theoreticalOffsets;
		this.whFactor = whFactor;
	}

	public float[] getBestFitZernikes() {
		return bestFitZernikes;
	}

	public void setBestFitZernikes(float[] bestFitZernikes) {
		this.bestFitZernikes = bestFitZernikes;
	}

	public float[] getTheoreticalOffsets() {
		return theoreticalOffsets;
	}

	public void setTheoreticalOffsets(float[] theoreticalOffsets) {
		this.theoreticalOffsets = theoreticalOffsets;
	}

	public float getWhFactor() {
		return whFactor;
	}

	public void setWhFactor(float whFactor) {
		this.whFactor = whFactor;
	}

	
}
