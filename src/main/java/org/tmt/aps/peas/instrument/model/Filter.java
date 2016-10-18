/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;
/**
 * Instrument configuration Entity class representing the Filter table.  
 * @author smichaels
 */
@Entity
@Table(name = "Filter")
@NamedQueries({
	@NamedQuery(name = "findAllFilters", query = "SELECT o from Filter o INNER JOIN FETCH o.filterType" ),
	@NamedQuery(name = "findByFilterTypeAndWheel", query = "SELECT o from Filter o INNER JOIN FETCH o.filterWheel fw INNER JOIN FETCH o.filterType ft "
			+ "where ft.filterTypeId = :filterTypeId AND fw.filterWheelId = :filterWheelId" )
})
public class Filter implements IterableEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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

	@ManyToOne
	@JoinColumn (name="filterTypeId")
	private FilterType filterType;

	
	
	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	public String getFilterName() {
		return filterName;
	}

	public int getFilterNameAsNumber() {
		return new Integer(filterName);
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
	
	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public boolean isNewRecord() {
		return filterId == null;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Filter) {
			Filter candidate = (Filter)obj;
			if (candidate.getFilterId().longValue() == this.getFilterId().longValue()) {
				return true;
			}
		}
		return false;
	}
	
	public String getClassName() {
		return this.getClass().getName();
	}


	@Override
	public String getKeyFieldName() {
		return "filterId";
	}


	@Override
	public String getLabelFieldName() {
		return "filterName";
	}


	@Override
	public String getLabel() {
		return "Filter";
	}
}
