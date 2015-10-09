package org.tmt.aps.peas.procedure.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ProcedureOutput implements ProcedureOutputable {

	Long procedureId;


	List<ProcedureIterationOutput> procedureIterationOutputList = new ArrayList<ProcedureIterationOutput>();

	List<ProcedureOutputValue> procedureOutputList;

	ProcedureDecisionLog procedureDecisionLog = new ProcedureDecisionLog();
	
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
		pio.setIteration(procedureIterationOutputList.size()-1);
	}

	public List<ProcedureIterationOutput> getProcedureIterationOutputList() {
		return procedureIterationOutputList;
	}

	public void setProcedureIterationOutputList(List<ProcedureIterationOutput> procedureIterationOutputList) {
		this.procedureIterationOutputList = procedureIterationOutputList;
	}

	public int getIterationCount() {
		return procedureIterationOutputList == null ? 0 : procedureIterationOutputList.size() + 1;
	}

	public List<ProcedureOutputValue> getProcedureOutputList() {
		return procedureOutputList;
	}


	public void setProcedureOutputList(List<ProcedureOutputValue> procedureOutputList) {
		this.procedureOutputList = procedureOutputList;
	}
	
	// subclasses will override this
	public int getM1SnapNumberAfter() {
		return procedureDecisionLog.getM1SnapNumberAfter();
	}

	public String getM1SnapNumberAfterDisplayText() {
		return getM1SnapNumberAfter() == -1 ? "None" : "" + getM1SnapNumberAfter();
	}

	public ProcedureDecisionLog getProcedureDecisionLog() {
		return procedureDecisionLog;
	}

	public void setProcedureDecisionLog(ProcedureDecisionLog procedureDecisionLog) {
		this.procedureDecisionLog = procedureDecisionLog;
	}
	
	public <T> List<T> getIterationValuesFor(String classname, String fieldname, Class<T> resultClass) throws Exception {
		
		// get the calcResult object
		List<T> resultList = new ArrayList<T>();
		
		
		for (ProcedureIterationOutput pio : procedureIterationOutputList) {
		
			Class pioClass = pio.getClass();
			
			Method calcGetterMethod = pioClass.getMethod("get" + classname, new Class[0]);
			
			Object calcResult = calcGetterMethod.invoke(pio, new Object[0]);
	
			Class calcClass = calcResult.getClass();
			
			Method fieldGetterMethod = calcClass.getMethod("get" + fieldname, new Class[0]);
			
			Object fieldValue = fieldGetterMethod.invoke(calcResult, new Object[0]);
			
			resultList.add(resultClass.cast(fieldValue));
					
		}
		
		return resultList;
	}

	
}
