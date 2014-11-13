package org.tmt.aps.peas.lang.interop; 
public class JgcSort2
{
	public native void gcSort2(RetVal retVal, float arr[], int arr_size_1, float brr[], int brr_size_1, float arrOutput[], int arrOutput_size_1, float brrOutput[], int brrOutput_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgcSort2(RetVal retVal, float arr[], float brr[], float arrOutput[], float brrOutput[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int arr_len1 = arr.length;
		int brr_len1 = brr.length;
		int arrOutput_len1 = arrOutput.length;
		int brrOutput_len1 = brrOutput.length;
		// Call native method
		gcSort2(retVal, arr,arr_len1,brr,brr_len1,arrOutput,arrOutput_len1,brrOutput,brrOutput_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}