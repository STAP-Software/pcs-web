/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.session.ui.SessionController;

@ManagedBean
@ApplicationScoped
public class ApplicationScopeBean {

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private SessionController sessionController;

	public void preRenderView(ComponentSystemEvent e) {

		Logger logger = Logger.getLogger(this.getClass());

		// workaround for "java.lang.IllegalStateException: Cannot create a session after the response has been committed" problem with JSF
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

		// tune session params, eg. session.setMaxInactiveInterval(..);

		// perform other pre-render stuff, like setting user context...

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		if (request.getMethod().equals("POST")) {
			return;
		}

		// navigation from direct URLs is handled here, breadcrumbs and selected navigation module must be updated to reflect where you are.

		String path = request.getServletPath();
		// remove the ops/partner/client prefix
		if (path.indexOf("/") < 0) {
			return;
		}

		System.out.println("URL = >>>>>>>>>>>>>>>>>>>>>  " + path);
		System.out.println("immediateURL = >>>>>>>>>>>>>>>>>>>>>  " + breadcrumbMenuBean.getImmediateUrl());

		if (path.equals(breadcrumbMenuBean.getImmediateUrl())) {
			// the same URL as the last action performed, we assume this is a result of a JSF action
			return;
		}

		// We only handle 'top level' navigation points, which are faces-redirect=true

		if (path.equals("/modules/session/sessionList.xhtml")) {
			sessionController.doViewSessionList();
		} else if (path.equals("/modules/session/sessionDetail.xhtml")) {
			sessionController.doViewSession();
		}

	}
}
