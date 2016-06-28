package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for calculatePseudoCentroidStats computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculatePseudoCentroidStats(org.tmt.aps.peas.common.FloatPoint[], int[])
 */
public class PseudoTipTiltCentroidStatsResult extends CentroidStatsResult {

	public PseudoTipTiltCentroidStatsResult(int maxSpotNum, float maxOffset, float rmsOffset, float enclosedEnergy80, float enclosedEnergy50) {
		super(maxSpotNum, maxOffset, rmsOffset, enclosedEnergy80, enclosedEnergy50);
	}
	
	public PseudoTipTiltCentroidStatsResult() {};

	
}
