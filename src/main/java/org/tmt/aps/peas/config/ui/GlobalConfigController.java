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

	GlobalConfig globalConfig;
	Long telescopeId;
	Long instrumentId;

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	@PostConstruct
	public void init() {

		try {
			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			telescopeId = new Long(telescopeIdStr);
			instrumentId = new Long(instrumentIdStr);

			globalConfig = globalConfigMgmt.findDefaultConfig(telescopeId, instrumentId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doCancelSaveSetup() {

	}

	public void doSaveSetup() {

		try {
			globalConfigMgmt.saveDefaultConfig(globalConfig);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Record Saved Successfully"));
			logger.info("doSave: success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error saving record"));
		}
	}

	public String doViewGlobalConfig() {
		breadcrumbMenuBean.addFirstItem("Global Configuration", "doViewGlobalConfig()");

		return "/modules/config/globalConfig.xhtml?faces-redirect=true";

	}

}
