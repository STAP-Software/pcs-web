package org.tmt.aps.peas.lang.interop; 
public class JuncompressB
{
	public native void uncompressB(RetVal retVal, float bCompressed[], int bCompressed_size_1, int rowFlag[], int rowFlag_size_1, float bOut[], int bOut_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] juncompressB(RetVal retVal, float bCompressed[], int rowFlag[], float bOut[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int bCompressed_len1 = bCompressed.length;
		int rowFlag_len1 = rowFlag.length;
		int bOut_len1 = bOut.length;
		// Call native method
		uncompressB(retVal, bCompressed,bCompressed_len1,rowFlag,rowFlag_len1,bOut,bOut_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}