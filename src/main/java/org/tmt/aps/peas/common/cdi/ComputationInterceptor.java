/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013-2026 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;
import java.lang.reflect.Method;

import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.annotation.Priority;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;

/**
 * Computation interceptor.  Every method with the <code>@Computation</code> annotation calls the method in this class with the 
 * <code>@AroundInvoke</code> tag.  Once a computation call so annotated has completed, this interceptor will populate the current 
 * output target, which is a subclass of either {@link org.tmt.aps.peas.procedure.model.ProcedureOutput} or {@link org.tmt.aps.peas.procedure.model.ProcedureIterationOutput}.
 * Computation methods have unique result class names, which are used to populate corresponding setter methods in the current output target.
 * The current output target is set in the procedureExecutionState within the procedure executor code.
 * @author smichaels
 * @see org.tmt.aps.peas.procedure.business.ProcedureExecutionState#getCurrentOutputTarget()
 */
@Interceptor
@Priority(1)
@Computation
public class ComputationInterceptor implements Serializable {

	private static final long serialVersionUID = 5885028293442969007L;
	
	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ProcedureExecutionState procedureExecutionState;
	
	@AroundInvoke
	public Object outputStep(InvocationContext ctx) throws Exception {
		
		Object result = ctx.proceed();
		
		try {
			// log the result to the current output target
			Object outputTarget = procedureExecutionState.getCurrentOutputTarget();		
			Class<?>[] parameters = new Class[1];
			parameters[0] = result.getClass();	
			String setMethodName = "set" + result.getClass().getSimpleName();
			Method setMethod = outputTarget.getClass().getMethod(setMethodName, parameters);
			setMethod.invoke(outputTarget, result);

		} catch (Throwable th) {
			logger.error(th);
		}
		return result;
	}
	
	
	

}
