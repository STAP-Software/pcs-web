/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common.ui;

import java.io.IOException;
import java.util.Iterator;

import jakarta.faces.FacesException;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;

import org.jboss.logging.Logger;

/**
 * Handles {@link ViewExpiredException} gracefully, whether it's raised from a normal
 * page request or a background ajax request (most notably the global p:poll in
 * template.xhtml, which fires every 2 seconds on every page). 
 * 
 * Without this handler, a stale browser tab whose server-side view no longer exists
 * (because the server restarted, or the view genuinely expired after long inactivity)
 * generates a ViewExpiredException on every single ajax poll tick, forever, since
 * nothing tells the poller to stop or reacts to the failure - this is purely log noise,
 * since a background poll can't meaningfully act on the default web.xml error-page
 * redirect (that redirect returns a full HTML page, which is not a valid ajax
 * partial-response, so PrimeFaces' client-side ajax handler can't do anything useful
 * with it).
 * 
 * Instead, this handler redirects the browser straight back to the application's entry
 * point (the session list) in both cases. For an ajax request, calling
 * ExternalContext.redirect() during response rendering is picked up by JSF's
 * PartialViewContext and automatically wrapped as a
 * &lt;partial-response&gt;&lt;redirect .../&gt;&lt;/partial-response&gt; payload, which
 * PrimeFaces' client-side ajax handler knows how to act on - so the poller (or any other
 * ajax interaction) actually navigates the tab away, rather than silently failing and
 * retrying every 2 seconds.
 * 
 * Registered via a matching ExceptionHandlerFactory in faces-config.xml.
 */
public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger logger = Logger.getLogger(ViewExpiredExceptionHandler.class);

	// application entry point - same destination index.html's meta-refresh sends
	// unauthenticated/bookmarked root requests to
	private static final String ENTRY_POINT_URL = "/modules/session/sessionList.jsf";

	private final ExceptionHandler wrapped;

	public ViewExpiredExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {

		Iterator<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents().iterator();

		while (events.hasNext()) {

			ExceptionQueuedEvent event = events.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			// the real exception may be wrapped (e.g. inside a FacesException), so walk
			// the cause chain rather than only checking the top-level exception
			Throwable t = context.getException();
			while (t != null && !(t instanceof ViewExpiredException)) {
				t = t.getCause();
			}

			if (t instanceof ViewExpiredException) {

				logger.info("View expired (server restart or long inactivity) - redirecting stale view to " + ENTRY_POINT_URL);

				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();

				try {
					externalContext.redirect(externalContext.getRequestContextPath() + ENTRY_POINT_URL);
				} catch (IOException e) {
					logger.error("Failed to redirect after ViewExpiredException", e);
				}

				facesContext.responseComplete();

				// mark handled so it isn't logged again or rethrown as unhandled
				events.remove();
			}
		}

		// let the wrapped handler deal with everything else exactly as it always has
		getWrapped().handle();
	}
}
