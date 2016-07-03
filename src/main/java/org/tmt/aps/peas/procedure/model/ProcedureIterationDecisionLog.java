package org.tmt.aps.peas.procedure.model;

/**
 * Procedure output data of decision branches made during a procedure iteration
 * @author smichaels
 *
 */
public class ProcedureIterationDecisionLog {

	private boolean telescopeMoved;
	
	
	public boolean isTelescopeMoved() {
		return telescopeMoved;
	}

	public void setTelescopeMoved(boolean telescopeMoved) {
		this.telescopeMoved = telescopeMoved;
	}

	
}
