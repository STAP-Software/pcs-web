/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutputable;

@Singleton
public class ProcedureExecutionState {

	Logger logger = Logger.getLogger(this.getClass());

	private Procedure procedureStack;
	private Procedure pendingSubProcedure;
	private boolean subProcedureStartRequested;
	private boolean subProcedureEndRequested;
	
	private boolean executionStatus;
	private boolean abortRequested;
	private boolean onCompletePerformed;
	private CcdFrame currentFrame;
	private Procedure currentProcedure;
	private Throwable procedureException;
	private ProcedureOutputable currentOutputTarget;

	public void init(Procedure procedure) {
		currentProcedure = procedure;
		executionStatus = false;
		currentProcedure.setPercentComplete(0);
		procedureException = null;
		abortRequested = false;
		onCompletePerformed = false;
	}
	
	public boolean getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public int getPercentComplete() {
		return currentProcedure.getPercentComplete();
	}

	@Abortable
	public void setPercentComplete(int percentComplete) {
		currentProcedure.setPercentComplete(percentComplete);
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

	public Throwable getProcedureException() {
		return procedureException;
	}

	public void setProcedureException(Throwable procedureException) {
		this.procedureException = procedureException;
	}

	public boolean getAbortRequested() {
		
		return abortRequested;
	}

	public void setAbortRequested(boolean abortRequested) {
		this.abortRequested = abortRequested;
	}

	public boolean isSubProcedureStartRequested() {
		return subProcedureStartRequested;
	}

	public void setSubProcedureStartRequested(boolean subProcedureStartRequested) {
		this.subProcedureStartRequested = subProcedureStartRequested;
	}

	public boolean isSubProcedureEndRequested() {
		return subProcedureEndRequested;
	}
	
	public void resetSubProcedureEndRequested() {
		subProcedureEndRequested = false;
	}

	public void setPendingSubProcedure(Procedure pendingSubProcedure) {
		this.pendingSubProcedure = pendingSubProcedure;
		subProcedureStartRequested = true;
	}

	public Procedure transferControlToSubProcedure() {
		procedureStack = currentProcedure;
		currentProcedure = pendingSubProcedure;
		subProcedureStartRequested = false;
		pendingSubProcedure = null;
		return currentProcedure;
	}

	public Procedure transferControlFromSubProcedure() {
		currentProcedure = procedureStack;
		procedureStack = null;
		subProcedureEndRequested = true;
		return currentProcedure;
	}

	public void requestCompleteProcedure() {
		if (procedureStack != null) {
			transferControlFromSubProcedure();
		} else {
			executionStatus = false;
		}
	}

	public void setOnCompletePerformed(boolean onCompletePerformed) {
		this.onCompletePerformed = onCompletePerformed;
	}

	public boolean getOnCompletePerformed() {
		return onCompletePerformed;
	}

	public ProcedureOutputable getCurrentOutputTarget() {
		return currentOutputTarget;
	}

	public void setCurrentOutputTarget(ProcedureOutputable currentOutputTarget) {
		this.currentOutputTarget = currentOutputTarget;
	}


}
