package org.tmt.aps.peas.lang.interop; 
public class Jnormalize
{
	public native void normalize(RetVal retVal, double u_in[], int u_in_size_1, double u_out[], int u_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnormalize(RetVal retVal, double u_in[], double u_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int u_in_len1 = u_in.length;
		int u_out_len1 = u_out.length;
		// Call native method
		normalize(retVal, u_in,u_in_len1,u_out,u_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}