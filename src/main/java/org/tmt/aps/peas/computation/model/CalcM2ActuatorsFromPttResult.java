package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for calcM2ActuatorsFromPtt computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calcM2ActuatorsFromPtt(float, org.tmt.aps.peas.common.FloatPoint, float)
 */
public class CalcM2ActuatorsFromPttResult {

	float[] deltaSecondardyActCmds;

	
	public CalcM2ActuatorsFromPttResult(float[] deltaSecondardyActCmds) {
		this.deltaSecondardyActCmds = deltaSecondardyActCmds;
	}
	
	public CalcM2ActuatorsFromPttResult() {
	}

	public float[] getDeltaSecondardyActCmds() {
		return deltaSecondardyActCmds;
	}

	public void setDeltaSecondardyActCmds(float[] deltaSecondardyActCmds) {
		this.deltaSecondardyActCmds = deltaSecondardyActCmds;
	}
	
	
}
