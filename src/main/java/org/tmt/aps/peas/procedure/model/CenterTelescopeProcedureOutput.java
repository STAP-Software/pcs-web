package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;

public class CenterTelescopeProcedureOutput extends ProcedureOutput {

	private FloatPoint centroidGuess;
	private FloatPoint centroid;
	private FloatPoint deltaAzEl;
	private boolean cmdTelescope;
	
	
	public FloatPoint getCentroidGuess() {
		return centroidGuess;
	}
	public void setCentroidGuess(FloatPoint centroidGuess) {
		this.centroidGuess = centroidGuess;
	}
	public FloatPoint getCentroid() {
		return centroid;
	}
	public void setCentroid(FloatPoint centroid) {
		this.centroid = centroid;
	}
	public FloatPoint getDeltaAzEl() {
		return deltaAzEl;
	}
	public void setDeltaAzEl(FloatPoint deltaAzEl) {
		this.deltaAzEl = deltaAzEl;
	}
	public boolean isCmdTelescope() {
		return cmdTelescope;
	}
	public void setCmdTelescope(boolean cmdTelescope) {
		this.cmdTelescope = cmdTelescope;
	}
	
	
	
}
