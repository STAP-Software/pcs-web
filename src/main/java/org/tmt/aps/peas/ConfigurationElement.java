/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.util.List;

import org.apache.log4j.Logger;

public class ConfigurationElement {

	public static int ELEMENT_TYPE_TEXT_INPUT = 0;
	public static int ELEMENT_TYPE_TEXT_AREA = 1;
	public static int ELEMENT_TYPE_SELECTLIST = 2;
	public static int ELEMENT_TYPE_EDITABLE_SELECTLIST = 3;
	public static int ELEMENT_TYPE_CHECKBOX = 4;
	
	
	private String label;
	private String valueName;
	private String value;
	private int elementType;
	private int elementSize;
	private List<ConfigurationOption> selectList;
	
	public ConfigurationElement(String label, String valueName, int elementType, int elementSize, List<ConfigurationOption> selectList) {
		this.label = label;
		this.valueName = valueName;
		this.elementType = elementType;
		this.elementSize = elementSize;
		this.selectList = selectList;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	public int getElementType() {
		return elementType;
	}
	public void setElementType(int elementType) {
		this.elementType = elementType;
	}
	public int getElementSize() {
		return elementSize;
	}
	public void setElementSize(int elementSize) {
		this.elementSize = elementSize;
	}
	public List<ConfigurationOption> getSelectList() {
		return selectList;
	}
	public void setSelectList(List<ConfigurationOption> selectList) {
		this.selectList = selectList;
	}
	
	// display related methods
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isTypeTextInput() {
		return elementType == ELEMENT_TYPE_TEXT_INPUT;
	}
	public boolean isTypeTextArea() {
		return elementType == ELEMENT_TYPE_TEXT_AREA;
	}
	public boolean isTypeSelectList() {
		return elementType == ELEMENT_TYPE_SELECTLIST;
	}
	public boolean isTypeEditableSelectList() {
		return elementType == ELEMENT_TYPE_EDITABLE_SELECTLIST;
	}
	public boolean isTypeCheckbox() {
		return elementType == ELEMENT_TYPE_CHECKBOX;
	}


	
	
}
