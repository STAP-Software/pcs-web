package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for calculateCentroidStats computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateCentroidStats(FloatPoint[], int[], int[], int[])
 */
public class CentroidStatsResult {

	int maxSpotNum;
	float maxOffset;
	float rmsOffset;
	float enclosedEnergy80;
	float enclosedEnergy50;

	public CentroidStatsResult(int maxSpotNum, float maxOffset, float rmsOffset, float enclosedEnergy80, float enclosedEnergy50) {
		this.maxSpotNum = maxSpotNum;
		this.maxOffset = maxOffset;
		this.rmsOffset = rmsOffset;
		this.enclosedEnergy80 = enclosedEnergy80;
		this.enclosedEnergy50 = enclosedEnergy50;
	}
	
	public CentroidStatsResult() {};

	public int getMaxSpotNum() {
		return maxSpotNum;
	}

	public void setMaxSpotNum(int maxSpotNum) {
		this.maxSpotNum = maxSpotNum;
	}

	public float getMaxOffset() {
		return maxOffset;
	}

	public void setMaxOffset(float maxOffset) {
		this.maxOffset = maxOffset;
	}

	public float getRmsOffset() {
		return rmsOffset;
	}

	public void setRmsOffset(float rmsOffset) {
		this.rmsOffset = rmsOffset;
	}

	public float getEnclosedEnergy80() {
		return enclosedEnergy80;
	}

	public void setEnclosedEnergy80(float enclosedEnergy80) {
		this.enclosedEnergy80 = enclosedEnergy80;
	}

	public float getEnclosedEnergy50() {
		return enclosedEnergy50;
	}

	public void setEnclosedEnergy50(float enclosedEnergy50) {
		this.enclosedEnergy50 = enclosedEnergy50;
	}

}
