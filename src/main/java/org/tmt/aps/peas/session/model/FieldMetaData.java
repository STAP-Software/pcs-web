/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "FieldMetaData")
@NamedQueries({
	@NamedQuery(name = "findAll", query = "SELECT o from FieldMetaData o" )
})
public class FieldMetaData {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long fieldMetaDataId;
	String tableName;
	String columnName;
	int dataType;
	int dimension1;
	int dimension2;
	String units;
	String description;
	String displayLabel;
	
	
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
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
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
	

	

	
}
