/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.ApplicationScopeBean;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extInterface.business.ExtInfFactory;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.ExtInfConnectConfig;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * JSF Controller for session user interfaces, user login, external interface connection user interface and user permissions
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class SessionController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	SessionMgmt sessionMgmt;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	CameraMgmt cameraMgmt;
	@EJB
	CcdMgmt ccdMgmt;
	@EJB
	ConstantsCache constantsCache;
	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	ProcedureExecutionState procedureExecutionState;
	@EJB
	PeasProperties peasProperties;
	@EJB
	ExtInfConfigState extInfConfigState;
	@EJB
	ExtInfFactory extInfFactory;
	@EJB
	PhysicalModel physicalModel;




	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private ApplicationScopeBean applicationScopeBean;

	Session currentSession;
	//Session currentSessionPersisted; // the session that is completed and stored
	Session session;
	Date searchDate;
	int searchQuantity = 10;
	List<Session> sessionList;
	List<String> frameList;

	boolean procedureExecuting;
	Telescope telescope;
	Instrument instrument;

	boolean advancedViewMode;
	int advancedView = Constants.ADVANCED_VIEW_ENGINEERING;
	String advancedViewModeLabel = "Engineering Mode";
	boolean extInfSimulationMode;
	
	String password;
	
	boolean runProcedurePermission;
	boolean ifCommandPermission;
	boolean configPermission;
	boolean includeTestData;
	
	boolean cameraInitialized = false;
	boolean ccdInitialized = false;

	
	/**
	 * Initialization method: creates a new current session if one does not exist
	 * Sets up external interfaces to start up either in simulation or operational mode
	 */
	@PostConstruct
	private void init() {

		try {
			
			// initialize with today
			searchDate = new Date();

			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			telescope = telescopeMgmt.findTelescope(new Long(telescopeIdStr));

			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			instrument = sessionMgmt.findInstrument(new Long(instrumentIdStr));

			sessionList = sessionMgmt.findLastSessions(telescope.getTelescopeId(), searchDate, searchQuantity);

			checkCurrentSession();

			advancedViewMode = false;
			includeTestData = false;
			
			
			// setup external interfaces either in simulation mode or production mode
			extInfSimulationMode = new Boolean(peasProperties.getProp("org.tmt.aps.peas.extinf.startup_simulation_mode"));
			
			if (!extInfSimulationMode) {
				// if not using simulators, now is the time that we connect up
				getExtInfConnectConfig().setCameraEnabled(true);
				getExtInfConnectConfig().setCcdEnabled(true);
				getExtInfConnectConfig().setAcsEnabled(true);
				getExtInfConnectConfig().setDcsEnabled(true);
				
				
				// initialize the camera once the page is loaded
				cameraInitialized = false;
				ccdInitialized = false;

			}
						
			

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	
	public void checkCurrentSession() throws Exception {
		currentSession = sessionMgmt.findCurrentSession(telescope.getTelescopeId());
		// do we get our own copy??
		//currentSessionPersisted = sessionMgmt.findCurrentSession(telescope.getTelescopeId());

		if (currentSession == null) {
			currentSession = sessionMgmt.createNewSession(instrument, telescope);
			//currentSessionPersisted = (Session) BeanUtils.cloneBean(currentSession);
			// the cloneBean will copy the procedure list, we want our own copy
			//currentSessionPersisted.setProcedureList(new ArrayList<Procedure>());
			
			// also reset and incomplete mirror settings
			GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt.findDefaultConfig(telescope.getTelescopeId(), instrument.getInstrumentId());

			List<Integer> mirrorList = new ArrayList<Integer>();
			int segmentCount = constantsCache.getTelescopeConstants().getNumberOfSegments();
			for (int i=0; i<segmentCount; i++) {
				mirrorList.add(new Integer(1));
			}
			
			globalConfigDefaults.setMirrorListEncoded(IntegerListEncoder.encodeList(mirrorList));
			
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);

			
		}

		session = currentSession;

	}

	public List<Session> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<Session> sessionList) {
		this.sessionList = sessionList;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public boolean isProcedureExecuting() {
		return procedureExecutionState.getExecutionStatus();
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public boolean isAdvancedViewMode() {
		return advancedViewMode;
	}

	public void setAdvancedViewMode(boolean advancedViewMode) {
		this.advancedViewMode = advancedViewMode;
	}
	
	public boolean isEngineeringView() {
		return advancedViewMode;
	}
	
	public boolean isAdministrationView() {
		return advancedViewMode && advancedView == Constants.ADVANCED_VIEW_ADMINISTRATION;
	}

	public String getAdvancedViewModeLabel() {
		return advancedViewModeLabel;
	}

	public void setAdvancedViewModeLabel(String advancedViewModeLabel) {
		this.advancedViewModeLabel = advancedViewModeLabel;
	}

	public boolean isExtInfSimulationMode() {
		return extInfSimulationMode;
	}

	public void setExtInfSimulationMode(boolean extInfSimulationMode) {
		this.extInfSimulationMode = extInfSimulationMode;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHelpUrl() throws Exception {
		return peasProperties.getProp("org.tmt.aps.peas.helpUrl");
	}

	public void setHelpUrl(String helpUrl) throws Exception {
		
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}


	public int getSearchQuantity() {
		return searchQuantity;
	}

	public void setSearchQuantity(int searchQuantity) {
		this.searchQuantity = searchQuantity;
	}

	public ExtInfConnectConfig getExtInfConnectConfig() {
		return extInfConfigState.getExtInfConnectConfig();
	}
	
	public boolean isNavDisabled() {
		return isRunProcedurePermission() && isProcedureExecuting();
	}

	
	public boolean isIncludeTestData() {
		return includeTestData;
	}

	public void setIncludeTestData(boolean includeTestData) {
		this.includeTestData = includeTestData;
	}



	public int procedureSortFunction(Object o1, Object o2) {
		Procedure p1 = (Procedure) o1;
		Procedure p2 = (Procedure) o2;
		return new ProcedureNumberComparator().compare(p1, p2);
	}

	
	public void updateCurrentSession() {
		try {
			currentSession = sessionMgmt.findSession(currentSession.getSessionId(), includeTestData);
		} catch (Exception e) {
			// do nothing
		}

	}
	
	/**
	 * JSF Action method to view the current night session user interface
	 * @return the JSF page rendering the session detail
	 */
	public String doViewCurrentSession() {
		
		try {
			session = sessionMgmt.findSession(currentSession.getSessionId(), includeTestData);
		} catch (Exception e) {
			session = currentSession;
		}

		// order procedures by procedure number
		Collections.sort(session.getProcedureList(), new ProcedureNumberComparator());

		breadcrumbMenuBean.addFirstItem("Current Session", "/modules/session/sessionDetail.xhtml");
		return "/modules/session/sessionDetail.xhtml?faces-redirect=true";

	}

	/**
	 * JSF Action method to view the currently selected session
	 * @return the JSF page rendering the session detail
	 */
	public String doViewSession() {

		try {
			session = sessionMgmt.findSession(session.getSessionId(), includeTestData);
	
			// order procedures by procedure number
			Collections.sort(session.getProcedureList(), new ProcedureNumberComparator());
	
			breadcrumbMenuBean.addFirstItem(
					"Session: " + session.getTelescope().getTelescopeName() + " - (" + session.getSessionDateFormatted() + ")",
					"/modules/session/sessionDetail.xhtml");
			return "/modules/session/sessionDetail.xhtml?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}
	
	public void operationalViewChangeListener() {

		try {
			session = sessionMgmt.findSession(session.getSessionId(), includeTestData);
	
			// order procedures by procedure number
			Collections.sort(session.getProcedureList(), new ProcedureNumberComparator());
	
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}
	

	/**
	 * JSF Action method that saves the current session
	 * @return the JSF page rendering the session detail
	 */
	public String doSaveSession() {

		try {
			sessionMgmt.updateSession(session);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

		try {
			currentSession = sessionMgmt.findSession(session.getSessionId(), includeTestData);
	
			breadcrumbMenuBean.addFirstItem("Session: " + session, "/modules/session/sessionDetail.xhtml");
			return "/modules/session/sessionDetail.xhtml";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}


	}

	/**
	 * JSF Action method called when the 'Cancel' button is clicked
	 * @return the JSF page rendering the session list
	 */
	public String doCancelSaveSession() {

		return doViewSessionList();	

	}

	/**
	 * JSF Action method rendering the session list
	 * @return the JSF page rendering the session list
	 */
	public String doViewSessionList() {

		try {
			sessionList = sessionMgmt.findLastSessions(telescope.getTelescopeId(), searchDate, searchQuantity);
	
			breadcrumbMenuBean.addFirstItem("Sessions", "/modules/session/sessionList.xhtml");
			return "/modules/session/sessionList.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}
	
	/**
	 * JSF Action method called when the 'Search' button is clicked
	 * @return the JSF page rendering the session list
	 */
	public String doSearchSessionList() {

		try {
			sessionList = sessionMgmt.findLastSessions(telescope.getTelescopeId(), searchDate, searchQuantity);
	
			breadcrumbMenuBean.addFirstItem("Sessions", "/modules/session/sessionList.xhtml");
			return "/modules/session/sessionList.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}
	
	/**
	 * Adds a new procedure to the current session
	 * @param procedure the procedure to add
	 */
	public void addNewProcedure(Procedure procedure) {
		currentSession.getProcedureList().add(procedure);
	}

	/**
	 * Returns the last procedure in the list of procedures for the current session
	 * @return the last procedure run in the current session
	 */
	public Procedure getCurrentSessionLastProcedure() {
		List<Procedure> pList = currentSession.getProcedureList();

		if (pList == null || pList.size() == 0) {
			return null;
		}
		
		Procedure lastProcedure = null;
		int maxProcNum = 0;
		for (Procedure procedure : pList) {
			String procNumStr = procedure.getProcedureNumber();
			if (procNumStr.indexOf(".") < 0 && (new Integer(procNumStr)) > maxProcNum) {
				lastProcedure = procedure;
				maxProcNum = new Integer(procNumStr);
			}
		}

		return lastProcedure;

	}

	/**
	 * JSF event listener called when the User mode button is clicked
	 * Pops up the login dialog
	 */
	public void modeChangeListener() {
		// here we check the mode and popup dialog at correct state change
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (advancedViewMode == true) {
			requestContext.execute("loginDialog.show()");
		} else {
			requestContext.update("procedureDetailForm");
			requestContext.update("procedureListForm");			
		}
		
	}

	/**
	 * JSF Action listener called when 'Login' button is clicked
	 * If a password was supplied, checks and if a match changes to administration mode
	 * If no password was supplied, changes to engineering mode
	 */
	public void login() {
		// here we check the password and change the mode accordingly
		RequestContext requestContext = RequestContext.getCurrentInstance();


		if (password.equals("ekinrez")) {
			advancedViewMode = true;
			advancedView = Constants.ADVANCED_VIEW_ADMINISTRATION;
			advancedViewModeLabel = "Administration Mode";
			requestContext.update("menuForm");
		} else {
			advancedViewMode = true;
			advancedView = Constants.ADVANCED_VIEW_ENGINEERING;
			advancedViewModeLabel = "Engineering Mode";
			requestContext.update("menuForm");
		}
		requestContext.update("procedureDetailForm");
		requestContext.update("procedureListForm");
	}

	/**
	 * JSF listener method called when the 'Connect...' or 'Disconnect' menu items are selected
	 * If 'Connect...' was clicked, pops up the dialog to manage the connections
	 * If 'Disconnect' was clicked, reset all connections and go into simulation mode.
	 */
	public void extInfChangeListener() {
		// here we check the mode and popup dialog at correct state change
		RequestContext requestContext = RequestContext.getCurrentInstance();
		if (extInfSimulationMode == true) {
			
			// default all the ext interface checkboxes in the dialog only
			requestContext.execute("setAllExtInfCheckboxes()");
			requestContext.execute("extInfDialog.show()");
			
		} else {
			// turn off all the ext interfaces
			getExtInfConnectConfig().reset();
			
			extInfSimulationMode = true;
			requestContext.update("extInfMode");
		}
	}

	/**
	 * JSF Action method called when external i/f connection dialog buttons 'Ok' or 'Cancel' are clicked
	 * @param ok  true if Ok was clicked otherwise false
	 */
	public void doExtInfChange(boolean ok) {
		// here we check the password and change the mode accordingly
		RequestContext requestContext = RequestContext.getCurrentInstance();

		if (ok) {
			extInfSimulationMode = false;
			requestContext.update("menuForm");
						
		} else {
			extInfSimulationMode = true;
			// turn off all the ext interfaces
			getExtInfConnectConfig().reset();
			// reset all connections
			extInfFactory.resetAll();
		}
		
		requestContext.update("extInfMode");

		
	}
	
	/**
	 * JSF Action method called when the End Session button is clicked.  Extends the Two Position mechanism and disconnects from all external I/Fs.
	 */
	public void doEndSession() {
		
		RequestContext requestContext = RequestContext.getCurrentInstance();
		
			
		try {
			Future<Integer> twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.EXTENDED);

			Utils.waitForComplete(twoPosCommandFuture);
			
			Future<Integer> stowFuture = cameraMgmt.stowCamera();
			
			Utils.waitForComplete(stowFuture);
						
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
		}
		
		
		
		// turn off all the ext interfaces
		getExtInfConnectConfig().reset();
		
		extInfSimulationMode = true;
		requestContext.update("extInfMode");

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Session Ended", ""));
	}


	public void doCheckStartSession() {
		if (extInfConfigState.getExtInfConnectConfig().isCameraEnabled()) {
			if (!cameraInitialized) {
				cameraInitialized = true;
				doInitCamera();
			}
		}
		if (extInfConfigState.getExtInfConnectConfig().isCcdEnabled()) {
			if (!ccdInitialized) {
				ccdInitialized = true;
				doInitCcd();
			}
		}
	}
	
	public void doInitCamera() {
		
		try {
			
			extInfConfigState.getExtInfConnectConfig().setCameraInitializing(true);

			// check if the camera overall status is ready
			CameraQueryResult queryResult = cameraMgmt.queryCamera(CameraCommand.DEVICE_CODE_OVERALL_STATUS);
			
			// if status is not ready, init camera
			if (queryResult.getIntValue() == CameraQueryResult.NOT_READY) {
			
				// initialize camera
				Future<Integer>  instFuture = cameraMgmt.initializeCamera();
				Utils.waitForComplete(instFuture);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Camera Initialized", ""));
			} 
			
			
		} catch (Throwable t) {
			
			Throwable next = t;
			StringBuffer buf = new StringBuffer();
			buf.append(next.getMessage());
			while (next.getCause() != null) {
				buf.append(" Caused By  " + next.getCause());
				next = next.getCause();
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Camera Initialization Failed: "  + buf, ""));
		} finally {
			//extInfConfigState.getExtInfConnectConfig().setCameraInitializing(false);			
		}
	}
	
	
	public void doInitCcd() {

		try {
			
			// command CCD to initialize
			extInfConfigState.getExtInfConnectConfig().setCcdInitializing(true);
			
			// set default temperature
			float defaultTemperature = instrument.getCcd().getDefaultTemperature();
			Future<Integer> temperatureFuture = ccdMgmt.setTemp(defaultTemperature);
			Utils.waitForComplete(temperatureFuture);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CCD Initialized", ""));
			

		} catch (Throwable t) {
			
			Throwable next = t;
			StringBuffer buf = new StringBuffer();
			buf.append(next.getMessage());
			while (next.getCause() != null) {
				buf.append(" Caused By  " + next.getCause());
				next = next.getCause();
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCD Initialization Failed: "  + buf, ""));
		} finally {
			extInfConfigState.getExtInfConnectConfig().setCcdInitializing(false);
		}
		
		
	}
	
	
	/**
	 * @return true if simulation mode should be rendered to the screen
	 */
	public boolean getRenderSimulationMode() {
		//return false;
		return extInfSimulationMode && getExtInfConnectConfig().isCameraHeartbeatStatus() && getExtInfConnectConfig().isCcdHeartbeatStatus();
	}

	/**
	 * Accessor method for procedure run permission
	 * Used by UI to show/hide elements based on this permission.
	 */
	public boolean isRunProcedurePermission() {
		return runProcedurePermission;
	}

	/**
	 * Setter method for procedure run permission
	 * This is set by the {@link ApplicationScopeBean} for the browser session that accessed PEAS-PCS first after startup
	 */
	public void setRunProcedurePermission(boolean runProcedurePermission) {
		this.runProcedurePermission = runProcedurePermission;
	}

	/**
	 * Accessor method for interface command permission
	 * Used by UI to show/hide elements based on this permission.
	 */
	public boolean isIfCommandPermission() {
		return ifCommandPermission;
	}

	/**
	 * Setter method for interface command permission
	 * This is set by the {@link ApplicationScopeBean} for the browser session that accessed PEAS-PCS first after startup
	 */
	public void setIfCommandPermission(boolean ifCommandPermission) {
		this.ifCommandPermission = ifCommandPermission;
	}

	/**
	 * Accessor method for configuration permission
	 * Used by UI to show/hide elements based on this permission.
	 */
	public boolean isConfigPermission() {
		return configPermission;
	}

	/**
	 * Setter method for configuration permission
	 * This is set by the {@link ApplicationScopeBean} for the browser session that accessed PEAS-PCS first after startup
	 */
	public void setConfigPermission(boolean configPermission) {
		this.configPermission = configPermission;
	}

	public String doError() {

		return "/error.xhtml?faces-redirect=true";
		
	}

	

}