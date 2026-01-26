/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Metadata Entity class representing a row in the ProcedureType table
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureType")
@NamedQueries({
	@NamedQuery(name = "findAllProcedureTypes", query = "SELECT p from ProcedureType p " ),
	@NamedQuery(name = "findProcedureType", query = "SELECT p from ProcedureType p "
			+ "WHERE p.procedureTypeId = :procedureTypeId" )
})
public class ProcedureType {

	public static final Long PROCEDURE_TYPE_ID_PASSIVE_TILT = Long.valueOf(1);
	public static final Long PROCEDURE_TYPE_ID_FINE_SCREEN = Long.valueOf(2);
	public static final Long PROCEDURE_TYPE_ID_COARSE_PHASING = Long.valueOf(3);
	public static final Long PROCEDURE_TYPE_ID_NARROW_BAND_PHASING = Long.valueOf(4);
	public static final Long PROCEDURE_TYPE_ID_SUFS = Long.valueOf(5);
	public static final Long PROCEDURE_TYPE_ID_PUPIL_REGISTRATION = Long.valueOf(6);
	public static final Long PROCEDURE_TYPE_ID_CENTER_TELESCOPE = Long.valueOf(7);
	public static final Long PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP = Long.valueOf(8);  // if this changes, RefBeamMap JPA Named query needs to change too
	public static final Long PROCEDURE_TYPE_ID_CREATE_FIRST_REFERENCE_BEAM_MAP = Long.valueOf(9);
	
	@Id
	private Long procedureTypeId;
	
	@Column(nullable=false, length=100)
	private String procedureTypeName;
	
	@Column(nullable=false, length=100)
	private String procedureOutputClassName;
	
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
	
	public String getProcedureOutputClassName() {
		return procedureOutputClassName;
	}
	public void setProcedureOutputClassName(String procedureOutputClassName) {
		this.procedureOutputClassName = procedureOutputClassName;
	}
	
	
	public boolean isCreateRefMap() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP);
	}
	public boolean isPassiveTilt() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_PASSIVE_TILT);
	}
	public boolean isCenterTelescope() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_CENTER_TELESCOPE);
	}
	public boolean isFineScreen() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_FINE_SCREEN);
	}
	public boolean isPupilRegistration() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_PUPIL_REGISTRATION);	
	}
	public boolean isCoarsePhasing() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_COARSE_PHASING);	
	}
	public boolean isNarrowBandPhasing() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_NARROW_BAND_PHASING);	
	}
	public boolean isSufs() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_SUFS);	
	}
	

	public String toString() {
		return procedureTypeName;
	}
	
	
	
	
}
