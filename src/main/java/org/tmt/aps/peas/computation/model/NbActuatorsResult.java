package org.tmt.aps.peas.computation.model;

public class NbActuatorsResult {

	float[] actCalc;
	float[] resid;
	int constrainedSegmentCount;
	int goodEdgeCount;
	float edgeResMax;
	float edgeResRms;
	float segmentPistonRms;
	
	public NbActuatorsResult(float[] actCalc, float[] resid, int constrainedSegmentCount, int goodEdgeCount, float edgeResMax,
			float edgeResRms, float segmentPistonRms) {
		
		this.actCalc = actCalc;
		this.resid = resid;
		this.constrainedSegmentCount = constrainedSegmentCount;
		this.goodEdgeCount = goodEdgeCount;
		this.edgeResMax = edgeResMax;
		this.edgeResRms = edgeResRms;
		this.segmentPistonRms = segmentPistonRms;
	}



	public float[] getActCalc() {
		return actCalc;
	}

	public void setActCalc(float[] actCalc) {
		this.actCalc = actCalc;
	}

	public float[] getResid() {
		return resid;
	}

	public void setResid(float[] resid) {
		this.resid = resid;
	}

	public int getConstrainedSegmentCount() {
		return constrainedSegmentCount;
	}

	public void setConstrainedSegmentCount(int constrainedSegmentCount) {
		this.constrainedSegmentCount = constrainedSegmentCount;
	}

	public int getGoodEdgeCount() {
		return goodEdgeCount;
	}

	public void setGoodEdgeCount(int goodEdgeCount) {
		this.goodEdgeCount = goodEdgeCount;
	}

	public float getEdgeResMax() {
		return edgeResMax;
	}

	public void setEdgeResMax(float edgeResMax) {
		this.edgeResMax = edgeResMax;
	}

	public float getEdgeResRms() {
		return edgeResRms;
	}

	public void setEdgeResRms(float edgeResRms) {
		this.edgeResRms = edgeResRms;
	}

	public float getSegmentPistonRms() {
		return segmentPistonRms;
	}

	public void setSegmentPistonRms(float segmentPistonRms) {
		this.segmentPistonRms = segmentPistonRms;
	}

}
