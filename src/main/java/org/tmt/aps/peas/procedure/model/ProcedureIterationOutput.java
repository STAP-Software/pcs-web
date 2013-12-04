package org.tmt.aps.peas.procedure.model;

public abstract class ProcedureIterationOutput {

	Long procedureId;

	int iteration;

	
	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	
	
}
