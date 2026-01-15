/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Transient;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;

/**
 * JSF Controller for global configuration user interface and zernike order user interface
 * @author smichaels
 *
 */
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

	Integer[] sufsZernikeOrderObjectArray;
	

	public Integer[] getSufsZernikeOrderObjectArray() {
		return sufsZernikeOrderObjectArray;
	}
	
	public void setSufsZernikeOrderObjectArray(Integer[] inputArray) {
		sufsZernikeOrderObjectArray = inputArray;
	}
	
	
	public GlobalConfig getGlobalConfigDefaults() {
		return globalConfigDefaults;
	}

	public void setGlobalConfigDefaults(GlobalConfigDefaults globalConfigDefaults) {
		this.globalConfigDefaults = globalConfigDefaults;
	}

	@PostConstruct
	public void init() {

	}

	/**
	 * Action method called when 'cancel' button is clicked on global configuration page
	 */
	public void doCancelSaveSetup() throws Exception {
		
	}

	/**
	 * Action method to save the global configuration to the database
	 */
	public void doSaveSetup() {

		try {
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}
	}

	/**
	 * Action method to view the current global configuration values
	 * @return JSF page to render
	 */
	public String doViewGlobalConfig() {
		
		
		try {
			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			telescopeId = new Long(telescopeIdStr);
			instrumentId = new Long(instrumentIdStr);

			globalConfigDefaults = globalConfigMgmt.findDefaultConfig(telescopeId, instrumentId);
			
			breadcrumbMenuBean.addFirstItem("Global Configuration", "/modules/config/globalConfig.xhtml");

			return "/modules/config/globalConfig.xhtml?faces-redirect=true";


		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		

	}
	
	
	/**
	 * Action method to view the current SUFS Zernike order calculation configuration
	 * @return JSF page to render
	 */
	public String doViewSufsZernikes() {
		
		try {

			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			telescopeId = new Long(telescopeIdStr);
			instrumentId = new Long(instrumentIdStr);

			globalConfigDefaults = globalConfigMgmt.findDefaultConfig(telescopeId, instrumentId);	
			
			
			sufsZernikeOrderObjectArray = IntegerListEncoder.decodeListToObjectArray(globalConfigDefaults.getSufsZernikeOrderListEncoded());
			
	
			breadcrumbMenuBean.addFirstItem("Sufs Segment Zernikes ", "/modules/sysadmin/sufsZernike.xhtml");

		return "/modules/sysadmin/sufsZernike.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	/**
	 * Action method saving SUFS Zernike orders to calculate.
	 */
	public void doSaveSufsZernikes() {

		try {

			String sufsZernikeOrderListEncoded = IntegerListEncoder.encodeList(sufsZernikeOrderObjectArray);

			globalConfigDefaults.setSufsZernikeOrderListEncoded(sufsZernikeOrderListEncoded);
			
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	/**
	 * Action method called when user clicks 'cancel' on SUFS Zernike order page
	 */
	public void doCancelSaveSufsZernikes() {


	}


}
