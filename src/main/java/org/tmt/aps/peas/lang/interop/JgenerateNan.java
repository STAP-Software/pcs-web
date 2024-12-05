package org.tmt.aps.peas.lang.interop; 
public class JgenerateNan
{
	public native void generateNan(RetVal retVal, float nan[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgenerateNan(RetVal retVal ) {
		// Output variable definitions
		float nan_outArray[] = new float[1];
		// Deal with Array Lengths
		// Call native method
		generateNan(retVal, nan_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = nan_outArray[0];
		return out;
	}
}