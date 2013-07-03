package org.tmt.aps.peas.procedure.business;

import javax.ejb.Singleton;

@Singleton
public class ProcedureExecutionMgmt {

	private boolean executionStatus;
	private int percentComplete;

	public boolean getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}


}
