package org.tmt.aps.peas.computation.model;

public class AvgCentroidStatsResult extends CentroidStatsResult {

	public AvgCentroidStatsResult(int maxSpotNum, float maxOffset, float rmsOffset, float enclosedEnergy80, float enclosedEnergy50) {
		super(maxSpotNum, maxOffset, rmsOffset, enclosedEnergy80, enclosedEnergy50);
	}
	
	public AvgCentroidStatsResult() {};

	
}
