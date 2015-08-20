/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;

@Interceptor
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
		
		return ctx.proceed();
		
	}
	
	
	

}
