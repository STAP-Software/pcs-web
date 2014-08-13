package org.tmt.aps.peas.lang.interop; 
public class Jsort
{
	public native void sort(RetVal retVal, int N, float RA_in[], int RA_in_size_1, float RA_out[], int RA_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsort(RetVal retVal, int N, float RA_in[], float RA_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int RA_in_len1 = RA_in.length;
		int RA_out_len1 = RA_out.length;
		// Call native method
		sort(retVal, N,RA_in,RA_in_len1,RA_out,RA_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}