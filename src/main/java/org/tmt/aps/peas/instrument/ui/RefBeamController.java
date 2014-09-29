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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

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
			logger.error("", e);
		}
	}

	
	public void validate(ComponentSystemEvent event) {
		 
		  FacesContext fc = FacesContext.getCurrentInstance();
	 
		  UIComponent components = event.getComponent();
	 
		  UIInput referenceBeamIdInput = (UIInput) components.findComponent("referenceBeamId");
		  if (referenceBeamIdInput.getLocalValue() == null) {
		  
		  // get password
		  UIInput refBeamNumInput = (UIInput) components.findComponent("refBeamNum");
		  String refBeamNumStr = refBeamNumInput.getLocalValue() == null ? "" : refBeamNumInput.getLocalValue().toString();
		  String refBeamNumId = refBeamNumInput.getClientId();
		  
		  int refBeamNum = new Integer(refBeamNumStr);
		  
		  for (ReferenceBeam referenceBeam : referenceBeamList) {
			  
			  if (referenceBeam.getRefBeamNum() == refBeamNum) {
				  
					FacesMessage msg = new FacesMessage("Reference Beam Number " + refBeamNum + " is already defined.");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					fc.addMessage(refBeamNumId, msg);
					fc.renderResponse();

			  }
			  
		  }
		  }
		  
	}
	
	public String doViewReferenceBeamList() {

		breadcrumbMenuBean.addFirstItem("Reference Beams", "/modules/sysadmin/refBeamList.xhtml");

		return "/modules/sysadmin/refBeamList.xhtml?faces-redirect=true";
	}

	public String doViewReferenceBeam() {

		breadcrumbMenuBean.addItem("" + referenceBeam.getRefBeamNum(), "/modules/sysadmin/refBeamDetail.xhtml");

		return "/modules/sysadmin/refBeamDetail.xhtml?faces-redirect=true";
	}

	public String doNewReferenceBeam() {

		referenceBeam = new ReferenceBeam();
		
		referenceBeam.setCamera(physicalModel.getInstrument().getCamera());

		breadcrumbMenuBean.addItem("New Reference Beam", "/modules/sysadmin/refBeamDetail.xhtml");

		return "/modules/sysadmin/refBeamDetail.xhtml?faces-redirect=true";

	}

	public String doSaveReferenceBeam() {

		if (referenceBeam.isNewRecord()) {
			cameraDefMgmt.createReferenceBeam(referenceBeam);

		} else {
			cameraDefMgmt.updateReferenceBeam(referenceBeam);

		}
		refreshReferenceBeamList();
		return "/modules/sysadmin/refBeamList.xhtml?faces-redirect=true";
	}

	public String doCancelSaveReferenceBeam() {

		breadcrumbMenuBean.addFirstItem("Reference Beams", "/modules/sysadmin/refBeamList.xhtml");

		return "/modules/sysadmin/refBeamList.xhtml?faces-redirect=true";

	}

}
