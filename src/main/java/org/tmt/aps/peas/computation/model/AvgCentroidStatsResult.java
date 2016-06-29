package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>avgCentroidStats</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateAvgCentroidStats(org.tmt.aps.peas.common.FloatPoint[], int[], int[], int[])
 */
public class AvgCentroidStatsResult extends CentroidStatsResult {

	public AvgCentroidStatsResult(int maxSpotNum, float maxOffset, float rmsOffset, float enclosedEnergy80, float enclosedEnergy50) {
		super(maxSpotNum, maxOffset, rmsOffset, enclosedEnergy80, enclosedEnergy50);
	}
	
	public AvgCentroidStatsResult() {};

	
}
