package org.tmt.aps.peas.lang.interop; 
public class JgaussCalc
{
	public native void gaussCalc(RetVal retVal, float x[], int x_size_1, float y[], int y_size_1, float x0, float sigma, float chisq[], float a[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgaussCalc(RetVal retVal, float x[], float y[], float x0, float sigma ) {
		// Output variable definitions
		float chisq_outArray[] = new float[1];
		float a_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_len1 = x.length;
		int y_len1 = y.length;
		// Call native method
		gaussCalc(retVal, x,x_len1,y,y_len1,x0,sigma,chisq_outArray,a_outArray);
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = chisq_outArray[0];
		out[1] = a_outArray[0];
		return out;
	}
}