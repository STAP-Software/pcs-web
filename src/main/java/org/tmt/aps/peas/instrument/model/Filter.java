/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;
/**
 * Instrument configuration Entity class representing the Filter table.  
 * @author smichaels
 */
@Entity
@Table(name = "Filter")
@NamedQueries({
    @NamedQuery(
        name = "findAllFilters",
        query = "SELECT o FROM Filter o " +
                "INNER JOIN FETCH o.filterType"
    ),
    @NamedQuery(
        name = "findByFilterTypeAndWheel",
        query = "SELECT o FROM Filter o " +
                "INNER JOIN FETCH o.filterWheel " +
                "INNER JOIN FETCH o.filterType " +
                "WHERE o.filterType.filterTypeId = :filterTypeId " +
                "AND o.filterWheel.filterWheelId = :filterWheelId"
    )
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
		try {
			return Integer.valueOf(filterName);
		} catch (Exception e) {
			// if 'None' then use 0
			return 0;
		}
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
