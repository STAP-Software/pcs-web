/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.tmt.aps.peas.session.model.FieldDescriptor;

/**
 * Metadata Entity representing a single row in the ProcedureOutputField table.
 * The metadata describes each procedure output field and is useful in encoding/decoding and display
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureOutputField")
@NamedQueries({
	@NamedQuery(name = "findAllOutputFieldsForClass", query = "SELECT p from ProcedureOutputField p where p.className = :className"),
	@NamedQuery(name = "findAllOutputFields", query = "SELECT p from ProcedureOutputField p")

})
public class ProcedureOutputField implements FieldDescriptor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long procedureOutputFieldId;
	
	String className;
	String fieldName;
	int dataType;
	int dimension1;
	int dimension2;
	String units;
	String description;
	String displayLabel;
	String displayFormat;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procedureOutputField")
	List<ProcedureOutputFieldDisplay> procedureOutputFieldDisplay;

	
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
	
	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
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
	
	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public boolean isArray() {
		return dimension1 > 0;
	}	
	
	public boolean isOneDimensional() {
		return dimension1 > 0 && dimension2 == 0;
	}	
	
	public boolean isScalar() {
		return dimension1 == 0;
	}

	public List<ProcedureOutputFieldDisplay> getProcedureOutputFieldDisplay() {
		return procedureOutputFieldDisplay;
	}

	public void setProcedureOutputFieldDisplay(List<ProcedureOutputFieldDisplay> procedureOutputFieldDisplay) {
		this.procedureOutputFieldDisplay = procedureOutputFieldDisplay;
	}
	


}
