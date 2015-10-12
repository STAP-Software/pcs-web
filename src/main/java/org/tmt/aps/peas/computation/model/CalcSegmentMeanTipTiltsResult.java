package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

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
