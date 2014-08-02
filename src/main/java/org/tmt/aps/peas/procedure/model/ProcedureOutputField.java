/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "ProcedureOutputField")
@NamedQueries({
	@NamedQuery(name = "findAllOutputFieldsForClass", query = "SELECT p from ProcedureOutputField p where p.className = :className" )
})
public class ProcedureOutputField {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long procedureOutputFieldId;
	
	String className;
	String fieldName;
	String dataType;
	int dimension1;
	int dimension2;
	String units;
	String description;
	String displayLabel;
	
	
	
	public Long getProcedureOutputFieldId() {
		return procedureOutputFieldId;
	}
	
	public void setProcedureOutputFieldId(Long procedureOutputFieldId) {
		this.procedureOutputFieldId = procedureOutputFieldId;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getDimension1() {
		return dimension1;
	}
	
	public void setDimension1(int dimension1) {
		this.dimension1 = dimension1;
	}
	
	public int getDimension2() {
		return dimension2;
	}
	
	public void setDimension2(int dimension2) {
		this.dimension2 = dimension2;
	}
	
	public String getUnits() {
		return units;
	}
	
	public void setUnits(String units) {
		this.units = units;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDisplayLabel() {
		return displayLabel;
	}
	
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}
	
}
