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

/**
 * Configuration entity class representing the NbFilterSeqConfigDefaults table.  This table is joined with the NbFilterSeqConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "NbFilterSeqConfigDefaults")
@PrimaryKeyJoinColumn(name="nbFilterSeqConfigId")
@NamedQueries({
	@NamedQuery(name = "nbFilterSeqConfig.findByFilterSetOption", query = "SELECT o from NbFilterSeqConfigDefaults o INNER JOIN FETCH o.iterationListConfigOption ilco "
			+ "where ilco.iterationListConfigId = :iterationListConfigId" )
})
public class NbFilterSeqConfigDefaults extends NbFilterSeqConfig {

	
	@ManyToOne
	@JoinColumn(name = "iterationListConfigId")
	private IterationListConfigOption iterationListConfigOption;

	public IterationListConfigOption getIterationListConfigOption() {
		return iterationListConfigOption;
	}

	public void setIterationListConfigOption(IterationListConfigOption iterationListConfigOption) {
		this.iterationListConfigOption = iterationListConfigOption;
	}


	
}
