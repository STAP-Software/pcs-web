/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.model;

import java.text.MessageFormat;

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

import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.model.Constant;

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
	
	
	public String getDataFormatted() {
		return Utils.reformatData(value, fieldMetaData);
	}

	
}
