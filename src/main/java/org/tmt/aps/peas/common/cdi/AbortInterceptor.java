/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.annotation.Priority;

import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;

/**
 * CDI interceptor for aborting a procedure.  Every method that contains the <code>@Abortable</code> annotation checks the 
 * {@link org.tmt.aps.peas.procedure.business.ProcedureExecutionState#getAbortRequested()} method.  If true, the interceptor will throw an {@link org.tmt.aps.peas.procedure.exception.AbortProcedureException}.
 * Typically, the {@link org.tmt.aps.peas.procedure.ui.AsyncController} will set the abort request when the user presses an Abort button on any screen.
 * @author smichaels
 * @see org.tmt.aps.peas.procedure.exception.AbortProcedureException
 * @see org.tmt.aps.peas.procedure.business.ProcedureExecutionState#getAbortRequested()
 * @see org.tmt.aps.peas.procedure.ui.AsyncController
 */
@Interceptor
@Priority(1)
@Abortable
public class AbortInterceptor implements Serializable {

	private static final long serialVersionUID = 5885028293442969007L;

	@EJB
	ProcedureExecutionState procedureExecutionState;
	
	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {

		if (procedureExecutionState.getAbortRequested()) {
			throw new AbortProcedureException("The procedure has been aborted by the operator.");
		}
		
		Object result = ctx.proceed();

		if (procedureExecutionState.getAbortRequested()) {
			throw new AbortProcedureException("The procedure has been aborted by the operator.");
		}
		
		return result;
		
	}
	
	
	

}
