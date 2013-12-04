package org.tmt.aps.peas.procedure.model;

import java.util.List;

public abstract class ProcedureOutput {

	Long procedureId;


	List<ProcedureIterationOutput> procedureIterationOutputList;


	public Long getProcedureId() {
		return procedureId;
	}


	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}


	public List<ProcedureIterationOutput> getProcedureIterationOutputList() {
		return procedureIterationOutputList;
	}


	public void setProcedureIterationOutputList(List<ProcedureIterationOutput> procedureIterationOutputList) {
		this.procedureIterationOutputList = procedureIterationOutputList;
	}
	
	
}
