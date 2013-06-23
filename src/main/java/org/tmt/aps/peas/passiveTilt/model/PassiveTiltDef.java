package org.tmt.aps.peas.passiveTilt.model;

public class PassiveTiltDef {

	private int filter;
	private float integrationTime;
	private int numberOfTrials;
	private int frameSource;

	private PassiveTiltAdvancedOptions advancedOptions;
	private PassiveTiltExecutionPreferences executionPreferences;

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

	public PassiveTiltAdvancedOptions getAdvancedOptions() {
		return advancedOptions;
	}

	public void setAdvancedOptions(PassiveTiltAdvancedOptions advancedOptions) {
		this.advancedOptions = advancedOptions;
	}

	public PassiveTiltExecutionPreferences getExecutionPreferences() {
		return executionPreferences;
	}

	public void setExecutionPreferences(PassiveTiltExecutionPreferences executionPreferences) {
		this.executionPreferences = executionPreferences;
	}

}
