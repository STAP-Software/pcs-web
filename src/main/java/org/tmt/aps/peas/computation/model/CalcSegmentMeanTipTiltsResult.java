package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for <b>calcSegmentMeanTipTilts</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calcSegmentMeanTipTilts(FloatPoint[][])
 */
public class CalcSegmentMeanTipTiltsResult {

	FloatPoint[] segmentMeanTipTiltErrors;
	
	public CalcSegmentMeanTipTiltsResult(FloatPoint[] segmentMeanTipTiltErrors) {
		this.segmentMeanTipTiltErrors = segmentMeanTipTiltErrors;
	}
	
	public CalcSegmentMeanTipTiltsResult() {};

	public FloatPoint[] getSegmentMeanTipTiltErrors() {
		return segmentMeanTipTiltErrors;
	}

	public void setSegmentMeanTipTiltErrors(FloatPoint[] segmentMeanTipTiltErrors) {
		this.segmentMeanTipTiltErrors = segmentMeanTipTiltErrors;
	}


}
