package org.tmt.aps.peas.computation.model;

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
