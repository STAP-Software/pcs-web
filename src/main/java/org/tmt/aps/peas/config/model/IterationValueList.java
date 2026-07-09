package org.tmt.aps.peas.config.model;

import java.lang.reflect.Method;
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
	
	public String findEntityLabelValue(int iteration, String accessName) throws Exception {
		
		String labelFieldName = getIterationValue(iteration).getIterableEntity(accessName).getLabelFieldName();
		
		Object object = getIterationValue(iteration).getIterableEntity(accessName);
		
		Class<? extends Object> clazz = object.getClass();
		
		String methodName = "get" + Character.toUpperCase(labelFieldName.charAt(0)) + labelFieldName.substring(1);
		
		Method method = clazz.getMethod(methodName);
		
		return method.invoke(object).toString();
		

	}
}
