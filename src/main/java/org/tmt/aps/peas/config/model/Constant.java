/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Entity representing the Constant database table.  This table contains metadata for each constant and a string data value field.
 * Data from the Constant database table is decoded and used to populate {@link PhasingConstants}, {@link PrimaryMirrorConstants}, {@link PrimaryMirrorSegmentConstants}, 
 * {@link SufsConstants} and {@link TelescopeConstants}
 * @author smichaels
 */
@Entity
@Table(name = "Constant")
@NamedQueries({ @NamedQuery(name = "findAllConstants", query = "SELECT g from Constant g") })
public class Constant {

	public static final int DATA_TYPE_INT = 0;
	public static final int DATA_TYPE_FLOAT = 1;
	public static final int DATA_TYPE_DOUBLE = 2;
	public static final int DATA_TYPE_INT_POINT = 3;  // x1,y1,x2,y1,etc - only used in array types
	public static final int DATA_TYPE_FLOAT_POINT = 4;  // x1,y1,x2,y1,etc - only used in array types
	public static final int DATA_TYPE_BOOLEAN = 5;
	public static final int DATA_TYPE_STRING = 6;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long constantId;

	String className;
	String fieldName;
	int dataType;
	int dimension1;
	int dimension2;
	String data;
	String units;
	String description;
	
	
	public Long getConstantId() {
		return constantId;
	}
	
	public void setConstantId(Long constantId) {
		this.constantId = constantId;
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
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
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