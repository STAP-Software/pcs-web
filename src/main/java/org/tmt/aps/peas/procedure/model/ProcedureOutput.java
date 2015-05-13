package org.tmt.aps.peas.procedure.model;

import java.util.ArrayList;
import java.util.List;


public class ProcedureOutput implements ProcedureOutputable {

	Long procedureId;


	List<ProcedureIterationOutput> procedureIterationOutputList = new ArrayList<ProcedureIterationOutput>();

	List<ProcedureOutputValue> procedureOutputList;

	public Long getProcedureId() {
		return procedureId;
	}

	public Integer getIteration() {
		return null;
	}

	public void setIteration(Integer iteration) {};
	
	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public void addIteration(ProcedureIterationOutput pio) {
		if (procedureIterationOutputList == null) {
			procedureIterationOutputList = new ArrayList<ProcedureIterationOutput>();
		}
		procedureIterationOutputList.add(pio);
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
	
	// subclasses will override this
	public int getM1SnapNumberAfter() {
		return -1;
	}

	public String getM1SnapNumberAfterDisplayText() {
		return getM1SnapNumberAfter() == -1 ? "None" : "" + getM1SnapNumberAfter();
	}
}
