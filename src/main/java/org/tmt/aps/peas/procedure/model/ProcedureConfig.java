package org.tmt.aps.peas.procedure.model;

public class ProcedureConfig {

	private int filter;
	private float integrationTime;
	private int numberOfTrials = 1;
	private int frameSource;

	protected ProcedureAdvancedConfig advancedOptions;
	protected ProcedureWorkflowPreferences executionPreferences;

	
	public ProcedureConfig() {
		this.advancedOptions = new ProcedureAdvancedConfig();
		this.executionPreferences = new ProcedureWorkflowPreferences();
	}
	
	public int getFilter() {
		return filter;
	}

	public void setFilter(int filter) {
		this.filter = filter;
	}

	public float getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(float integrationTime) {
		this.integrationTime = integrationTime;
	}

	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(int numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}

	public int getFrameSource() {
		return frameSource;
	}

	public void setFrameSource(int frameSource) {
		this.frameSource = frameSource;
	}

	public ProcedureAdvancedConfig getAdvancedOptions() {
		return advancedOptions;
	}

	public void setAdvancedOptions(ProcedureAdvancedConfig advancedOptions) {
		this.advancedOptions = advancedOptions;
	}

	public ProcedureWorkflowPreferences getExecutionPreferences() {
		return executionPreferences;
	}

	public void setExecutionPreferences(ProcedureWorkflowPreferences executionPreferences) {
		this.executionPreferences = executionPreferences;
	}


}
