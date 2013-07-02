package org.tmt.aps.peas.passiveTilt.model;

public class PassiveTiltConfig {

	private int filter;
	private float integrationTime;
	private int numberOfTrials = 1;
	private int frameSource;

	private PassiveTiltAdvancedConfig advancedOptions;
	private PassiveTiltWorkflowPreferences executionPreferences;

	
	public PassiveTiltConfig() {
		this.advancedOptions = new PassiveTiltAdvancedConfig();
		this.executionPreferences = new PassiveTiltWorkflowPreferences();
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

	public PassiveTiltAdvancedConfig getAdvancedOptions() {
		return advancedOptions;
	}

	public void setAdvancedOptions(PassiveTiltAdvancedConfig advancedOptions) {
		this.advancedOptions = advancedOptions;
	}

	public PassiveTiltWorkflowPreferences getExecutionPreferences() {
		return executionPreferences;
	}

	public void setExecutionPreferences(PassiveTiltWorkflowPreferences executionPreferences) {
		this.executionPreferences = executionPreferences;
	}

}
