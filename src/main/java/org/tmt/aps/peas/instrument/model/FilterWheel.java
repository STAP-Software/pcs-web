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



	

	
	
}
