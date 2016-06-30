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

/**
 * Configuration entity class representing the PupilRegErrorConfigDefaults table.  This table is joined with the PupilRegErrorConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "PupilRegErrorConfigDefaults")
@PrimaryKeyJoinColumn(name="pupilRegErrorConfigId")
@NamedQueries({
	@NamedQuery(name = "pupilRegErrorConfig.findByMaskType", query = "SELECT o from PupilRegErrorConfigDefaults o INNER JOIN FETCH o.pupilMaskType p "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId" )
})
public class PupilRegErrorConfigDefaults extends PupilRegErrorConfig {

	
	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}
	
}
