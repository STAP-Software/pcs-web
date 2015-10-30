package org.tmt.aps.peas.lang.interop; 
public class JhowToCenterSubap
{
	public native void howToCenterSubap(RetVal retVal, float hw_microns, float delta, int ncenter[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jhowToCenterSubap(RetVal retVal, float hw_microns, float delta ) {
		// Output variable definitions
		int ncenter_outArray[] = new int[1];
		// Deal with Array Lengths
		// Call native method
		howToCenterSubap(retVal, hw_microns,delta,ncenter_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = ncenter_outArray[0];
		return out;
	}
}