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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
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
	private PhysicalModel physicalModel;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private CoarseTiltMirror coarseTiltMirror;

	@PostConstruct
	private void init() {
			
		coarseTiltMirror = physicalModel.getInstrument().getCamera().getCoarseTiltMirror();
	}


	public CoarseTiltMirror getCoarseTiltMirror() {
		return coarseTiltMirror;
	}


	public void setCoarseTiltMirror(CoarseTiltMirror coarseTiltMirror) {
		this.coarseTiltMirror = coarseTiltMirror;
	}


	public String doViewCoarseTiltMirror() {

		breadcrumbMenuBean.addFirstItem("Coarse Tilt Mirror", "doViewCoarseTiltMirror()");

		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";
	}


	public String doSaveCoarseTiltMirror() {

		try {
		
		cameraDefMgmt.updateCoarseTiltMirror(coarseTiltMirror);
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating Coarse Tilt Mirror Configuration", ""));
			return null;
		}
		FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated Coarse Tilt Mirror Configuration", ""));
		
		return null;
	}

	public String doCancelSaveCoarseTiltMirror() {

		breadcrumbMenuBean.addFirstItem("Coarse Tilt Mirror", "doViewCoarseTiltMirror()");

		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";

	}


}
