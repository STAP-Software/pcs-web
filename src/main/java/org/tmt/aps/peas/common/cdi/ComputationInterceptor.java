/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;

@Interceptor
@Computation
public class ComputationInterceptor implements Serializable {

	private static final long serialVersionUID = 5885028293442969007L;

	@EJB
	ProcedureExecutionState procedureExecutionState;
	
	@AroundInvoke
	public Object outputStep(InvocationContext ctx) throws Exception {
		
		Object result = ctx.proceed();
		
		// log the result to the current output target
		Object outputTarget = procedureExecutionState.getCurrentOutputTarget();
		String methodName = "add" + result.getClass().getSimpleName();
		Class<?>[] parameters = new Class[1];
		parameters[0] = result.getClass();
		Method method = outputTarget.getClass().getMethod(methodName, parameters);
		method.invoke(outputTarget, result);
	
		// TODO: this will eventually supercede the above "add" method
		String setMethodName = "set" + result.getClass().getSimpleName();
		Method setMethod = outputTarget.getClass().getMethod(setMethodName, parameters);
		setMethod.invoke(outputTarget, result);

		return result;
	}
	
	
	

}
