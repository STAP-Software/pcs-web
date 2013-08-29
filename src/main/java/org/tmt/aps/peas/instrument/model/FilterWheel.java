/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "FilterWheel")

public class FilterWheel {

	@Id
	private Long filterWheelId;
	
	@OneToOne
	@JoinColumn (name="cameraId")
	private Camera camera;
	
	@OneToMany (mappedBy="filterWheel")
	List<Filter> filterList;
	
	@Transient
	private Filter selectedFilter;
	@Transient
	private Filter filter1;
	@Transient
	private Filter filter2;
	@Transient
	private Filter filter3;
	@Transient
	private Filter filter4;
	@Transient
	private Filter filter5;
	@Transient
	private Filter filter6;

	
	
	public Long getFilterWheelId() {
		return filterWheelId;
	}

	public void setFilterWheelId(Long filterWheelId) {
		this.filterWheelId = filterWheelId;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Filter getSelectedFilter() {
		return selectedFilter;
	}

	public void setSelectedFilter(Filter selectedFilter) {
		this.selectedFilter = selectedFilter;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}

	public Filter getFilter1() {
		return filter1;
	}

	public void setFilter1(Filter filter1) {
		this.filter1 = filter1;
	}

	public Filter getFilter2() {
		return filter2;
	}

	public void setFilter2(Filter filter2) {
		this.filter2 = filter2;
	}

	public Filter getFilter3() {
		return filter3;
	}

	public void setFilter3(Filter filter3) {
		this.filter3 = filter3;
	}

	public Filter getFilter4() {
		return filter4;
	}

	public void setFilter4(Filter filter4) {
		this.filter4 = filter4;
	}

	public Filter getFilter5() {
		return filter5;
	}

	public void setFilter5(Filter filter5) {
		this.filter5 = filter5;
	}

	public Filter getFilter6() {
		return filter6;
	}

	public void setFilter6(Filter filter6) {
		this.filter6 = filter6;
	}



	

	
	
}
