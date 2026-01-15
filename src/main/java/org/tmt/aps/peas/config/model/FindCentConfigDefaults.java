/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Configuration entity class representing the FindCentConfigDefaults table.  This table is joined with the FindCentConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "FindCentConfigDefaults")
@PrimaryKeyJoinColumn(name="findCentConfigId")
@NamedQueries({
	@NamedQuery(name = "findByMaskType", query = "SELECT o from FindCentConfigDefaults o INNER JOIN FETCH o.pupilMaskType p "
			+ "INNER JOIN FETCH o.filterType f INNER JOIN FETCH o.ccdType t "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId and f.filterTypeId = :filterTypeId and "
			+ "t.ccdTypeId = :ccdTypeId and o.spotType = :spotType" )
})
public class FindCentConfigDefaults extends FindCentConfig {

	
	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;
	
	@ManyToOne
	@JoinColumn(name = "filterTypeId")
	private FilterType filterType;

	@ManyToOne
	@JoinColumn(name = "ccdTypeId")
	private CcdType ccdType;

	
	int spotType;
	
	public int getSpotType() {
		return spotType;
	}

	public void setSpotType(int spotType) {
		this.spotType = spotType;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}
	
}
