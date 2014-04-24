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


	float actDeltas[][] = new float[36][3];

	

	@PostConstruct
	public void init() {

		for (int i=0; i<36; i++) {
			for (int j=0; j<3; j++) {
				actDeltas[i][j] = (i+1)*10 + j + 1;
			}
		}

	}


	public float[][] getActDeltas() {
		return actDeltas;
	}


	public void setActDeltas(float[][] actDeltas) {
		this.actDeltas = actDeltas;
	}


	public String doViewAcsManualInterface() {
		
		init();
		

		breadcrumbMenuBean.addFirstItem("ACS Manual Interface", "doViewAcsManualInterface()");

		return "/modules/diagnostic/acsManualInterface.xhtml?faces-redirect=true";

	}

}
