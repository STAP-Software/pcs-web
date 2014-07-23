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
		sufsGroupList = new ArrayList<SufsGroup>(physicalModel.getInstrument().getCamera().getPupilWheel().getSufsPupilMask().getSufsGroupSet());
		Collections.sort(sufsGroupList, new BeanComparator("groupNumber"));
	}

	public void validate(ComponentSystemEvent event) {
		 
		  FacesContext fc = FacesContext.getCurrentInstance();
	 
		  UIComponent components = event.getComponent();
	 
		  UIInput sufsGroupIdInput = (UIInput) components.findComponent("sufsGroupId");
		  if (sufsGroupIdInput.getLocalValue() == null) {
		  
		  // get group number
		  UIInput groupNumberInput = (UIInput) components.findComponent("groupNumber");
		  String groupNumberStr = groupNumberInput.getLocalValue() == null ? "" : groupNumberInput.getLocalValue().toString();
		  String groupNumberId = groupNumberInput.getClientId();
		  
		  int groupNumber = new Integer(groupNumberStr);
		  
		  for (SufsGroup sufsGroup : sufsGroupList) {
			  
			  if (sufsGroup.getGroupNumber() == groupNumber) {
				  
					FacesMessage msg = new FacesMessage("SUFS Group Number " + groupNumber + " is already defined.");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					fc.addMessage(groupNumberId, msg);
					fc.renderResponse();

			  }
			  
		  }
		  }
		  
	}
	
	public String doViewSufsGroupList() {

		try {
			refreshSufsGroupList();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "No SUFS Mask Defined.  A SUFS mask must be defined in the pupil wheel first.", ""));
			logger.error("", e);
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

			// validate against group number to make sure group numbers are unique and between 1 and 7
			for (SufsGroup candidate : sufsGroupList) {
				if (sufsGroup.getGroupNumber() == candidate.getGroupNumber()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "SUFS Group Number already exsits", ""));
					throw new Exception("SUFS Group Number already exsits");
				}
			}
			if (sufsGroup.getGroupNumber() > 7 || sufsGroup.getGroupNumber() < 1) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "SUFS Group Number must be between 1 and 7", ""));
				throw new Exception("SUFS Group Number must be between 1 and 7");
				
			}
			
			if (sufsGroup.isNewRecord()) {
				
				
				
				
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
