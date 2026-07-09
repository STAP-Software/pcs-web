package org.tmt.aps.peas.procedure.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tmt.aps.peas.computation.model.StartupComputationsResult;

/**
 * Base procedure output class containing accessor and utility methods
 * @author smichaels
 *
 */
public class ProcedureOutput implements ProcedureOutputable {

	Long procedureId;


	List<ProcedureIterationOutput> procedureIterationOutputList = new ArrayList<ProcedureIterationOutput>();

	List<ProcedureOutputValue> procedureOutputList;
	
	Map<String, ProcedureOutputValue> procedureOutputValueMap;

	ProcedureDecisionLog procedureDecisionLog = new ProcedureDecisionLog();
	
	StartupComputationsResult startupComputationsResult = new StartupComputationsResult();
	
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

	/**
	 * Adds an iteratio to this procedure output
	 * @param pio the procedureIterationOutput to add
	 */
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

	/**
	 * @return the number of iterations currently contained in this procedure output
	 */
	public int getIterationCount() {
		return procedureIterationOutputList == null ? 0 : procedureIterationOutputList.size() + 1;
	}

	/**
	 * @return the list of procedure output values for this procedure output
	 */
	public List<ProcedureOutputValue> getProcedureOutputList() {
		return procedureOutputList;
	}


	public void setProcedureOutputList(List<ProcedureOutputValue> procedureOutputList) {
		this.procedureOutputList = procedureOutputList;
	}
	
	/**
	 * Returns a single procedure output value for the passed field name
	 * @param name the field name
	 * @return the procedure output value for that field
	 */
	public ProcedureOutputValue getProcedureOutputValue(String name) {
		// a method to return the ProcedureOutputValue object given its name
		if (procedureOutputValueMap == null) {
			procedureOutputValueMap = new HashMap<String, ProcedureOutputValue>();
			// populate it
			for (ProcedureOutputValue procedureOutputValue : procedureOutputList) {
				procedureOutputValueMap.put(procedureOutputValue.getProcedureOutputField().getFieldName(), procedureOutputValue);
			}
		}
		return procedureOutputValueMap.get(name);
	}
	
	
	/**
	 * Convenience method to return the M1 snap number after the procedure
	 */
	public int getM1SnapNumberAfter() {
		return procedureDecisionLog.getM1SnapNumberAfter();
	}

	/**
	 * Convenience method to return the M1 snap number before the procedure
	 */
	public int getM1SnapNumberBefore() {
		return procedureDecisionLog.getM1SnapNumberBefore();
	}
	
	/**
	 * Convenience method to return a display text for the M1 snap number after the procedure
	 */
	public String getM1SnapNumberAfterDisplayText() {
		return getM1SnapNumberAfter() == -1 ? "None" : "" + getM1SnapNumberAfter();
	}

	/**
	 * Accessor method for getting the procedure decision log
	 */
	public ProcedureDecisionLog getProcedureDecisionLog() {
		return procedureDecisionLog;
	}

	/**
	 * Accessor method for setting the procedure decision log
	 */
	public void setProcedureDecisionLog(ProcedureDecisionLog procedureDecisionLog) {
		this.procedureDecisionLog = procedureDecisionLog;
	}
	
	public StartupComputationsResult getStartupComputationsResult() {
		return startupComputationsResult;
	}

	public void setStartupComputationsResult(StartupComputationsResult startupComputationsResult) {
		this.startupComputationsResult = startupComputationsResult;
	}

	/**
	 * Utility method to return a list of a particular result class type object, over all iterations
	 * @param resultFieldName the field name of the result class in the procedure iteration class
	 * @param resultClass the result class, e.g. FiResult, FindCentResult, etc
	 * @return a list of the given type of result object, over all iterations
	 */
	public <T> List<T> getIterationResultObjectFor(String resultFieldName, Class<T> resultClass) throws Exception {
		
		// get the calcResult object
		List<T> resultList = new ArrayList<T>();
		
		
		for (ProcedureIterationOutput pio : procedureIterationOutputList) {
		
			Class<? extends ProcedureIterationOutput> pioClass = pio.getClass();
			
			Method calcGetterMethod = pioClass.getMethod("get" + resultFieldName);
			
			Object calcResult = calcGetterMethod.invoke(pio);
	
			resultList.add(resultClass.cast(calcResult));		
		}
		
		return resultList;
	}
	
	/**
	 * Utility method to return a specific field value, over all iterations
	 * @param classname the class name of the result class the field resides in
	 * @param fieldname the field name of the field
	 * @param resultClass the class of the field
	 * @return a list of the field objects, over all iterations
	 */
	public <T> List<T> getIterationValuesFor(String classname, String fieldname, Class<T> resultClass) throws Exception {
		
		// get the calcResult object
		List<T> resultList = new ArrayList<T>();
		
		
		for (ProcedureIterationOutput pio : procedureIterationOutputList) {
		
			Class<? extends ProcedureIterationOutput> pioClass = pio.getClass();
			
			Method calcGetterMethod = pioClass.getMethod("get" + classname);
			
			Object calcResult = calcGetterMethod.invoke(pio);
	
			Class<? extends Object> calcClass = calcResult.getClass();
			
			Method fieldGetterMethod = calcClass.getMethod("get" + fieldname);
			
			Object fieldValue = fieldGetterMethod.invoke(calcResult);
			
			resultList.add(resultClass.cast(fieldValue));
					
		}
		
		return resultList;
	}

	
}
