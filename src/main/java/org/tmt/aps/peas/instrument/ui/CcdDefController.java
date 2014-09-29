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
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.instrument.business.CcdDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Ccd;

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
		refreshCcdList();
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

	private void refreshCcdList() {
		ccdList = ccdDefMgmt.findAllCcds();
	}

	public Ccd getSelectedCcd() {
		return selectedCcd;
	}

	public void setSelectedCcd(Ccd selectedCcd) {
		this.selectedCcd = selectedCcd;
	}

	public String doViewCcdList() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";
	}

	public String doViewCcd() {

		breadcrumbMenuBean.addItem(ccd.getCcdName(), "/modules/sysadmin/ccdDetail.xhtml");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";
	}

	public String doNewCcd() {

		ccd = new Ccd();

		breadcrumbMenuBean.addItem("New Ccd", "/modules/sysadmin/ccdDetail.xhtml");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";

	}

	public void doDeleteHotPixel() {

		try {
			ccd.removeHotPixel(hotPixelBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			physicalModel.refresh();

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}
	}
	
	public void doDeleteHotColumn() {

		try {
			ccd.removeHotColumn(hotColumnBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			physicalModel.refresh();

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}
	}

	public String doSaveCcd() {

		try {

			ccdDefMgmt.createCcd(ccd);

			refreshCcdList();

			physicalModel.refresh();
			
			breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");

			return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

			return null;
		}

	}

	public String doCancelSaveCcd() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "/modules/sysadmin/ccdList.xhtml");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	public void doAddHotPixel() {

		try {

			ccd.addHotPixel(hotPixelBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			hotPixelBoundingRect.reset();

			physicalModel.refresh();
			
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}
	}

	public void doAddHotColumn() {

		try {

			ccd.addHotColumn(hotColumnBoundingRect);

			ccdDefMgmt.updateCcd(ccd);

			hotColumnBoundingRect.reset();

			physicalModel.refresh();
			
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}
	}

	public String doViewCcdSelectList() {

		breadcrumbMenuBean.addFirstItem("Select a CCD", "/modules/sysadmin/ccdSelectList.xhtml");

		return "/modules/sysadmin/ccdSelectList.xhtml?faces-redirect=true";
	}

	public String doSaveCcdSelection() {

		try {
			ccdDefMgmt.assignCcdToInstrument(selectedCcd);

			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "CCD Assigned"));

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error", e.getMessage()));

		}

		return null;

	}

	public String doCancelSaveCcdSelection() {

		return null;

	}

}
