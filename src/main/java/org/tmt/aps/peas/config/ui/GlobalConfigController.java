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
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
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

	Integer[] sufsZernikeOrderObjectArray;
	
	// convenience methods for encoding and decoding

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

	public void doCancelSaveSetup() throws Exception {
		
	}

	public void doSaveSetup() {

		try {
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}
	}

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

	public void doCancelSaveSufsZernikes() {


	}


}
