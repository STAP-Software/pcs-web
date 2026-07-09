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
import jakarta.persistence.SequenceGenerator;
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
    @NamedQuery(
        name = "findOutputDisplayValuesForProcedure",
        query = "SELECT p FROM ProcedureOutputValue p " +
                "INNER JOIN FETCH p.procedureOutputField " +        // normal join for filtering
                "INNER JOIN p.procedureOutputField f " +        // normal join for filtering
                "INNER JOIN f.procedureOutputFieldDisplay fdElem " +
                "INNER JOIN fdElem.procedureType pt " +
                "WHERE p.procedureId = :procedureId " +
                "AND p.iteration IS NULL " +
                "AND pt.procedureTypeId = :procedureTypeId " +
                "AND fdElem.isIteration = false " +
                "ORDER BY fdElem.displayOrder"
    ),
    @NamedQuery(
        name = "findAllOutputValuesForProcedure",
        query = "SELECT p FROM ProcedureOutputValue p " +
                "INNER JOIN FETCH p.procedureOutputField " +
                "WHERE p.procedureId = :procedureId " +
                "AND p.iteration IS NULL"
    ),
    @NamedQuery(
    	    name = "findOutputDisplayValuesForProcedureIteration",
    	    query = "SELECT p FROM ProcedureOutputValue p " +
    	            "INNER JOIN FETCH p.procedureOutputField " +     // no alias here!
    	            "INNER JOIN p.procedureOutputField.procedureOutputFieldDisplay fd " +  // collection join with alias
    	            "INNER JOIN fd.procedureType pt " +
    	            "WHERE p.procedureId = :procedureId " +
    	            "AND p.iteration = :iteration " +
    	            "AND pt.procedureTypeId = :procedureTypeId " +
    	            "AND fd.isIteration = true " +
    	            "ORDER BY fd.displayOrder"
    	),
    @NamedQuery(
        name = "findAllOutputValuesForProcedureIteration",
        query = "SELECT p FROM ProcedureOutputValue p " +
                "INNER JOIN FETCH p.procedureOutputField " +
                "WHERE p.procedureId = :procedureId " +
                "AND p.iteration = :iteration"
    )
})


public class ProcedureOutputValue {

	@Id
	@SequenceGenerator(
		    name = "procedureOutputValue_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "procedureOutputValue_gen"
		)
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

	@Override
	public String toString() {
		return "ProcedureOutputValue [procedureOutputValueId=" + procedureOutputValueId + ", procedureId=" + procedureId
				+ ", iteration=" + iteration + ", data=" + data + ", procedureOutputField=" + procedureOutputField
				+ "]";
	}




}
