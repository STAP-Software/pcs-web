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

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.SufsGroup;

@Entity
@Table(name = "SufsCoarseOffsetsConfigDefaults")
@PrimaryKeyJoinColumn(name="SufsCoarseOffsetsConfigId")
@NamedQueries({ @NamedQuery(name = "findSufsCoarseOffsetsConfig", query = "SELECT o from SufsCoarseOffsetsConfigDefaults o INNER JOIN FETCH o.instrument i INNER JOIN FETCH o.sufsGroup s "
		+ " WHERE i.instrumentId = :instrumentId and s.sufsGroupId = :sufsGroupId ") })

public class SufsCoarseOffsetsConfigDefaults extends SufsCoarseOffsetsConfig {

	@ManyToOne
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;
	
	@ManyToOne
	@JoinColumn(name = "sufsGroupId")
	private SufsGroup sufsGroup;

}
