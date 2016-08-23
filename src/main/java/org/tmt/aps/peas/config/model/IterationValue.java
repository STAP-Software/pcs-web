package org.tmt.aps.peas.config.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationValue {

	Map<String, IterableEntity> classNameToEntity = new HashMap<String, IterableEntity>();
	List<IterableEntity> entities;
	
	public IterationValue(List<IterableEntity> entities) {
		this.entities = entities;
		for (IterableEntity entity : entities) {
			classNameToEntity.put(entity.getClassName(), entity);
		}
	}
	
	public IterableEntity getIterableEntity(String className) {
	
		return classNameToEntity.get(className);
	}
	
	public String getDisplayString() {
		// We display only the label for the first entity class
		
		try {
			String labelValueField = entities.get(0).getLabelFieldName();
			
			String getter = "get" + Character.toUpperCase(labelValueField.charAt(0)) + labelValueField.substring(1);
			
			Method method = entities.get(0).getClass().getMethod(getter , null);
			
			return (String)method.invoke(entities.get(0), null);
		
		} catch (Exception e) {
			e.printStackTrace();
			return "Unknown";
		}

	}
}
