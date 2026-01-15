/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.tmt.aps.peas.common.Utils;

/**
 * Procedure output data Entity representing the ProcedureOutputValue table.
 * String encoded Data for a procedure output field for a procedure or iteration.  
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureOutputValue")
@NamedQueries({
	@NamedQuery(name = "findOutputDisplayValuesForProcedure", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
			+ "INNER JOIN f.procedureOutputFieldDisplay fd INNER JOIN fd.procedureType pt "
			+ "where p.procedureId = :procedureId "
			+ "AND p.iteration is null "
			+ "AND pt.procedureTypeId = :procedureTypeId "
			+ "AND fd.isIteration = false "
			+ "ORDER BY fd.displayOrder "),
	@NamedQuery(name = "findAllOutputValuesForProcedure", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
			+ "where p.procedureId = :procedureId "
			+ "AND p.iteration is null "),
	@NamedQuery(name = "findOutputDisplayValuesForProcedureIteration", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
			+ "INNER JOIN f.procedureOutputFieldDisplay fd INNER JOIN fd.procedureType pt "
			+ "where p.procedureId = :procedureId "
			+ "AND p.iteration = :iteration "
			+ "AND pt.procedureTypeId = :procedureTypeId "
			+ "AND fd.isIteration = true "
			+ "ORDER BY fd.displayOrder "),
	@NamedQuery(name = "findAllOutputValuesForProcedureIteration", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
			+ "where p.procedureId = :procedureId "
			+ "AND p.iteration = :iteration ") })

public class ProcedureOutputValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long procedureOutputValueId;

	Long procedureId;
	Integer iteration;

	@Column(nullable = false, length = 10000)
	private String data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureOutputFieldId", nullable = false, updatable = false)
	ProcedureOutputField procedureOutputField;

	
	public Long getProcedureOutputValueId() {
		return procedureOutputValueId;
	}

	public void setProcedureOutputValueId(Long procedureOutputValueId) {
		this.procedureOutputValueId = procedureOutputValueId;
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ProcedureOutputField getProcedureOutputField() {
		return procedureOutputField;
	}

	public void setProcedureOutputField(ProcedureOutputField procedureOutputField) {
		this.procedureOutputField = procedureOutputField;
	}

	public Integer getIteration() {
		return iteration;
	}

	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}

	public String getDataFormatted() {
		return Utils.reformatData(data, procedureOutputField);
	}




}
