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
	
	@Id
	private Long procedureTypeId;
	
	@Column(nullable=false, length=100)
	private String procedureTypeName;
	
	
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
	
	
	
	
}
