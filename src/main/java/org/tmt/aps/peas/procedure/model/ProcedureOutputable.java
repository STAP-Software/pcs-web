package org.tmt.aps.peas.procedure.model;

/**
 * Interface for any data output by a procedure that can map to the procedure output table
 * @author smichaels
 *
 */
public interface ProcedureOutputable {

	public Long getProcedureId();

	public void setProcedureId(Long procedureId);

	public Integer getIteration();

	public void setIteration(Integer iteration);

	//public List<ProcedureOutputValue> getProcedureOutputList();
	//public void setProcedureOutputList(List<ProcedureOutputValue> procedureOutputList);
	
}
