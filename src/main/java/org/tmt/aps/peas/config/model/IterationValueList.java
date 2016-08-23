package org.tmt.aps.peas.config.model;

import java.util.List;

public class IterationValueList {

	List<IterationValue> iterationValueList;
	
	public IterationValueList(List<IterationValue> iterationValueList) {
		this.iterationValueList = iterationValueList;
	}
	
	IterationValue getIterationValue(int index) {
		return iterationValueList.get(index);
	}
	
	public String getDisplayString() {
		StringBuffer buf = new StringBuffer();
		for (IterationValue iterationValue : iterationValueList) {
			buf.append(iterationValue.getDisplayString() + ", ");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	
}
