package org.tmt.aps.peas.session.model;

public interface FieldDescriptor {
	
	public String getFieldName();
		
	public int getDataType();

	public int getDimension1();
		
	public int getDimension2();
	
	public String getUnits();
	
	public String getDescription();
	
	public String getDisplayLabel();
	
	public boolean isArray();
	
	public boolean isOneDimensional();
	
	public boolean isScalar();
	
	
}
