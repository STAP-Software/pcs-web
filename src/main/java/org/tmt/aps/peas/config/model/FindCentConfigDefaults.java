/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
	@NamedQuery(name = "findByMaskType", query = "SELECT o from FindCentConfigDefaults o INNER JOIN FETCH o.pupilMaskType p INNER JOIN FETCH o.filterType f "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId and f.filterTypeId = :filterTypeId and o.spotType = :spotType" )
})
public class FindCentConfigDefaults extends FindCentConfig {

	
	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;
	
	@ManyToOne
	@JoinColumn(name = "filterTypeId")
	private FilterType filterType;

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
