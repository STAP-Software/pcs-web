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

import org.apache.log4j.Logger;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;

@Interceptor
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
