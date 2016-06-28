/**
 * @author Scott Michaels
 * @version 1.0
 * Copyright IBM Corporation 2013 - All Rights Reserved.
 */
package org.tmt.aps.peas.common.cdi;

import java.io.Serializable;
import java.util.Arrays;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;
/**
 * Logging interceptor.  Every method with the <code>@Loggable</code> annotation will participate in trace level logging of
 * method name, input and output values.  This interceptor is currently not being called as no method is currently so annotated.
 * Trace logging using this interceptor can be set up by simply annotating a method with the <code>@Loggable</code> annotation.
 * @author smichaels
 *
 */
@Interceptor
@Loggable
public class LoggingInterceptor implements Serializable {

	private static final long serialVersionUID = 5885028293442969007L;

	

	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {
		Logger logger = Logger.getLogger(ctx.getTarget().getClass().getName());
		logger.trace("calling " + ctx.getMethod().getName() + " with args " + Arrays.toString(ctx.getParameters()));
		Object returnMe = ctx.proceed();
		logger.trace(ctx.getMethod().getName() + " returned " + returnMe);

		return returnMe;

	}
	
	
	

}
