/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import javax.ejb.Singleton;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutputable;

/**
 * Singleton EJB containing procedure state information: run state, procedure/subprocedure context, 
 * queues abort requests, manages the current output target and execution iteration count
 * Manages a procedure stack to handle state of subprocedures and superprocedures.
 * @author smichaels
 *
 */
@Singleton
@Named
public class ProcedureExecutionState {

	Logger logger = Logger.getLogger(this.getClass());

	private Procedure procedureStack;
	private Procedure pendingSubProcedure;
	private boolean subProcedureStartRequested;
	private boolean subProcedureEndRequested;
	
	private int currentIteration;
	
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
		currentIteration = 0;
	}
	
	public boolean getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public int getPercentComplete() {
		return (currentProcedure == null) ? 0 : currentProcedure.getPercentComplete();
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
	
	public Procedure getSuperProcedure() {
		return procedureStack;
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

	/**
	 * Pushes the current superprocedure onto the stack, sets the currentProcedure to the pending subprocedure
	 * @return the now current procedure (the subprocedure)
	 */
	public Procedure transferControlToSubProcedure() {
		procedureStack = currentProcedure;
		currentProcedure = pendingSubProcedure;
		subProcedureStartRequested = false;
		pendingSubProcedure = null;
		return currentProcedure;
	}

	/**
	 * Pops the superprocedure off the stack and sets it as the current procedure
	 * @return the now current procedure 
	 */
	public Procedure transferControlFromSubProcedure() {
		
		currentProcedure = procedureStack;
		procedureStack = null;
		subProcedureEndRequested = true;
		
		return currentProcedure;
	}

	/**
	 * 
	 * @return true if the current execution context is a subprocedure
	 */
	public boolean isExecutionContextSubProcedure() {
		return procedureStack != null;
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

	public int getCurrentIteration() {
		return currentIteration;
	}

	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
	}

	public void incrementIteration() {
		currentIteration++;
	}

}
