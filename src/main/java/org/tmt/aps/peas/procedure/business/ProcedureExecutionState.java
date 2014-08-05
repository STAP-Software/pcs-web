/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;

@Singleton
public class ProcedureExecutionState {

	Logger logger = Logger.getLogger(this.getClass());

	private boolean executionStatus;
	private int percentComplete;
	private CcdFrame currentFrame;
	private Procedure currentProcedure;

	public void init(Procedure procedure) {
		currentProcedure = procedure;
		executionStatus = false;
		percentComplete = 0;
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
