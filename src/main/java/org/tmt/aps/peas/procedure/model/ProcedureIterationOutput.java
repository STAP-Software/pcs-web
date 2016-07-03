package org.tmt.aps.peas.procedure.model;

import java.util.List;

/**
 * Base procedure iteration output class containing accessor methods
 * @author smichaels
 *
 */
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

	/**
	 * Used for report generation, which only uses procedure field names and not result structures.
	 * @return a list of the procedure output values in this object. 
	 */
	public List<ProcedureOutputValue> getProcedureIterationOutputList() {
		return procedureIterationOutputList;
	}

	/**
	 * Population method which only populates procedure output values as a list and not sub-class result structures
	 * This is used for report generation which is not procedure specific
	 * @param procedureIterationOutputList
	 */
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
