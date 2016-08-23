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

import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the IterationListConfigOption table.  This table is joined with the IterationListConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "IterationListConfigOption")
@PrimaryKeyJoinColumn(name="iterationListConfigId")
@NamedQueries({
	@NamedQuery(name = "findIterationListConfigOptions", query = "SELECT o from IterationListConfigOption o INNER JOIN FETCH o.procedureType p "
			+ "where p.procedureTypeId = :procedureTypeId ORDER BY o.optionOrder" )
})
public class IterationListConfigOption extends IterationListConfig {
	
	int optionOrder;
	
	@ManyToOne
	@JoinColumn(name = "procedureTypeId")
	private ProcedureType procedureType;

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}

	public int getOptionOrder() {
		return optionOrder;
	}

	public void setOptionOrder(int optionOrder) {
		this.optionOrder = optionOrder;
	}
	
}
