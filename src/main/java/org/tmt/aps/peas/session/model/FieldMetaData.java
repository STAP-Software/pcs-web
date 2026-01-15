/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Database metadata Entity class representing a row in the FieldMetaData table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "FieldMetaData")
@NamedQueries({
	@NamedQuery(name = "findAll", query = "SELECT o from FieldMetaData o" )
})
public class FieldMetaData implements FieldDescriptor {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long fieldMetaDataId;
	String tableName;
	
	@Column(name="columnname")
	String fieldName;
	
	int dataType;
	int dimension1;
	int dimension2;
	String units;
	String description;
	String displayLabel;
	String displayFormat;
	
	
	public Long getFieldMetaDataId() {
		return fieldMetaDataId;
	}
	public void setFieldMetaDataId(Long fieldMetaDataId) {
		this.fieldMetaDataId = fieldMetaDataId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	

	
}
