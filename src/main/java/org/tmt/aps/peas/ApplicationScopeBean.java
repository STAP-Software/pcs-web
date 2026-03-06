/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.config.ui.GlobalConfigController;
import org.tmt.aps.peas.config.ui.MissingSpotsController;
import org.tmt.aps.peas.extInterface.ui.AcsManualController;
import org.tmt.aps.peas.extInterface.ui.CameraManualController;
import org.tmt.aps.peas.extInterface.ui.CcdManualController;
import org.tmt.aps.peas.extInterface.ui.DcsManualController;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.ui.CcdDefController;
import org.tmt.aps.peas.instrument.ui.CoarseTiltMirrorController;
import org.tmt.aps.peas.instrument.ui.FilterController;
import org.tmt.aps.peas.instrument.ui.FineTiltMirrorController;
import org.tmt.aps.peas.instrument.ui.PupilMaskController;
import org.tmt.aps.peas.instrument.ui.RefBeamController;
import org.tmt.aps.peas.instrument.ui.SufsGroupController;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.session.ui.SessionController;

/**
 * JSF Application scoped bean; used to allow limited page bookmarking and managing user sessions.
 * @author smichaels
 *
 */
@Named
@ApplicationScoped
public class ApplicationScopeBean implements Serializable {

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private SessionController sessionController;
	@Inject
	private SufsGroupController sufsGroupController;
	@Inject
	private RefBeamController refBeamController;
	@Inject
	private MissingSpotsController missingSpotsController;
	@Inject
	private ProcedureController procedureController;
	@Inject
	private AcsManualController acsManualController;
	@Inject
	private CcdManualController ccdManualController;
	@Inject
	private CameraManualController cameraManualController;
	@Inject
	private DcsManualController dcsManualController;
	@Inject
	private GlobalConfigController globalConfigController;
	@Inject
	private FrameController frameController;
	@Inject
	private CcdDefController ccdDefController;
	@Inject
	private CoarseTiltMirrorController coarseTiltMirrorController;
	@Inject
	private FineTiltMirrorController fineTiltMirrorController;
	@Inject
	private PupilMaskController pupilMaskController;
	@Inject
	private FilterController filterController;

	HttpSession persistentSession = null;
	String ownerRequestedSessionId = null;
	
