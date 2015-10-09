package org.tmt.aps.peas.computation.model;

public class CalcM2ActuatorsFromPttResult {

	float[] deltaSecondardyActCmds;

	
	public CalcM2ActuatorsFromPttResult(float[] deltaSecondardyActCmds) {
		this.deltaSecondardyActCmds = deltaSecondardyActCmds;
	}

	public float[] getDeltaSecondardyActCmds() {
		return deltaSecondardyActCmds;
	}

	public void setDeltaSecondardyActCmds(float[] deltaSecondardyActCmds) {
		this.deltaSecondardyActCmds = deltaSecondardyActCmds;
	}
	
	
}
