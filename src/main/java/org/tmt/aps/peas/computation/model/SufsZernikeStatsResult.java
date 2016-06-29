package org.tmt.aps.peas.computation.model;

/**
 * Computation data intermediate result class for <b>SufsSegmentZernikeStatsResult</b> computation.  Contains Zernike means and eoms over iterations for a single segment.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult
 */
public class SufsZernikeStatsResult {
	
	float[] zernikeMeans;
	float[] zernikeEoms;
	
	public SufsZernikeStatsResult() {
		
	}
	
	public SufsZernikeStatsResult(float[] zernikeMeans, float[] zernikeEoms) {
		this.zernikeMeans = zernikeMeans;
		this.zernikeEoms = zernikeEoms;
	}

	public float[] getZernikeMeans() {
		return zernikeMeans;
	}

	public void setZernikeMeans(float[] zernikeMeans) {
		this.zernikeMeans = zernikeMeans;
	}

	public float[] getZernikeEoms() {
		return zernikeEoms;
	}

	public void setZernikeEoms(float[] zernikeEoms) {
		this.zernikeEoms = zernikeEoms;
	}



	
}
