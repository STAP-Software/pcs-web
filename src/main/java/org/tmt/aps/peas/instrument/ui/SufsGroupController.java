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
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.SufsGroup;

@Named
@SessionScoped
public class SufsGroupController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	@EJB
	private PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private SufsGroup sufsGroup;

	private List<SufsGroup> sufsGroupList;

	@PostConstruct
	private void init() throws Exception {

	}

	public SufsGroup getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(SufsGroup sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public List<SufsGroup> getSufsGroupList() {
		return sufsGroupList;
	}

	public void setSufsGroupList(List<SufsGroup> sufsGroupList) {
		this.sufsGroupList = sufsGroupList;
	}

	public void refreshSufsGroupList() throws Exception {
		physicalModel.refresh();
		sufsGroupList = physicalModel.getInstrument().getCamera().getPupilWheel().getSufsPupilMask().getSufsGroupList();
	}

	public String doViewSufsGroupList() {

		try {
			refreshSufsGroupList();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No SUFS Mask Defined.  A SUFS mask must be defined first.", ""));
			return null;
		}
		breadcrumbMenuBean.addFirstItem("Sufs Groups", "doViewSufsGroupList()");

		return "/modules/sysadmin/sufsGroupList.xhtml?faces-redirect=true";
	}

	public String doViewSufsGroup() {

		breadcrumbMenuBean.addFirstItem("Sufs Group " + sufsGroup, "doViewSufsGroup()");

		return "/modules/sysadmin/sufsGroupDetail.xhtml?faces-redirect=true";
	}

	public String doNewSufsGroup() {

		sufsGroup = new SufsGroup();
		sufsGroup.setPupilMask(physicalModel.getInstrument().getCamera().getPupilWheel().getSufsPupilMask());
		
		breadcrumbMenuBean.addFirstItem("Sufs Group " + sufsGroup, "doViewSufsGroup()");

		return "/modules/sysadmin/sufsGroupDetail.xhtml?faces-redirect=true";
	}

	public String doSaveSufsGroup() {

		try {

			if (sufsGroup.isNewRecord()) {
				
				// TODO: need to validate against group number to make sure group numbers are unique and between 1 and 7
				
				cameraDefMgmt.createSufsGroup(sufsGroup);
			} else {
				cameraDefMgmt.updateSufsGroup(sufsGroup);
			}
			refreshSufsGroupList();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating Sufs Group Configuration", ""));
			return null;
		}
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully updated Sufs Group Configuration", ""));

		breadcrumbMenuBean.addFirstItem("Sufs Groups", "doViewSufsGroupList()");

		return "/modules/sysadmin/sufsGroupList.xhtml?faces-redirect=true";
	}

	public String doCancelSaveSufsGroup() {

		breadcrumbMenuBean.addFirstItem("Sufs Groups", "doViewSufsGroupList()");

		return "/modules/sysadmin/sufsGroupList.xhtml?faces-redirect=true";

	}

}
