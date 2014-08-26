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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.instrument.business.CcdDefMgmt;
import org.tmt.aps.peas.instrument.model.Ccd;

@Named
@SessionScoped
public class CcdDefController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CcdDefMgmt ccdDefMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Ccd> ccdList;
	private Ccd ccd;
	private Rect hotPixelBoundingRect;
	private Ccd selectedCcd;

	@PostConstruct
	private void init() {
		refreshCcdList();
		hotPixelBoundingRect = new Rect();
		hotPixelBoundingRect.reset();
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

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";
	}

	public String doViewCcd() {

		breadcrumbMenuBean.addItem(ccd.getCcdName(), "doViewCcd()");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";
	}

	public String doNewCcd() {

		ccd = new Ccd();

		breadcrumbMenuBean.addItem("New Ccd", "doNewCcd()");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";

	}

	public void doDeleteHotPixel() {

		ccd.removeHotPixel(hotPixelBoundingRect);
		
		ccdDefMgmt.updateCcd(ccd);

	}

	public String doSaveCcd() {

		ccdDefMgmt.createCcd(ccd);

		refreshCcdList();

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	public String doCancelSaveCcd() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	public void doAddHotPixel() {


		ccd.addHotPixel(hotPixelBoundingRect);
		
		ccdDefMgmt.updateCcd(ccd);
		
		hotPixelBoundingRect.reset();

	}

	public String doViewCcdSelectList() {

		breadcrumbMenuBean.addFirstItem("Select a CCD", "doViewCcdSelectList()");

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
