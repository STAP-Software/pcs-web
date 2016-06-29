package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>calculatePhasingStats</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculatePhasingStats(int[], int[], float[], float[])
 */
public class PhasingStatsResult {

	int goodEdgeCount;
	float edgeErrorMax;
	float edgeErrorRss;
	
	float residualEdgeErrorMax;
	float residualEdgeErrorRss;

	
	public PhasingStatsResult() {
		
	};

	public PhasingStatsResult(int goodEdgeCount, float edgeErrorMax, float edgeErrorRss, float residualEdgeErrorMax, float residualEdgeErrorRss) {
		this.goodEdgeCount = goodEdgeCount;
		this.edgeErrorMax = edgeErrorMax;
		this.edgeErrorRss = edgeErrorRss;
		this.residualEdgeErrorMax = residualEdgeErrorMax;
		this.residualEdgeErrorRss = residualEdgeErrorRss;
	}

	public int getGoodEdgeCount() {
		return goodEdgeCount;
	}

	public void setGoodEdgeCount(int goodEdgeCount) {
		this.goodEdgeCount = goodEdgeCount;
	}

	public float getEdgeErrorMax() {
		return edgeErrorMax;
	}

	public void setEdgeErrorMax(float edgeErrorMax) {
		this.edgeErrorMax = edgeErrorMax;
	}

	public float getEdgeErrorRss() {
		return edgeErrorRss;
	}

	public void setEdgeErrorRss(float edgeErrorRss) {
		this.edgeErrorRss = edgeErrorRss;
	}

	public float getResidualEdgeErrorMax() {
		return residualEdgeErrorMax;
	}

	public void setResidualEdgeErrorMax(float residualEdgeErrorMax) {
		this.residualEdgeErrorMax = residualEdgeErrorMax;
	}

	public float getResidualEdgeErrorRss() {
		return residualEdgeErrorRss;
	}

	public void setResidualEdgeErrorRss(float residualEdgeErrorRss) {
		this.residualEdgeErrorRss = residualEdgeErrorRss;
	}

}
