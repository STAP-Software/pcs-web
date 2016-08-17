package org.tmt.aps.peas.computation.model;

public class NbActuatorsResult {

	float[] actNoplaneCmd;
	float[] resid;
	int constrainedSegmentCount;
	int goodEdgeCount;
	float edgeResMax;
	float edgeResRms;
	float actRms;
	
	public NbActuatorsResult(float[] actNoplaneCmd, float[] resid, int constrainedSegmentCount, int goodEdgeCount, float edgeResMax,
			float edgeResRms, float actRms) {
		
		this.actNoplaneCmd = actNoplaneCmd;
		this.resid = resid;
		this.constrainedSegmentCount = constrainedSegmentCount;
		this.goodEdgeCount = goodEdgeCount;
		this.edgeResMax = edgeResMax;
		this.edgeResRms = edgeResRms;
		this.actRms = actRms;
	}

	public float[] getActNoplaneCmd() {
		return actNoplaneCmd;
	}

	public void setActNoplaneCmd(float[] actNoplaneCmd) {
		this.actNoplaneCmd = actNoplaneCmd;
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

	public float getActRms() {
		return actRms;
	}

	public void setActRms(float actRms) {
		this.actRms = actRms;
	}

	
}
