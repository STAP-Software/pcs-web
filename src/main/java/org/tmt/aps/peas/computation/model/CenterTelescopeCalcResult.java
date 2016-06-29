package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for <b>centerTelescopeCalc</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#centerTelescopeCalc(FloatPoint, FloatPoint, double)
 */
public class CenterTelescopeCalcResult {

	private FloatPoint deltaAzEl;

	public CenterTelescopeCalcResult(FloatPoint deltaAzEl) {
		this.deltaAzEl = deltaAzEl;
	}
	
	public CenterTelescopeCalcResult() {};
	 
	
	public FloatPoint getDeltaAzEl() {
		return deltaAzEl;
	}

	public void setDeltaAzEl(FloatPoint deltaAzEl) {
		this.deltaAzEl = deltaAzEl;
	}
	
	
}
