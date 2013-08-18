package org.tmt.aps.peas.procedure.business;

import javax.ejb.Singleton;

import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;

@Singleton
public class ProcedureExecutionState {

	private boolean executionStatus;
	private int percentComplete;
	private CcdFrame currentFrame;
	private Procedure currentProcedure;

	public void init(Procedure procedure) {
		currentProcedure = procedure;
	}
	
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

	public CcdFrame getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(CcdFrame currentFrame) {
		this.currentFrame = currentFrame;
	}

	public Procedure getCurrentProcedure() {
		return currentProcedure;
	}

	public void setCurrentProcedure(Procedure currentProcedure) {
		this.currentProcedure = currentProcedure;
	}


}
