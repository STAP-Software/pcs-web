package org.tmt.aps.peas.lang.interop; 
public class Jsum
{
	public native void sum(RetVal retVal, float x, float y, float z[] );
	static { System.loadLibrary("sum"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsum(RetVal retVal, float x, float y ) {
		// Output variable definitions
		float z_outArray[] = new float[1];
		// Call native method
		sum(retVal, x, y, z_outArray );
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = z_outArray[0];
		return out;
	}
}