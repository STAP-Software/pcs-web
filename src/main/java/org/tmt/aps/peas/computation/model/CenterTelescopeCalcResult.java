package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class CenterTelescopeCalcResult {

	private CentroidOffsetsResult centroidOffsetsResult;
	private FloatPoint deltaAzEl;

	public CenterTelescopeCalcResult(CentroidOffsetsResult centroidOffsetsResult, FloatPoint deltaAzEl) {
		this.centroidOffsetsResult = centroidOffsetsResult;
		this.deltaAzEl = deltaAzEl;
	}

	public CentroidOffsetsResult getCentroidOffsetsResult() {
		return centroidOffsetsResult;
	}

	public void setCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		this.centroidOffsetsResult = centroidOffsetsResult;
	}

	public FloatPoint getDeltaAzEl() {
		return deltaAzEl;
	}

	public void setDeltaAzEl(FloatPoint deltaAzEl) {
		this.deltaAzEl = deltaAzEl;
	}
	
	
}
