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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;

/**
 * Instrument metadata Entity class representing the FilterType table.  
 * @author smichaels
 */
@Entity
@Table(name = "FilterType")
@NamedQueries({ @NamedQuery(name = "findAllFilterTypes", query = "SELECT o from FilterType o") })
public class FilterType implements IterableEntity {

	public static final Long FILTER_TYPE_ID_611 = Long.valueOf(1);
	public static final Long FILTER_TYPE_ID_651 = Long.valueOf(2);
	public static final Long FILTER_TYPE_ID_852 = Long.valueOf(3);
	public static final Long FILTER_TYPE_ID_870 = Long.valueOf(4);
	public static final Long FILTER_TYPE_ID_891 = Long.valueOf(5);
	public static final Long FILTER_TYPE_ID_NONE = Long.valueOf(6);
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long filterTypeId;

	private String filterTypeName;


	
	
	public Long getFilterTypeId() {
		return filterTypeId;
	}


	public void setFilterTypeId(Long filterTypeId) {
		this.filterTypeId = filterTypeId;
	}


	public String getFilterTypeName() {
		return filterTypeName;
	}


	public void setFilterTypeName(String filterTypeName) {
		this.filterTypeName = filterTypeName;
	}


	public boolean isNewRecord() {
		return filterTypeId == null;
	}

	
	public boolean equals(Object obj) {
		if (obj instanceof FilterType) {
			FilterType candidate = (FilterType) obj;
			if (candidate.getFilterTypeId().longValue() == this.getFilterTypeId().longValue()) {
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
		return "filterTypeId";
	}


	@Override
	public String getLabelFieldName() {
		return "filterTypeName";
	}


	@Override
	public String getLabel() {
		return "Filter";
	}

}
