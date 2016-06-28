package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for calculateSufsZernikeStats computation.  Contains Zernike statistics for each segment in the SUFS group.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateSufsZernikeStats(SufsSegmentZernikeResult[])
 */
public class SufsSegmentZernikeStatsResult {
	
	float[][] zernikeMeans;
	float[][] zernikeEoms;
	
	public SufsSegmentZernikeStatsResult() {
		
	}
	/**
	 * Constructor using array of SufsZernikeStatsResults objects, one per segment.
	 * @param sufsZernikeStatsResults array of Zernike statistics objects 
	 */
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
	
	/**
	 * 2-d segment indexed array of means of Zernikes for a segment
	 */
	public float[][] getZernikeMeans() {
		return zernikeMeans;
	}

	/**
	 * 2-d segment indexed array of means of Zernikes for a segment
	 */
	public void setZernikeMeans(float[][] zernikeMeans) {
		this.zernikeMeans = zernikeMeans;
	}

	/**
	 * 2-d segment indexed array of EOMs of Zernikes for a segment
	 */
	public float[][] getZernikeEoms() {
		return zernikeEoms;
	}

	/**
	 * 2-d segment indexed array of EOMs of Zernikes for a segment
	 */
	public void setZernikeEoms(float[][] zernikeEoms) {
		this.zernikeEoms = zernikeEoms;
	}

	/**
	 * @return an array of SufsZernikeStatsResults, one for each segment in the SUFS group.
	 */
	public SufsZernikeStatsResult[] getSufsZernikeStatsResult() {
		
		SufsZernikeStatsResult[] result = new SufsZernikeStatsResult[7];
		for (int i=0; i<7; i++) {
		
			result[i] = new SufsZernikeStatsResult(zernikeMeans[i], zernikeEoms[i]);
		
		}
		return result;
	}


}
