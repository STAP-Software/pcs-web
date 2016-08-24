package org.tmt.aps.peas.config.model;

import java.util.List;

public class IterationValueList {

	List<IterationValue> iterationValueList;
	
	public IterationValueList(List<IterationValue> iterationValueList) {
		this.iterationValueList = iterationValueList;
	}
	
	public IterationValue getIterationValue(int index) {
		return iterationValueList.get(index);
	}
	
	public int getSize() {
		return iterationValueList.size();
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
	
	public String getFullDisplayString() {
		StringBuffer buf = new StringBuffer();
		for (IterationValue iterationValue : iterationValueList) {
			buf.append(iterationValue.getFullDisplayString() + ", ");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	
}
