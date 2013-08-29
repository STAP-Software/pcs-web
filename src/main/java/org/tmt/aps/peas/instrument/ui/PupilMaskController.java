/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.PupilWheel;

@Named
@SessionScoped
public class PupilMaskController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB 
	CameraDefMgmt cameraDefMgmt;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<PupilMask> pupilMaskList;
	private PupilMask pupilMask;
	private PupilWheel pupilWheel;
	private List<PupilMaskType> pupilMaskTypeList;

	@PostConstruct
	private void init() {
		
		refreshPupilMaskList();
		refreshPupilWheel();
		pupilMaskTypeList = cameraDefMgmt.findAllPupilMaskTypes();
	}

	public List<PupilMask> getPupilMaskList() {
		return pupilMaskList;
	}

	public void setPupilMaskList(List<PupilMask> pupilMaskList) {
		this.pupilMaskList = pupilMaskList;
	}

	public PupilMask getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(PupilMask pupilMask) {
		this.pupilMask = pupilMask;
	}

	public PupilWheel getPupilWheel() {
		return pupilWheel;
	}

	public void setPupilWheel(PupilWheel pupilWheel) {
		this.pupilWheel = pupilWheel;
	}

	public List<PupilMaskType> getPupilMaskTypeList() {
		return pupilMaskTypeList;
	}

	private void refreshPupilMaskList() {
		pupilMaskList =cameraDefMgmt.findAllPupilMasks();
	}
	
	private void refreshPupilWheel() {
		
		//String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");

		// TODO: implement
		// the idea here may be that the entire instrument is loaded at system startup
		// and the tree is parsed to get the appropriate pupil wheel.
	}

	public String doViewPupilMaskList() {

		breadcrumbMenuBean.addFirstItem("PCS Pupil Masks", "doViewPupilMaskList()");

		return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";
	}

	public String doViewPupilMask() {

		breadcrumbMenuBean.addItem(pupilMask.getMaskName(), "doViewPupilMask()");

		return "/modules/sysadmin/pupilMaskDetail.xhtml?faces-redirect=true";
	}

	public String doViewPupilWheel() {

		breadcrumbMenuBean.addItem("PCS Pupil Wheel", "doViewPupilWheel()");

		return "/modules/sysadmin/pupilWheel.xhtml?faces-redirect=true";
	}

	public String doNewPupilMask() {

		pupilMask = new PupilMask();
		
		breadcrumbMenuBean.addItem("New Pupil Mask", "doNewPupilMask()");

		return "/modules/sysadmin/pupilMaskDetail.xhtml?faces-redirect=true";

	}

	public String doSavePupilMask() {
		if (pupilMask.isNewRecord()) {
			cameraDefMgmt.createPupilMask(pupilMask);
			
		} else {
			cameraDefMgmt.updatePupilMask(pupilMask);
		}


		refreshPupilMaskList();
		return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";
	}

	public String doCancelSavePupilMask() {

		breadcrumbMenuBean.addFirstItem("PCS Pupil Masks", "doViewPupilMaskList()");

		return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";

	}
	
	public String doSavePupilWheel() {

		cameraDefMgmt.updatePupilWheel(pupilWheel);
		
		refreshPupilMaskList();
		return "/modules/sysadmin/pupilWheel.xhtml?faces-redirect=true";
	}

	public String doCancelSavePupilWheel() {


		return "/modules/sysadmin/pupilWheel.xhtml?faces-redirect=true";

	}

}
