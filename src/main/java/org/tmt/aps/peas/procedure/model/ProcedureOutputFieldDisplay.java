/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

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
