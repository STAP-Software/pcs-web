/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterWheel;
import org.tmt.aps.peas.instrument.model.PupilMask;

@Named
@SessionScoped
public class FilterController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	@EJB
	PhysicalModel physicalModel;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Filter> filterList;
	private Filter filter;
	private FilterWheel filterWheel;

	@PostConstruct
	private void init() {

		try {
			refreshFilterList();
			refreshFilterWheel();
		} catch (Exception e) {
			logger.error("", e);
		}

	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public FilterWheel getFilterWheel() {
		return filterWheel;
	}

	public void setFilterWheel(FilterWheel filterWheel) {
		this.filterWheel = filterWheel;
	}

	private void refreshFilterList() {
		filterList = cameraDefMgmt.findAllFilters();
	}

	private void refreshFilterWheel() throws Exception {
		
		// re-read in from database
		physicalModel.refresh();
		
		filterWheel = physicalModel.getInstrument().getCamera().getFilterWheel();
		filterWheel.updateSlotsFromList();

	}

	public String doViewFilterList() {

		breadcrumbMenuBean.addFirstItem("PCS Filters", "doViewFilterList()");

		return "/modules/sysadmin/filterList.xhtml?faces-redirect=true";
	}

	public String doViewFilter() {

		breadcrumbMenuBean.addItem(filter.getFilterName(), "doViewFilter()");

		return "/modules/sysadmin/filterDetail.xhtml?faces-redirect=true";
	}

	public String doViewFilterWheel() {

		try {
			
			refreshFilterWheel();

			breadcrumbMenuBean.addFirstItem("PCS Filter Wheel", "doViewFilterWheel()");

			return "/modules/sysadmin/filterWheel.xhtml?faces-redirect=true";
			
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

	}

	public String doNewFilter() {

		filter = new Filter();

		breadcrumbMenuBean.addItem("New Filter", "doNewFilter()");

		return "/modules/sysadmin/filterDetail.xhtml?faces-redirect=true";

	}

	public String doSaveFilter() {

		if (filter.isNewRecord()) {
			cameraDefMgmt.createFilter(filter);

		} else {
			cameraDefMgmt.updateFilter(filter);

		}
		refreshFilterList();
		return "/modules/sysadmin/filterList.xhtml?faces-redirect=true";
	}

	public String doCancelSaveFilter() {

		breadcrumbMenuBean.addFirstItem("PCS Filters", "doViewFilterList()");

		return "/modules/sysadmin/filterList.xhtml?faces-redirect=true";

	}

	
	
	
	public String doSaveFilterWheel() {

		filterWheel.updateFilterStates();
		try {

			cameraDefMgmt.updateFilterWheel(filterWheel);

			// clear old filter masks states
			for (Filter filter : filterWheel.getOrigFilterList()) {
				filter.setWheelPosition(0);
				filter.setFilterWheel(null);
				cameraDefMgmt.updateFilter(filter);
			}

			// add in new masks
			for (Filter filter : filterWheel.getNewFilterList()) {
				filter.setFilterWheel(filterWheel);
				cameraDefMgmt.updateFilter(filter);
			}

			refreshFilterWheel();
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Record update successful", ""));
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Updating Database.  Check logs for details", ""));
			logger.error("", e);
		}

		return null;
	}

	public String doCancelSaveFilterWheel() {

		return "/modules/sysadmin/filterWheel.xhtml?faces-redirect=true";

	}



}
