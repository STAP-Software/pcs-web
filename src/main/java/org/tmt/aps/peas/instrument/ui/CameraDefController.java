/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

@Named
@SessionScoped
public class CameraDefController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<ReferenceBeam> attachedReferenceBeamList;
	private List<ReferenceBeam> availableReferenceBeamList;
	

	@PostConstruct
	private void init() {

	}

	public List<ReferenceBeam> getAttachedReferenceBeamList() {
		return attachedReferenceBeamList;
	}

	public void setAttachedReferenceBeamList(List<ReferenceBeam> attachedReferenceBeamList) {
		this.attachedReferenceBeamList = attachedReferenceBeamList;
	}

	public List<ReferenceBeam> getAvailableReferenceBeamList() {
		return availableReferenceBeamList;
	}

	public void setAvailableReferenceBeamList(List<ReferenceBeam> availableReferenceBeamList) {
		this.availableReferenceBeamList = availableReferenceBeamList;
	}

	public String doViewReferenceBeams() {

		breadcrumbMenuBean.addFirstItem("PCS Filters", "doViewFilterList()");

		return "/modules/sysadmin/filterList.xhtml?faces-redirect=true";
	}


	public void doSaveReferenceBeams() {

		
		
	}

	public String doCancelSaveReferenceBeams() {

		breadcrumbMenuBean.addFirstItem("PCS Reference Beams", "doViewReferenceBeams()");

		return "/modules/sysadmin/referenceBeams.xhtml?faces-redirect=true";

	}

}
