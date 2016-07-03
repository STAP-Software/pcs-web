/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.model;

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
import javax.persistence.Transient;

import org.tmt.aps.peas.common.Utils;

/**
 * Database metadata Entity class representing a row in the FrameFieldDisplay table
 * These fields are the frame/centroid related fields that are used in reports, yet not part of the procedure output
 * An additional Transient field for the field value is supplied to also use this class to populate the report
 * @author smichaels
 *
 */
@Entity
@Table(name = "FrameFieldDisplay")
@NamedQueries({
	@NamedQuery(name = "findAllFields", query = "SELECT o from FrameFieldDisplay o INNER JOIN FETCH o.fieldMetaData ORDER BY o.frameFieldDisplayId" )
})
public class FrameFieldDisplay {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long frameFieldDisplayId;
	 
	String className;
	String fieldName;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "fieldMetaDataId")
	FieldMetaData fieldMetaData;


	@Transient
	String value;




	public Long getFrameFieldDisplayId() {
		return frameFieldDisplayId;
	}


	public void setFrameFieldDisplayId(Long frameFieldDisplayId) {
		this.frameFieldDisplayId = frameFieldDisplayId;
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


	public FieldMetaData getFieldMetaData() {
		return fieldMetaData;
	}


	public void setFieldMetaData(FieldMetaData fieldMetaData) {
		this.fieldMetaData = fieldMetaData;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Convienience method that calls {@link Utils#reformatData(String, FieldDescriptor)} to format the value string using the fieldMetaData description of the data.
	 * @return the value data formatted for use in frame reports
	 */
	public String getDataFormatted() {
		return Utils.reformatData(value, fieldMetaData);
	}

	
}
