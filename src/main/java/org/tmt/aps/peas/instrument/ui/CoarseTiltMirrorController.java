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
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;

/**
 * JSF Controller for coarse tilt mirror configuration user interface
 * @author smichaels
 *
 */
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

	/**
	 * JSF Action method to view the coarse tilt mirror configuration
	 * @return the JSF page to render the coarse tilt mirror detail
	 */
	public String doViewCoarseTiltMirror() {

		try {
			if (coarseTiltMirror == null) {
				throw new Exception("Coarse Tilt mirror not found in physical model");
			}

			breadcrumbMenuBean.addFirstItem("Coarse Tilt Mirror", "/modules/sysadmin/coarseTiltMirrorDetail.xhtml");

			return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	/**
	 * JSF Action method to save the coarse tilt mirror configuration
	 */
	public void doSaveCoarseTiltMirror() {

		try {

			cameraDefMgmt.updateCoarseTiltMirror(coarseTiltMirror);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	public String doCancelSaveCoarseTiltMirror() {

		breadcrumbMenuBean.addFirstItem("Coarse Tilt Mirror", "/modules/sysadmin/coarseTiltMirrorDetail.xhtml");

		return "/modules/sysadmin/coarseTiltMirrorDetail.xhtml?faces-redirect=true";

	}

}
