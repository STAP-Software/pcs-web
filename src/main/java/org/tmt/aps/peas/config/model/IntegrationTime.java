package org.tmt.aps.peas.config.model;

public class IntegrationTime implements IterableEntity {

	Long integrationTimeKey;
	float integrationTime;
	
	public IntegrationTime(Long integrationTimeKey, float integrationTime) {
		this.integrationTimeKey = integrationTimeKey;
		this.integrationTime = integrationTime;
	}
	
	public String getClassName() {
		return this.getClass().getName();
	}


	public String getKeyFieldName() {
		return "IntegrationTimeKey";
	}


	public String getLabelFieldName() {
		return "IntegrationTime";
	}


	public String getLabel() {
		return "Integration Time";
	}
	
	public float getIntegrationTime() {
		return integrationTime;
	}

	public Long getIntegrationTimeKey() {
		return integrationTimeKey;
	}
}
