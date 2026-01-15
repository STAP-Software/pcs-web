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
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.instrument.business.CcdDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Ccd;

/**
 * JSF Controller for CCD configuration user interface
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class CcdDefController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CcdDefMgmt ccdDefMgmt;
	@EJB
	PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Ccd> ccdList;
	private Ccd ccd;
	private Rect hotPixelBoundingRect;
	private Rect hotColumnBoundingRect;
	private Ccd selectedCcd;

	@PostConstruct
	private void init() {
		hotPixelBoundingRect = new Rect();
		hotPixelBoundingRect.reset();
		hotColumnBoundingRect = new Rect();
		hotColumnBoundingRect.reset();
	}

	public List<Ccd> getCcdList() {
		return ccdList;
	}

	public void setCcdList(List<Ccd> ccdList) {
		this.ccdList = ccdList;
	}

	public Ccd getCcd() {
		return ccd;
	}

	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
	}

	public Rect getHotPixelBoundingRect() {
		return hotPixelBoundingRect;
	}

	public void setHotPixelBoundingRect(Rect hotPixelBoundingRect) {
		this.hotPixelBoundingRect = hotPixelBoundingRect;
	}

	public Rect getHotColumnBoundingRect() {
		return hotColumnBoundingRect;
	}

	public void setHotColumnBoundingRect(Rect hotColumnBoundingRect) {
		this.hotColumnBoundingRect = hotColumnBoundingRect;
	}

	public Ccd getSelectedCcd() {
		return selectedCcd;
	}

	public void setSelectedCcd(Ccd selectedCcd) {
		this.selectedCcd = selectedCcd;
	}

	/**
	 * JSF Action method to view the list of CCDs
	 * @return the JSF page to render the CCD list
	 */
	public String doViewCcdList() {
		
		try {
		
			ccdList = ccdDefMgmt.findAllCcds();
	
			breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");
	
			return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	/**
	 * JSF Action method to view a single CCD configuration
	 * @return the JSF page to render the CCD detail
	 */
	public String doViewCcd() {

		breadcrumbMenuBean.addItem(ccd.getCcdName(), "/modules/sysadmin/ccdDetail.xhtml");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method to setup a new CCD configuration
	 * @return the JSF page to render the CCD detail
	 */
	public String doNewCcd() {

		ccd = new Ccd();

		breadcrumbMenuBean.addItem("New Ccd", "/modules/sysadmin/ccdDetail.xhtml");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";

	}

	/**
	 * JSF Action method to remove a hot pixel from the current CCD configuration
	 */
	public void doDeleteHotPixel() {

		try {
			ccd.removeHotPixel(hotPixelBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			physicalModel.refresh();

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}
	
	/**
	 * JSF Action method to remove a hot column from the current CCD configuration
	 */
	public void doDeleteHotColumn() {

		try {
			ccd.removeHotColumn(hotColumnBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			physicalModel.refresh();

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	/**
	 * JSF Action to save a CCD configuration
	 * @return the JSF page to render when complete
	 */
	public String doSaveCcd() {

		try {

			if (ccd.isNewRecord()) {
				ccdDefMgmt.createCcd(ccd);
			} else {
				ccdDefMgmt.updateCcd(ccd);
			}

			ccdList = ccdDefMgmt.findAllCcds();

			physicalModel.refresh();
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
			breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");

			return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
			
			return null;
		}

	}

	/**
	 * JSF Action method called when the 'Cancel' button is clicked
	 * @return the JSF page to render the CCD list
	 */
	public String doCancelSaveCcd() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	/**
	 * JSF Action method to add a hot pixel to the current CCD configuration
	 */
	public void doAddHotPixel() {

		try {

			ccd.addHotPixel(hotPixelBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			hotPixelBoundingRect.reset();

			physicalModel.refresh();
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}
	}

	/**
	 * JSF Action method to add a hot column to the current CCD configuration
	 */
	public void doAddHotColumn() {

		try {

			ccd.addHotColumn(hotColumnBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			hotColumnBoundingRect.reset();

			physicalModel.refresh();
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

	/**
	 * JSF Action method to view the CCDs available to assign to the instrument
	 * @return the JSF page to render the CCD selection list
	 */
	public String doViewCcdSelectList() {
		
		try {
			ccdList = ccdDefMgmt.findAllCcds();
	
			
			selectedCcd = physicalModel.getInstrument().getCcd();
			
			logger.debug("selected ccd is: " + selectedCcd);
			
			breadcrumbMenuBean.addFirstItem("Select a CCD", "/modules/sysadmin/ccdSelectList.xhtml");
	
			return "/modules/sysadmin/ccdSelectList.xhtml?faces-redirect=true";
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

		
	}

	/**
	 * JSF Action method to assign the selected CCD to the instrument
	 */
	public void doSaveCcdSelection() {

		try {
			
			ccdDefMgmt.assignCcdToInstrument(selectedCcd);
			
			physicalModel.refresh();

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			
			if (selectedCcd == null) {
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You must select an existing CCD", 
						"The system will not run without a CCD associated with this instrument"));
				logger.error(MessageGenerator.generateMessage("crud.failure"), e);

			} else {
			
				FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
				logger.error(MessageGenerator.generateMessage("crud.failure"), e);
			}
		}


	}

	/**
	 * JSF Action method called when the 'Cancel' button is clicked
	 */
	public void doCancelSaveCcdSelection() {


	}

}
