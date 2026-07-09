/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
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

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.SufsGroup;

/**
 * JSF Controller for the SufsGroup configuration user interface
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class SufsGroupController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4292536017311327328L;

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
	private void init() {

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
		
		sufsGroupList = cameraDefMgmt.findSufsGroups();
		
		// V3.0
		//Set<SufsGroup> sufsGroupSet = new HashSet<SufsGroup>(sufsGroupList);

		// update physical model on the fly
		//physicalModel.getInstrument().getCamera().getPupilWheel().getSufsPupilMask().setSufsGroupSet(sufsGroupSet);
		//Collections.sort(sufsGroupList, new BeanComparator("groupNumber"));
	}

	/**
	 * Validates the form input.  Does not allow duplicate group numbers.
	 * @param event
	 */
	public void validate(ComponentSystemEvent event) {

		FacesContext fc = FacesContext.getCurrentInstance();

		UIComponent components = event.getComponent();

		UIInput sufsGroupIdInput = (UIInput) components.findComponent("sufsGroupId");
		if (sufsGroupIdInput.getLocalValue() == null) {

			// get group number
			UIInput groupNumberInput = (UIInput) components.findComponent("groupNumber");
			String groupNumberStr = groupNumberInput.getLocalValue() == null ? "" : groupNumberInput.getLocalValue().toString();
			String groupNumberId = groupNumberInput.getClientId();

			try {

				int groupNumber = Integer.valueOf(groupNumberStr);

				for (SufsGroup sufsGroup : sufsGroupList) {

					if (sufsGroup.getGroupNumber() == groupNumber) {

						FacesMessage msg = new FacesMessage("SUFS Group Number " + groupNumber + " is already defined.");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						fc.addMessage(groupNumberId, msg);
						fc.renderResponse();

					}

				}
			} catch (Exception e) {
				FacesMessage msg = new FacesMessage("SUFS Group Number " + groupNumberStr + " is not valid.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				fc.addMessage(groupNumberId, msg);
				fc.renderResponse();

			}
		}

	}

	/**
	 * JSF Action method to view the SUFS group list
	 * @return the JSF page to render the SUFS group list
	 */
	public String doViewSufsGroupList() {

		try {
			refreshSufsGroupList();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"No SUFS Mask Defined.  A SUFS mask must be defined in the pupil wheel first.", ""));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		breadcrumbMenuBean.addFirstItem("Sufs Groups", "/modules/sysadmin/sufsGroupList.xhtml");

		return "/modules/sysadmin/sufsGroupList.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to view a single SUFS group configuration
	 * @return the JSF page to render an SUFS group detail
	 */
	public String doViewSufsGroup() {

		breadcrumbMenuBean.addItem("Sufs Group " + sufsGroup.getGroupNumber(), "/modules/sysadmin/sufsGroupDetail.xhtml");

		return "/modules/sysadmin/sufsGroupDetail.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to create a new SUFS group
	 * @return the JSF page to render an SUFS group detail
	 */
	public String doNewSufsGroup() {

		sufsGroup = new SufsGroup();

		breadcrumbMenuBean.addItem("New Sufs Group ", "/modules/sysadmin/sufsGroupDetail.xhtml");

		return "/modules/sysadmin/sufsGroupDetail.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to save an SUFS group configuration.  Does not allow duplicate group numbers or group numbers greater than 7.
	 */
	public void doSaveSufsGroup() {

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

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}
	
	/**
	 * JSF Action method called when the 'Cancel' button is clicked
	 * @return the JSF page to render the SUFS group list
	 */

	public String doCancelSaveSufsGroup() {

		breadcrumbMenuBean.addFirstItem("Sufs Groups", "/modules/sysadmin/sufsGroupList.xhtml");

		return "/modules/sysadmin/sufsGroupList.xhtml?faces-redirect=true";

	}
	

}
