package org.tmt.aps.peas.computation.model;

public class SufsSegmentZernikeStatsResult {
	
	float[][] zernikeMeans;
	float[][] zernikeEoms;
	
	public SufsSegmentZernikeStatsResult() {
		
	}
	
	public SufsSegmentZernikeStatsResult(SufsZernikeStatsResult[] sufsZernikeStatsResults) {
				
		// TODO: we may need to make these as large as the largest number of zernikes since multiple segments are stored together
		
		int meanCount = sufsZernikeStatsResults[0].getZernikeMeans().length;
		int eomCount = sufsZernikeStatsResults[0].getZernikeEoms().length;
			
		zernikeMeans = new float[sufsZernikeStatsResults.length][meanCount];
		zernikeEoms = new float[sufsZernikeStatsResults.length][eomCount];
		
		for (int i=0; i<sufsZernikeStatsResults.length; i++) {
			zernikeMeans[i] = sufsZernikeStatsResults[i].getZernikeMeans();
			zernikeEoms[i] = sufsZernikeStatsResults[i].getZernikeEoms();
		}
	}

	public float[][] getZernikeMeans() {
		return zernikeMeans;
	}

	public void setZernikeMeans(float[][] zernikeMeans) {
		this.zernikeMeans = zernikeMeans;
	}

	public float[][] getZernikeEoms() {
		return zernikeEoms;
	}

	public void setZernikeEoms(float[][] zernikeEoms) {
		this.zernikeEoms = zernikeEoms;
	}

	public SufsZernikeStatsResult[] getSufsZernikeStatsResult() {
		
		SufsZernikeStatsResult[] result = new SufsZernikeStatsResult[7];
		for (int i=0; i<7; i++) {
		
			result[i] = new SufsZernikeStatsResult(zernikeMeans[i], zernikeEoms[i]);
		
		}
		return result;
	}


}
