package org.tmt.aps.peas.lang.interop; 
public class JfixPistons
{
	public native void fixPistons(RetVal retVal, float pistonRaw[], int pistonRaw_size_1, float xActuator[], int xActuator_size_1, float yActuator[], int yActuator_size_1, float actRaw[], int actRaw_size_1, float actFixed[], int actFixed_size_1, float actRms[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfixPistons(RetVal retVal, float pistonRaw[], float xActuator[], float yActuator[], float actRaw[], float actFixed[] ) {
		// Output variable definitions
		float actRms_outArray[] = new float[1];
		// Deal with Array Lengths
		int pistonRaw_len1 = pistonRaw.length;
		int xActuator_len1 = xActuator.length;
		int yActuator_len1 = yActuator.length;
		int actRaw_len1 = actRaw.length;
		int actFixed_len1 = actFixed.length;
		// Call native method
		fixPistons(retVal, pistonRaw,pistonRaw_len1,xActuator,xActuator_len1,yActuator,yActuator_len1,actRaw,actRaw_len1,actFixed,actFixed_len1,actRms_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = actRms_outArray[0];
		return out;
	}
}