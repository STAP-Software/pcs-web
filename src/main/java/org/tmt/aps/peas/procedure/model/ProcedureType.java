/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "ProcedureType")
@NamedQueries({
	@NamedQuery(name = "findAllProcedureTypes", query = "SELECT p from ProcedureType p" )
})
public class ProcedureType {

	public static final Long PROCEDURE_TYPE_ID_PASSIVE_TILT = new Long(1);
	public static final Long PROCEDURE_TYPE_ID_FINE_SCREEN = new Long(2);
	public static final Long PROCEDURE_TYPE_ID_PHASING = new Long(3);
	public static final Long PROCEDURE_TYPE_ID_SUFS = new Long(5);
	public static final Long PROCEDURE_TYPE_ID_PUPIL_REGISTRATION = new Long(6);
	public static final Long PROCEDURE_TYPE_ID_CENTER_TELESCOPE = new Long(7);
	public static final Long PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM = new Long(8);
	public static final Long PROCEDURE_TYPE_ID_CREATE_FIRST_REFERENCE_BEAM = new Long(9);
	
	@Id
	private Long procedureTypeId;
	
	@Column(nullable=false, length=100)
	private String procedureTypeName;
	
	@Column(nullable=false, length=10)
	private String procedureTypeCd;
	
	
	public Long getProcedureTypeId() {
		return procedureTypeId;
	}
	public void setProcedureTypeId(Long procedureTypeId) {
		this.procedureTypeId = procedureTypeId;
	}
	public String getProcedureTypeName() {
		return procedureTypeName;
	}
	public void setProcedureTypeName(String procedureTypeName) {
		this.procedureTypeName = procedureTypeName;
	}
	public String getProcedureTypeCd() {
		return procedureTypeCd;
	}
	public void setProcedureTypeCd(String procedureTypeCd) {
		this.procedureTypeCd = procedureTypeCd;
	}
	
	
	
	
}
