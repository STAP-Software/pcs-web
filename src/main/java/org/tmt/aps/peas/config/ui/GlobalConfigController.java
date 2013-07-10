package org.tmt.aps.peas.config.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;

@Named
@SessionScoped
public class GlobalConfigController implements Serializable {

	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	PeasProperties peasProperties;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	GlobalConfig globalConfig;

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

		//ProcedureConfig procedureConfig = new ProcedureConfig();
		globalConfig = globalConfigMgmt.findDefaultConfig(new Long(telescopeIdStr), new Long(instrumentIdStr));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void doCancelSaveSetup() {

	}

}
