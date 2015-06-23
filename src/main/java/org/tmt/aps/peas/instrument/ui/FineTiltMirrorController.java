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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;

@Named
@SessionScoped
public class FineTiltMirrorController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	@EJB
	private PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private FineTiltMirror fineTiltMirror;

	@PostConstruct
	private void init() {

		fineTiltMirror = physicalModel.getInstrument().getCamera().getFineTiltMirror();
	}

	public FineTiltMirror getFineTiltMirror() {
		return fineTiltMirror;
	}

	public void setFineTiltMirror(FineTiltMirror fineTiltMirror) {
		this.fineTiltMirror = fineTiltMirror;
	}

	public String doViewFineTiltMirror() {
		
		try {
			if (fineTiltMirror == null) {
				throw new Exception("Fine Tilt Mirror not found in physical model");
			}
			breadcrumbMenuBean.addFirstItem("Fine Tilt Mirror", "/modules/sysadmin/fineTiltMirrorDetail.xhtml");

			return "/modules/sysadmin/fineTiltMirrorDetail.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	public void doSaveFineTiltMirror() {

		try {
			cameraDefMgmt.updateFineTiltMirror(fineTiltMirror);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	public String doCancelSaveFineTiltMirror() {

		breadcrumbMenuBean.addFirstItem("Fine Tilt Mirror", "/modules/sysadmin/fineTiltMirrorDetail.xhtml");

		return "/modules/sysadmin/fineTiltMirrorDetail.xhtml?faces-redirect=true";

	}

}
