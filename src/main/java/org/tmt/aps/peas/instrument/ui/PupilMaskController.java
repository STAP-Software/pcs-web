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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.PupilWheel;

@Named
@SessionScoped
public class PupilMaskController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<PupilMask> pupilMaskList;
	private PupilMask pupilMask;
	private PupilWheel pupilWheel;
	private List<PupilMaskType> pupilMaskTypeList;

	@PostConstruct
	private void init() {

		try {
			refreshPupilMaskList();
			refreshPupilWheel();
			pupilMaskTypeList = cameraDefMgmt.findAllPupilMaskTypes();
		} catch (Exception e) {
			logger.error("", e);
		}
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
		pupilMaskList = cameraDefMgmt.findAllPupilMasks();
	}

	private void refreshPupilWheel() throws Exception {
		
		// re-read in from database
		physicalModel.refresh();

		pupilWheel = physicalModel.getInstrument().getCamera().getPupilWheel();	
		pupilWheel.updateSlotsFromList();
	}

	public String doViewPupilMaskList() {
		
		try {
			refreshPupilMaskList();
			
			breadcrumbMenuBean.addFirstItem("PCS Pupil Masks", "/modules/sysadmin/pupilMaskList.xhtml");

			return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	public String doViewPupilMask() {

		breadcrumbMenuBean.addItem(pupilMask.getMaskName(), "/modules/sysadmin/pupilMaskDetail.xhtml");

		return "/modules/sysadmin/pupilMaskDetail.xhtml?faces-redirect=true";
	}

	public String doViewPupilWheel() {
		
		try {
			
			refreshPupilWheel();

			breadcrumbMenuBean.addItem("PCS Pupil Wheel", "/modules/sysadmin/pupilWheel.xhtml");

			return "/modules/sysadmin/pupilWheel.xhtml?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		
	}

	public String doNewPupilMask() {

		pupilMask = new PupilMask();

		breadcrumbMenuBean.addItem("New Pupil Mask", "/modules/sysadmin/pupilMaskDetail.xhtml");

		return "/modules/sysadmin/pupilMaskDetail.xhtml?faces-redirect=true";

	}

	public String doSavePupilMask() {
		
		try {
		
			if (pupilMask.isNewRecord()) {
				cameraDefMgmt.createPupilMask(pupilMask);
	
			} else {
				cameraDefMgmt.updatePupilMask(pupilMask);
			}
	
			refreshPupilMaskList();
		
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
		
			return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
			
			return null;
		}

	}

	public String doCancelSavePupilMask() {

		breadcrumbMenuBean.addFirstItem("PCS Pupil Masks", "/modules/sysadmin/pupilMaskList.xhtml");

		return "/modules/sysadmin/pupilMaskList.xhtml?faces-redirect=true";

	}

	public void doSavePupilWheel() {

		pupilWheel.updatePupilMaskStates();
		try {

			cameraDefMgmt.updatePupilWheel(pupilWheel);

			// clear old pupil masks states
			for (PupilMask pupilMask : pupilWheel.getOrigPupilMaskList()) {
				pupilMask.setWheelPosition(0);
				pupilMask.setPupilWheel(null);
				cameraDefMgmt.updatePupilMask(pupilMask);
			}

			// add in new masks
			for (PupilMask pupilMask : pupilWheel.getNewPupilMaskList()) {
				pupilMask.setPupilWheel(pupilWheel);
				cameraDefMgmt.updatePupilMask(pupilMask);
			}

			refreshPupilWheel();
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	public String doCancelSavePupilWheel() {

		return "/modules/sysadmin/pupilWheel.xhtml?faces-redirect=true";

	}

}
