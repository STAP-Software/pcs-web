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

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.SufsGroup;

/**
 * Configuration entity class representing the SufsCoarseOffsetsConfigDefaults table.  This table is joined with the SufsCoarseOffsetsConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "SufsCoarseOffsetsConfigDefaults")
@PrimaryKeyJoinColumn(name="SufsCoarseOffsetsConfigId")
@NamedQueries({
    @NamedQuery(
        name = "findSufsCoarseOffsetsConfig",
        query = "SELECT o FROM SufsCoarseOffsetsConfigDefaults o " +
                "INNER JOIN FETCH o.instrument " +
                "INNER JOIN FETCH o.sufsGroup " +
                "WHERE o.instrument.instrumentId = :instrumentId " +
                "AND o.sufsGroup.sufsGroupId = :sufsGroupId"
    )
})


public class SufsCoarseOffsetsConfigDefaults extends SufsCoarseOffsetsConfig {

	@ManyToOne
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;
	
	@ManyToOne
	@JoinColumn(name = "sufsGroupId")
	private SufsGroup sufsGroup;

}
