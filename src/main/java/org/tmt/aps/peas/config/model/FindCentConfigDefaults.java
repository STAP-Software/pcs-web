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

import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "FindCentConfigDefaults")
@PrimaryKeyJoinColumn(name="findCentConfigId")
@NamedQueries({
	@NamedQuery(name = "findByMaskType", query = "SELECT o from FindCentConfigDefaults o INNER JOIN FETCH o.pupilMaskType p "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId and p.spotType = :spotType" )
})
public class FindCentConfigDefaults extends FindCentConfig {

	
	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

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
