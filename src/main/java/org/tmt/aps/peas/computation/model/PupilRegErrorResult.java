package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>calculatePupilRegError</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculatePupilRegError(org.tmt.aps.peas.config.model.PupilRegErrorConfig, org.tmt.aps.peas.refBeamMap.model.CentroidMap, int, float[], float[], float[], float, float, int[], int[], int[])
 */
public class PupilRegErrorResult {

	private float regErrorX; // x registration error (m)
	private float regErrorY; // y registration error (m)
	private float regErrorPhi; // phi rotation error (r)
	private float regErrorApproxX; // x registration error using approx calc (m)
	private float regErrorApproxY; // y registration error using approx calc (m)
	private float regErrorApproxPhi; // phi rotation error using approx calc (r)
	private float regScaleError; // scale error
	
	public PupilRegErrorResult() {};
	
	public PupilRegErrorResult(float regErrorX, float regErrorY, float regErrorPhi, float regErrorApproxX, 
			float regErrorApproxY, float regErrorApproxPhi, float regScaleError) { 
	
		this.regErrorX = regErrorX;
		this.regErrorY = regErrorY;
		this.regErrorPhi = regErrorPhi;
		this.regErrorApproxX = regErrorApproxX;
		this.regErrorApproxY = regErrorApproxY;
		this.regErrorApproxPhi = regErrorApproxPhi;
		this.regScaleError = regScaleError;
	
	}
	
	public float getRegErrorX() {
		return regErrorX;
	}
	public void setRegErrorX(float regErrorX) {
		this.regErrorX = regErrorX;
	}
	public float getRegErrorY() {
		return regErrorY;
	}
	public void setRegErrorY(float regErrorY) {
		this.regErrorY = regErrorY;
	}
	public float getRegErrorPhi() {
		return regErrorPhi;
	}
	public void setRegErrorPhi(float regErrorPhi) {
		this.regErrorPhi = regErrorPhi;
	}
	public float getRegErrorApproxX() {
		return regErrorApproxX;
	}
	public void setRegErrorApproxX(float regErrorApproxX) {
		this.regErrorApproxX = regErrorApproxX;
	}
	public float getRegErrorApproxY() {
		return regErrorApproxY;
	}
	public void setRegErrorApproxY(float regErrorApproxY) {
		this.regErrorApproxY = regErrorApproxY;
	}
	public float getRegErrorApproxPhi() {
		return regErrorApproxPhi;
	}
	public void setRegErrorApproxPhi(float regErrorApproxPhi) {
		this.regErrorApproxPhi = regErrorApproxPhi;
	}
	public float getRegScaleError() {
		return regScaleError;
	}
	public void setRegScaleError(float regScaleError) {
		this.regScaleError = regScaleError;
	}

	
}
