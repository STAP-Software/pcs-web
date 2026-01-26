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

/**
 * Configuration entity class representing the NbFilterSeqConfigDefaults table.  This table is joined with the NbFilterSeqConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "NbFilterSeqConfigDefaults")
@PrimaryKeyJoinColumn(name="nbFilterSeqConfigId")
@NamedQueries({
    @NamedQuery(
        name = "nbFilterSeqConfig.findByFilterSetOption",
        query = "SELECT o FROM NbFilterSeqConfigDefaults o " +
                "INNER JOIN FETCH o.iterationListConfigOption " + // no alias
                "WHERE o.iterationListConfigOption.iterationListConfigId = :iterationListConfigId"
    )
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
