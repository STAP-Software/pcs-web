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

import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Configuration entity class representing the PupilRegErrorConfigDefaults table.  This table is joined with the PupilRegErrorConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "PupilRegErrorConfigDefaults")
@PrimaryKeyJoinColumn(name="pupilRegErrorConfigId")
@NamedQueries({
    @NamedQuery(
        name = "pupilRegErrorConfig.findByMaskType",
        query = "SELECT o FROM PupilRegErrorConfigDefaults o " +
                "INNER JOIN FETCH o.pupilMaskType " +  // no alias here
                "WHERE o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId"
    )
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
