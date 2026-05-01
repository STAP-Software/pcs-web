/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

/**
 * JSF Controller for reference beam user interface
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class RefBeamController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	@EJB
	PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<ReferenceBeam> referenceBeamList;
	private ReferenceBeam referenceBeam;

	@PostConstruct
	private void init() {

		refreshReferenceBeamList();

	}

	public List<ReferenceBeam> getReferenceBeamList() {
		return referenceBeamList;
	}

	public void setReferenceBeamList(List<ReferenceBeam> referenceBeamList) {
		this.referenceBeamList = referenceBeamList;
	}

	public ReferenceBeam getReferenceBeam() {
		return referenceBeam;
	}

	public void setReferenceBeam(ReferenceBeam referenceBeam) {
		this.referenceBeam = referenceBeam;
	}

	private void refreshReferenceBeamList() {

		try {
			referenceBeamList = new ArrayList<ReferenceBeam>(physicalModel.getInstrument().getCamera().getReferenceBeamSet());

			Collections.sort(referenceBeamList, new BeanComparator("refBeamNum"));

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	/**
	 * Validates the form.  Does not allow duplicate reference beam numbers to be created.
	 * @param event
	 */
	public void validate(ComponentSystemEvent event) {

		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		UIInput referenceBeamIdInput = (UIInput) components.findComponent("referenceBeamId");
		if (referenceBeamIdInput.getLocalValue() == null) {

			UIInput refBeamNumInput = (UIInput) components.findComponent("refBeamNum");
			String refBeamNumStr = refBeamNumInput.getLocalValue() == null ? "" : refBeamNumInput.getLocalValue().toString();
			String refBeamNumId = refBeamNumInput.getClientId();

			try {
				int refBeamNum = Integer.valueOf(refBeamNumStr);
	
				for (ReferenceBeam referenceBeam : referenceBeamList) {
	
					if (referenceBeam.getRefBeamNum() == refBeamNum) {
	
						FacesMessage msg = new FacesMessage("Reference Beam Number " + refBeamNum + " is already defined.");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						fc.addMessage(refBeamNumId, msg);
						fc.renderResponse();
	
					}
	
				}
			} catch (Exception e) {
				FacesMessage msg = new FacesMessage("Reference Beam Number " + refBeamNumStr + " is not valid.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				fc.addMessage(refBeamNumId, msg);
				fc.renderResponse();
				
			}
		}

	}

	/**
	 * JSF Action method to view the reference beam list 
	 * @return the JSF page to render the reference beam list
	 */
	public String doViewReferenceBeamList() {

		try {
			refreshReferenceBeamList();

			breadcrumbMenuBean.addFirstItem("Reference Beams", "/modules/sysadmin/refBeamList.xhtml");

			return "/modules/sysadmin/refBeamList.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	/**
	 * JSF Action method to view a single reference beam configuration
	 * @return the JSF page to render a reference beam configuration detail
	 */
	public String doViewReferenceBeam() {

		breadcrumbMenuBean.addItem("" + referenceBeam.getRefBeamNum(), "/modules/sysadmin/refBeamDetail.xhtml");

		return "/modules/sysadmin/refBeamDetail.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to setup for a new reference beam to be created
	 * @return the JSF page to render a reference beam configuration detail
	 */
	public String doNewReferenceBeam() {

		referenceBeam = new ReferenceBeam();

		referenceBeam.setCamera(physicalModel.getInstrument().getCamera());

		breadcrumbMenuBean.addItem("New Reference Beam", "/modules/sysadmin/refBeamDetail.xhtml");

		return "/modules/sysadmin/refBeamDetail.xhtml?faces-redirect=true";

	}

	/**
	 * Creates or updates a reference beam configuration record
	 */
	public void doSaveReferenceBeam() {

		try {

			if (referenceBeam.isNewRecord()) {
				cameraDefMgmt.createReferenceBeam(referenceBeam);

			} else {
				cameraDefMgmt.updateReferenceBeam(referenceBeam);

			}
			refreshReferenceBeamList();

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);

		}

	}

	/**
	 * JSF Action method called when the 'Cancel' button is clicked
	 * @return the JSF page rendering the reference beam list
	 */
	public String doCancelSaveReferenceBeam() {

		breadcrumbMenuBean.addFirstItem("Reference Beams", "/modules/sysadmin/refBeamList.xhtml");

		return "/modules/sysadmin/refBeamList.xhtml?faces-redirect=true";

	}

}
