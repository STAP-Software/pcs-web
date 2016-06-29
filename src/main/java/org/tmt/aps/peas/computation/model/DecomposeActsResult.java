package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>decomposeActs</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#decomposeActs(float[][])
 */
public class DecomposeActsResult {

	float[][] tipTiltActs;
	float[][] pistonActs;
	
	public DecomposeActsResult(float[][] tipTiltActs, float[][] pistonActs) {
		this.tipTiltActs = tipTiltActs;
		this.pistonActs = pistonActs;
	}
	
	public DecomposeActsResult() {};
	
	public float[][] getTipTiltActs() {
		return tipTiltActs;
	}
	public void setTipTiltActs(float[][] tipTiltActs) {
		this.tipTiltActs = tipTiltActs;
	}
	public float[][] getPistonActs() {
		return pistonActs;
	}
	public void setPistonActs(float[][] pistonActs) {
		this.pistonActs = pistonActs;
	}
	
	
}
