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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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

@ManagedBean
@ApplicationScoped
public class ApplicationScopeBean {

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
	
	public void preRenderView(ComponentSystemEvent e) {

		Logger logger = Logger.getLogger(this.getClass());

		// workaround for "java.lang.IllegalStateException: Cannot create a session after the response has been committed" problem with JSF
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		HttpSession session = null;

		if (getPersistentSession() == null) {
			session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			setPersistentSession(session);
		} else {
			// set the JSESSIONID cookie to that of the persistent session
			session = getPersistentSession();
			addCookie(response, "JSESSIONID", session.getId(), 1800);
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

		System.out.println("URL = >>>>>>>>>>>>>>>>>>>>>  " + path);
		System.out.println("immediateURL = >>>>>>>>>>>>>>>>>>>>>  " + breadcrumbMenuBean.getImmediateUrl());

		if (path.equals(breadcrumbMenuBean.getImmediateUrl()) && path.equals("/modules/session/sessionList.xhtml")) {
			// the same URL as the last action performed, we assume this is a result of a JSF action
			return;
		}

		
		// We only handle 'top level' navigation points, which are faces-redirect=true

		if (path.contains("/modules/session/sessionList.")) {
			sessionController.doViewSessionList();
		} else if (path.equals("/modules/session/sessionDetail.xhtml")) {
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
		} else if (path.equals("/modules/procedure/procedurePerspective.")) {
			procedureController.doViewProcedure();
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

	public HttpSession getPersistentSession() {
		return persistentSession;
	}

	public void setPersistentSession(HttpSession persistentSession) {
		this.persistentSession = persistentSession;
	}
	
	/**
	 * 
	 * @author BalusC
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
	 * 
	 * @author BalusC
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
	    Cookie cookie = new Cookie(name, value);
	    cookie.setPath("/");
	    cookie.setMaxAge(maxAge);
	    response.addCookie(cookie);
	}

	/**
	 * 
	 * @author BalusC
	 */
	public static void removeCookie(HttpServletResponse response, String name) {
	    addCookie(response, name, null, 0);
	}
	
}
