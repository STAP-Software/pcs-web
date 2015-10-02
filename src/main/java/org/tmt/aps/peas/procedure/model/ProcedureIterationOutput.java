package org.tmt.aps.peas.procedure.model;

import java.util.List;

public abstract class ProcedureIterationOutput implements ProcedureOutputable {

	Long procedureId;

	Integer iteration;

	List<ProcedureOutputValue> procedureIterationOutputList;
	
	ProcedureIterationDecisionLog procedureIterationDecisionLog = new ProcedureIterationDecisionLog();

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public Integer getIteration() {
		return iteration;
	}

	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}

	public List<ProcedureOutputValue> getProcedureIterationOutputList() {
		return procedureIterationOutputList;
	}

	public void setProcedureIterationOutputList(List<ProcedureOutputValue> procedureIterationOutputList) {
		this.procedureIterationOutputList = procedureIterationOutputList;
	}

	public ProcedureIterationDecisionLog getProcedureIterationDecisionLog() {
		return procedureIterationDecisionLog;
	}

	public void setProcedureIterationDecisionLog(ProcedureIterationDecisionLog procedureIterationDecisionLog) {
		this.procedureIterationDecisionLog = procedureIterationDecisionLog;
	}

	
}
