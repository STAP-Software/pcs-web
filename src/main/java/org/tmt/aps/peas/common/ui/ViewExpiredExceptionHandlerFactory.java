/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common.ui;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

/**
 * Registers {@link ViewExpiredExceptionHandler} into the JSF exception-handling chain.
 * Wired up via the &lt;factory&gt;&lt;exception-handler-factory&gt; entry in
 * faces-config.xml - JSF instantiates this with the previous factory in the chain
 * (normally the container's default) as the single-arg constructor parameter.
 */
public class ViewExpiredExceptionHandlerFactory extends ExceptionHandlerFactory {



	public ViewExpiredExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		super(parent);
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new ViewExpiredExceptionHandler(getWrapped().getExceptionHandler());
	}
}
