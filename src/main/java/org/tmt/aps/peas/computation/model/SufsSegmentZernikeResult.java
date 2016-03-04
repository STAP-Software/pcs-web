package org.tmt.aps.peas.computation.model;

public class SufsSegmentZernikeResult {
	
	float[][] bestFitZernikes;
	float[][] theoreticalOffsets;
	float[] whFactor;
	
	public SufsSegmentZernikeResult() {
		
	}
	
	public SufsSegmentZernikeResult(SufsZernikeResult[] sufsZernikeResults) {
				
		int bestFitCount = sufsZernikeResults[0].getBestFitZernikes().length;
		int theoreticalCount = sufsZernikeResults[0].getTheoreticalOffsets().length;
			
		whFactor = new float[sufsZernikeResults.length];
		bestFitZernikes = new float[sufsZernikeResults.length][bestFitCount];
		theoreticalOffsets = new float[sufsZernikeResults.length][theoreticalCount];
		
		for (int i=0; i<sufsZernikeResults.length; i++) {
			whFactor[i] = sufsZernikeResults[i].getWhFactor();
			bestFitZernikes[i] = sufsZernikeResults[i].getBestFitZernikes();
			theoreticalOffsets[i] = sufsZernikeResults[i].getTheoreticalOffsets();
		}

		
	}

	public float[][] getBestFitZernikes() {
		return bestFitZernikes;
	}

	public void setBestFitZernikes(float[][] bestFitZernikes) {
		this.bestFitZernikes = bestFitZernikes;
	}

	public float[][] getTheoreticalOffsets() {
		return theoreticalOffsets;
	}

	public void setTheoreticalOffsets(float[][] theoreticalOffsets) {
		this.theoreticalOffsets = theoreticalOffsets;
	}

	public float[] getWhFactor() {
		return whFactor;
	}

	public void setWhFactor(float[] whFactor) {
		this.whFactor = whFactor;
	}


}
