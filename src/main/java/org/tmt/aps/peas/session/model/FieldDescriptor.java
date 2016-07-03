package org.tmt.aps.peas.session.model;

/**
 * Field decriptor interface used to describe a field and as input data to encoding/decoding operations for field values
 * @author smichaels
 *
 */
public interface FieldDescriptor {
	
	public String getFieldName();
		
	public int getDataType();

	public int getDimension1();
		
	public int getDimension2();
	
	public String getUnits();
	
	public String getDescription();
	
	public String getDisplayLabel();
	
	public String getDisplayFormat();
	
	public boolean isArray();
	
	public boolean isOneDimensional();
	
	public boolean isScalar();
	
	
}
