package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Filter")
public class Filter {

	@Id
	private Long filterId;

	private String filterName;

	private int wheelPosition;

	private float wavelength;

	private float bandwidth;

	private float coherenceLength;

	private float minCoherenceLimit;

	@ManyToOne
	@JoinColumn (name="filterWheelId")
	private FilterWheel filterWheel;

	
	
	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public int getWheelPosition() {
		return wheelPosition;
	}

	public void setWheelPosition(int wheelPosition) {
		this.wheelPosition = wheelPosition;
	}

	public float getWavelength() {
		return wavelength;
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public float getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(float bandwidth) {
		this.bandwidth = bandwidth;
	}

	public float getCoherenceLength() {
		return coherenceLength;
	}

	public void setCoherenceLength(float coherenceLength) {
		this.coherenceLength = coherenceLength;
	}

	public float getMinCoherenceLimit() {
		return minCoherenceLimit;
	}

	public void setMinCoherenceLimit(float minCoherenceLimit) {
		this.minCoherenceLimit = minCoherenceLimit;
	}

	public FilterWheel getFilterWheel() {
		return filterWheel;
	}

	public void setFilterWheel(FilterWheel filterWheel) {
		this.filterWheel = filterWheel;
	}

}
