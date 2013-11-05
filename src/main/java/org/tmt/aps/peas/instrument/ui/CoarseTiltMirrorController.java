/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.business.ActiveInstrument;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;

@Named
@SessionScoped
public class CoarseTiltMirrorController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	@EJB
	private ActiveInstrument activeInstrument;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private CoarseTiltMirror coarseTiltMirror;

	@PostConstruct
	private void init() {
			
		coarseTiltMirror = activeInstrument.getInstrument().getCamera().getCoarseTiltMirror();
	}


	public CoarseTiltMirror getCoarseTiltMirror() {
		return coarseTiltMirror;
	}


	public void setCoarseTiltMirror(CoarseTiltMirror coarseTiltMirror) {
		this.coarseTiltMirror = coarseTiltMirror;
	}


	public String doViewCoarseTiltMirror() {

		System.out.println("doViewCoarseTiltMirror");
		
		breadcrumbMenuBean.addItem("Coarse Tilt Mirror", "doViewFilter()");

		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";
	}


	public String doSaveCoarseTiltMirror() {

		cameraDefMgmt.updateCoarseTiltMirror(coarseTiltMirror);
		
		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";
	}

	public String doCancelSaveCoarseTiltMirror() {

		breadcrumbMenuBean.addFirstItem("Coarse Tilt Mirror", "doViewCoarseTiltMirror()");

		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";

	}


}
