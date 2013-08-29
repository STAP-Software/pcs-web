/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;

@Named
@SessionScoped
public class AcsManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;



	

	@PostConstruct
	public void init() {


	}


	public String doViewDcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("DCS Manual Interface", "doViewDcsManualInterface()");

		return "/modules/diagnostic/dcsManualInterface.xhtml?faces-redirect=true";

	}

}
