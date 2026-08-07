/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013-2026 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.annotation.Priority;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.computation.business.ComputationException;


/**
 * This interceptor is not used.
 * @author smichaels
 * @Deprecated
 */
@Interceptor
@Priority(1)
@ProcedureExceptionManageable
public class ProcedureExceptionInterceptor implements Serializable {

	private static final long serialVersionUID = 5885028293442969007L;



	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {
		Logger logger = Logger.getLogger(ctx.getTarget().getClass().getName());
		
		Object returnMe = null;
		try {
			returnMe = ctx.proceed();

		} catch (ComputationException e) {
			logger.error("", e);
			throw e;
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
		return returnMe;

	}
	
}
