package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>calculateSufsCentroidStats</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateSufsCentroidStats(SufsSegmentOffsetsResult, int[], int[], int[], int[][])
 */
public class SufsCentroidStatsResult {

	int maxSpotNum[];
	float maxOffset[];
	float rmsOffset[];
	float enclosedEnergy80[];
	float enclosedEnergy50[];

	public SufsCentroidStatsResult() {
		
	}
	
	public SufsCentroidStatsResult(CentroidStatsResult[] segmentStatsResults) {
		maxSpotNum = new int[segmentStatsResults.length];
		maxOffset = new float[segmentStatsResults.length];
		rmsOffset = new float[segmentStatsResults.length];
		enclosedEnergy80 = new float[segmentStatsResults.length];
		enclosedEnergy50 = new float[segmentStatsResults.length];
		
		for (int i=0; i<segmentStatsResults.length; i++) {
			maxSpotNum[i] = segmentStatsResults[i].getMaxSpotNum();
			maxOffset[i] = segmentStatsResults[i].getMaxOffset();
			rmsOffset[i] = segmentStatsResults[i].getRmsOffset();
			enclosedEnergy80[i] = segmentStatsResults[i].getEnclosedEnergy80();
			enclosedEnergy50[i] = segmentStatsResults[i].getEnclosedEnergy50();
		}
	}


	public int[] getMaxSpotNum() {
		return maxSpotNum;
	}


	public void setMaxSpotNum(int[] maxSpotNum) {
		this.maxSpotNum = maxSpotNum;
	}


	public float[] getMaxOffset() {
		return maxOffset;
	}


	public void setMaxOffset(float[] maxOffset) {
		this.maxOffset = maxOffset;
	}


	public float[] getRmsOffset() {
		return rmsOffset;
	}


	public void setRmsOffset(float[] rmsOffset) {
		this.rmsOffset = rmsOffset;
	}


	public float[] getEnclosedEnergy80() {
		return enclosedEnergy80;
	}


	public void setEnclosedEnergy80(float[] enclosedEnergy80) {
		this.enclosedEnergy80 = enclosedEnergy80;
	}


	public float[] getEnclosedEnergy50() {
		return enclosedEnergy50;
	}


	public void setEnclosedEnergy50(float[] enclosedEnergy50) {
		this.enclosedEnergy50 = enclosedEnergy50;
	}


	
}