	/**
	 * Called by all JSF requests prior to the RenderView phase
	 * Manages the persistent session: the first browser user to make a request after a reboot will get the persistent session.
	 * Killing the browser session will not kill the persistent session, which will be reinstated upon the next request
	 * The persistent session will obtain the RunProcedurePermission and IfCommandPermission, to run procedures and command interfaces.
	 * Any subsequent browser request not from that user (different IP or Linux account) will have a standard browser session without
	 * those permissions.
	 */
	public void preRenderView(ComponentSystemEvent e) {

		Logger logger = Logger.getLogger(this.getClass());

		// workaround for "java.lang.IllegalStateException: Cannot create a session after the response has been committed" problem with JSF
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpSession session = null;

		// root context cannot get the persistent session
		if (request.getRequestURI().contains("pcs-web")) {
			// set up persistent session if this is first access since reboot (ownerRequestedSessionId == null) or this is the owner asking
			if (ownerRequestedSessionId == null || ownerRequestedSessionId.equals(request.getRequestedSessionId())) {
				
				if (getPersistentSession() == null || !request.isRequestedSessionIdValid()) {
					
					logger.info("invalid or null persistent session = " + getPersistentSession());
					session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
					// set run procedure permission on session controller for this session (SessionController is session scoped)
					sessionController.setRunProcedurePermission(true);
					// set I/F command permission on session controller for this session (SessionController is session scoped)
					sessionController.setIfCommandPermission(true);
					// set configuration permissing on session controller for this session (SessionController is session scoped)
					sessionController.setConfigPermission(true);
					setPersistentSession(session);
					ownerRequestedSessionId = session.getId();
					
				} else {
					// set the JSESSIONID cookie to that of the persistent session
					session = getPersistentSession();
					addCookie(response, "JSESSIONID", session.getId(), 1800);			
				}
			} else {
				// bounce them out
				try {
					ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				    ec.redirect(ec.getRequestContextPath() + "/error.html");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} 
		
		
		
		
		// tune session params, eg. session.setMaxInactiveInterval(..);

		// perform other pre-render stuff, like setting user context...

		
		
		
		
		if (request.getMethod().equals("POST")) {
			return;
		}

		// navigation from direct URLs is handled here, breadcrumbs and selected navigation module must be updated to reflect where you are.

		String path = request.getServletPath();
		// remove the ops/partner/client prefix
		if (path.indexOf("/") < 0) {
			return;
		}

		String facesRedirect = request.getParameter("faces-redirect");
		String test = request.getParameter("test");
		String fromBreadcrumb = request.getParameter("from-breadcrumb");
		
		logger.debug("URL = >>>>>>>>>>>>>>>>>>>>>  " + path);
		logger.debug("immediateURL = >>>>>>>>>>>>>>>>>>>>>  " + breadcrumbMenuBean.getImmediateUrl());

		if (path.equals(breadcrumbMenuBean.getImmediateUrl()) && path.equals("/modules/session/sessionList.xhtml")) {
			// the same URL as the last action performed, we assume this is a result of a JSF action
			return;
		}

		
		// We only handle 'top level' navigation points, which are faces-redirect=true

		if (path.contains("/modules/session/sessionList.")) {
			sessionController.doViewSessionList();
		} else if (path.contains("/modules/session/sessionDetail.") && fromBreadcrumb != null) {
			sessionController.doViewSession();
		} else if (path.equals("/modules/sysadmin/sufsGroupList.xhtml")) {
			sufsGroupController.doViewSufsGroupList();
		} else if (path.equals("/modules/sysadmin/sufsGroupDetail.xhtml")) {
			sufsGroupController.doViewSufsGroup();
		} else if (path.equals("/modules/sysadmin/refBeamList.xhtml")) {
			refBeamController.doViewReferenceBeamList();
		} else if (path.equals("/modules/sysadmin/refBeamDetail.xhtml")) {
			refBeamController.doViewReferenceBeam();
		} else if (path.equals("/modules/config/missingSpots.xhtml")) {
			missingSpotsController.doViewMissingSpots();
		} else if (path.contains("/modules/procedure/procedurePerspective.") && facesRedirect != null) {
			
			if (test != null) {
				procedureController.doViewNextArchivedProcedure();
			} else {
				breadcrumbMenuBean.removeTo("Procedure #");
				//procedureController.doViewArchivedProcedure();
			}
		} else if (path.equals("/modules/diagnostic/acsManualInterface.xhtml")) {
			acsManualController.doViewAcsManualInterface();
		} else if (path.equals("/modules/diagnostic/ccdDiagnostic.xhtml")) {
			ccdManualController.doViewCcdDiagnostic();
		} else if (path.equals("/modules/diagnostic/cameraDiagnostic.xhtml")) {
			cameraManualController.doViewCameraDiagnostic();
		} else if (path.equals("/modules/diagnostic/dcsManualInterface.xhtml")) {
			dcsManualController.doViewDcsManualInterface();
		} else if (path.equals("/modules/config/globalConfig.xhtml")) {
			globalConfigController.doViewGlobalConfig();
		} else if (path.equals("/modules/frameViewer/frameViewer.xhtml")) {
			frameController.doSetupFrameViewer();
		} else if (path.equals("/modules/sysadmin/ccdList.xhtml")) {
			ccdDefController.doViewCcdList();
		} else if (path.equals("/modules/sysadmin/ccdDetail.xhtml")) {
			ccdDefController.doViewCcd();
		} else if (path.equals("/modules/sysadmin/ccdSelectList.xhtml")) {
			ccdDefController.doViewCcdSelectList();
		} else if (path.equals("/modules/sysadmin/coarseTiltMirrorDetail.xhtml")) {
			coarseTiltMirrorController.doViewCoarseTiltMirror();
		} else if (path.equals("/modules/sysadmin/fineTiltMirrorDetail.xhtml")) {
			fineTiltMirrorController.doViewFineTiltMirror();
		} else if (path.equals("/modules/sysadmin/pupilMaskList.xhtml")) {
			pupilMaskController.doViewPupilMaskList();
		} else if (path.equals("/modules/sysadmin/pupilMaskDetail.xhtml")) {
			pupilMaskController.doViewPupilMask();
		} else if (path.equals("/modules/sysadmin/pupilWheel.xhtml")) {
			pupilMaskController.doViewPupilWheel();
		} else if (path.equals("/modules/sysadmin/filterList.xhtml")) {
			filterController.doViewFilterList();
		} else if (path.equals("/modules/sysadmin/filterDetail.xhtml")) {
			filterController.doViewFilter();
		} else if (path.equals("/modules/sysadmin/filterWheel.xhtml")) {
			filterController.doViewFilterWheel();
		}
		
	}
	/**
	 * @return the persistent session object
	 */
	public HttpSession getPersistentSession() {
		return persistentSession;
	}

	/**
	 * Sets the persistent session object
	 */
	public void setPersistentSession(HttpSession persistentSession) {
		this.persistentSession = persistentSession;
	}
	
	/**
	 * Returns a cookie value from the request, given its name
	 * @param request the request
	 * @param name the cookie name
	 * @return the cookie value
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (name.equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}

	/**
	 * Adds a cookie to the user
	 * @param response the HTTP response
	 * @param name the new cookie name
	 * @param value the new cookie value
	 * @param maxAge expiration age for the cookie
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
	    Cookie cookie = new Cookie(name, value);
	    cookie.setPath("/");
	    cookie.setMaxAge(maxAge);
	    response.addCookie(cookie);
	}

	/**
	 * Removes a cookie for the user of this response
	 * @param response the HTTP response
	 * @param name the name of the cookie to remove
	 */
	public static void removeCookie(HttpServletResponse response, String name) {
	    addCookie(response, name, null, 0);
	}
	
}
