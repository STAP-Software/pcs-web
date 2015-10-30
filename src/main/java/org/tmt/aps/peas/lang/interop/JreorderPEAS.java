package org.tmt.aps.peas.lang.interop; 
public class JreorderPEAS
{
	public native void reorderPEAS(RetVal retVal, float x_in[], int x_in_size_1, int n123, float x_out[], int x_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jreorderPEAS(RetVal retVal, float x_in[], int n123, float x_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_in_len1 = x_in.length;
		int x_out_len1 = x_out.length;
		// Call native method
		reorderPEAS(retVal, x_in,x_in_len1,n123,x_out,x_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}