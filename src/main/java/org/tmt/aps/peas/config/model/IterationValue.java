package org.tmt.aps.peas.config.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IterationValue {

	Map<String, IterableEntity> accessNameToEntity = new HashMap<String, IterableEntity>();
	List<IterableEntity> entities;
	
	public IterationValue(List<IterableEntity> entities, List<ProcedureIterationDef> iterationDefs) {
		this.entities = entities;
		int i=0;
		for (IterableEntity entity : entities) {
			accessNameToEntity.put(iterationDefs.get(i++).getIterationEntityAccessName(), entity);
		}
	}
	
	public IterableEntity getIterableEntity(String className) {
	
		return accessNameToEntity.get(className);
	}
	
	public String getDisplayString() {
		// We display only the label for the first entity class
		
		return getEntityDisplayString(entities.get(0));

	}
	
	private String getEntityDisplayString(IterableEntity entity) {
		try {
			String labelValueField = entity.getLabelFieldName();
			
			String getter = "get" + Character.toUpperCase(labelValueField.charAt(0)) + labelValueField.substring(1);
			
			Method method = entity.getClass().getMethod(getter , null);
			
			return "" + method.invoke(entity, null);
		
		} catch (Exception e) {
			e.printStackTrace();
			return "Unknown";
		}
	}
	
	public String getFullDisplayString() {
		// We display only the label for the first entity class
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for (IterableEntity entity : entities) {
			if (first) {
				buf.append(getEntityDisplayString(entity));
				first = false;
			} else {
				buf.append("(" + getEntityDisplayString(entity) + ")");
			}
		}
		//if (buf.length() > 0) {
		//	buf.deleteCharAt(buf.length()-1);
		//}
		return buf.toString();
	}

	public List<IterableEntity> getEntities() {
		return entities;
	}
}
