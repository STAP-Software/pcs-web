package org.tmt.aps.peas.lang.interop; 
public class JcompressB
{
	public native void compressB(RetVal retVal, float b_in[], int b_in_size_1, int row_flag[], int row_flag_size_1, float b_out[], int b_out_size_1, int nprime[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcompressB(RetVal retVal, float b_in[], int row_flag[], float b_out[] ) {
		// Output variable definitions
		int nprime_outArray[] = new int[1];
		// Deal with Array Lengths
		int b_in_len1 = b_in.length;
		int row_flag_len1 = row_flag.length;
		int b_out_len1 = b_out.length;
		// Call native method
		compressB(retVal, b_in,b_in_len1,row_flag,row_flag_len1,b_out,b_out_len1,nprime_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = nprime_outArray[0];
		return out;
	}
}