/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;

@Named
@SessionScoped
public class GlobalConfigController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	GlobalConfigDefaults globalConfigDefaults;
	Long telescopeId;
	Long instrumentId;

	public GlobalConfig getGlobalConfigDefaults() {
		return globalConfigDefaults;
	}

	public void setGlobalConfigDefaults(GlobalConfigDefaults globalConfigDefaults) {
		this.globalConfigDefaults = globalConfigDefaults;
	}

	@PostConstruct
	public void init() {

		try {
			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			telescopeId = new Long(telescopeIdStr);
			instrumentId = new Long(instrumentIdStr);

			globalConfigDefaults = globalConfigMgmt.findDefaultConfig(telescopeId, instrumentId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doCancelSaveSetup() {

	}

	public void doSaveSetup() {

		try {
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Saved Successfully"));
			logger.info("doSave: success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error saving record"));
		}
	}

	public String doViewGlobalConfig() {
		breadcrumbMenuBean.addFirstItem("Global Configuration", "/modules/config/globalConfig.xhtml");

		return "/modules/config/globalConfig.xhtml?faces-redirect=true";

	}

}
