package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for fixPistons computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#fixPistons(org.tmt.aps.peas.common.FloatPoint[], float[])
 */
public class FixPistonsResult {

	private float[] actRaw;
	private float[] actFixed;
	private float actRms;

	
	public FixPistonsResult() {
		
	}

	public FixPistonsResult(float[] actRaw, float[] actFixed, float actRms) {
		this.actRaw = actRaw;
		this.actFixed = actFixed;
		this.actRms = actRms;
	}

	
	public float[] getActRaw() {
		return actRaw;
	}

	public void setActRaw(float[] actRaw) {
		this.actRaw = actRaw;
	}

	public float[] getActFixed() {
		return actFixed;
	}

	public void setActFixed(float[] actFixed) {
		this.actFixed = actFixed;
	}

	public float getActRms() {
		return actRms;
	}

	public void setActRms(float actRms) {
		this.actRms = actRms;
	}
	
}
