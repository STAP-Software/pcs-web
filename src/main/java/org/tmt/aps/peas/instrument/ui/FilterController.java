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
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterWheel;

@Named
@SessionScoped
public class FilterController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Filter> filterList;
	private Filter filter;
	private FilterWheel filterWheel;

	@PostConstruct
	private void init() {

		refreshFilterList();
		refreshFilterWheel();
				
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
	
	private void refreshFilterWheel() {
		
		//String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");

		// TODO: implement
		// the idea here may be that the entire instrument is loaded at system startup
		// and the tree is parsed to get the appropriate filter wheel.
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

		breadcrumbMenuBean.addFirstItem("PCS Filter Wheel", "doViewFilterWheel()");

		return "/modules/sysadmin/filterWheel.xhtml?faces-redirect=true";
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

		cameraDefMgmt.updateFilterWheel(filterWheel);
		return "/modules/sysadmin/filterWheel.xhtml?faces-redirect=true";
	}

	public String doCancelSaveFilterWheel() {

		return "/modules/sysadmin/filterWheel.xhtml?faces-redirect=true";

	}

}
