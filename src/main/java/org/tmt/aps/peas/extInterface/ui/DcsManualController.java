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
import org.tmt.aps.peas.extinf.StarInfo;

@Named
@SessionScoped
public class DcsManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	int dcsStatus = 2;
	StarInfo star = new StarInfo("SomeName", 6.3f, "blue");
	float[] telescopePosition = new float[2];
	

	@PostConstruct
	public void init() {
		telescopePosition[0] = 0.345f;
		telescopePosition[1] = 4.111f;
	}


	public int getDcsStatus() {
		return dcsStatus;
	}

	public void setDcsStatus(int dcsStatus) {
		this.dcsStatus = dcsStatus;
	}

	public StarInfo getStar() {
		return star;
	}

	public void setStar(StarInfo star) {
		this.star = star;
	}

	public float[] getTelescopePosition() {
		return telescopePosition;
	}

	public void setTelescopePosition(float[] telescopePosition) {
		this.telescopePosition = telescopePosition;
	}


	public String doViewDcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("DCS Manual Interface", "doViewDcsManualInterface()");

		return "/modules/diagnostic/dcsManualInterface.xhtml?faces-redirect=true";

	}
	
	public void doQueryTelescopePosition() {


	}

	public void doQueryStar() {


	}

	public void doQueryDcsStatus() {


	}

	public void doQueryAll() {

	}
}


