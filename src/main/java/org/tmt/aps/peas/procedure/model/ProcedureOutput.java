package org.tmt.aps.peas.procedure.model;

import java.util.List;


public class ProcedureOutput {

	Long procedureId;


	List<ProcedureIterationOutput> procedureIterationOutputList;

	List<ProcedureOutputValue> procedureOutputList;

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


	public List<ProcedureOutputValue> getProcedureOutputList() {
		return procedureOutputList;
	}


	public void setProcedureOutputList(List<ProcedureOutputValue> procedureOutputList) {
		this.procedureOutputList = procedureOutputList;
	}
	
	
}
