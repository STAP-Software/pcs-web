/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.common.Utils;

@Entity
@Table(name = "ProcedureOutputValue")
@NamedQueries({
		@NamedQuery(name = "findOutputValuesForProcedure", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
				+ "where p.procedureId = :procedureId AND p.iteration is null ORDER BY f.displayOrder "),
		@NamedQuery(name = "findOutputValuesForProcedureIteration", query = "SELECT p from ProcedureOutputValue p INNER JOIN FETCH p.procedureOutputField f "
				+ "where p.procedureId = :procedureId AND p.iteration = :iteration ORDER BY f.displayOrder ") })
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
