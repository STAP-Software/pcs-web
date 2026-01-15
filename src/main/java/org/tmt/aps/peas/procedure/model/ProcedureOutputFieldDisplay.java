/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.Table;

/**
 * Metadata Entity representing a single row in the ProcedureOutputFieldDisplay table.
 * The metadata describes maps procedure output fields to procedure types, iteration, and what order the field will be displayed in the report
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureOutputFieldDisplay")
@NamedQueries({

})

public class ProcedureOutputFieldDisplay {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long procedureOutputFieldDisplayId;
	
	int displayOrder;
	
	boolean isIteration;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureOutputFieldId", nullable = false, updatable = false)
	ProcedureOutputField procedureOutputField;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureTypeId", nullable = false, updatable = false)
	ProcedureType procedureType;

	
}
