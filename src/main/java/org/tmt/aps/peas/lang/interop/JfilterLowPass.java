package org.tmt.aps.peas.lang.interop; 
public class JfilterLowPass
{
	public native void filterLowPass(RetVal retVal, float array_in[], int array_in_size_1, int array_in_size_2, float array_out[], int array_out_size_1, int array_out_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfilterLowPass(RetVal retVal, float array_in[][], float array_out[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int array_in_len1 = array_in.length;
		int array_in_len2 = array_in[0].length;
		float[] array_in_collapse = new float[array_in_len1 * array_in_len2];
		int array_out_len1 = array_out.length;
		int array_out_len2 = array_out[0].length;
		float[] array_out_collapse = new float[array_out_len1 * array_out_len2];
		// collapse array to one dimension
		for (int i=0; i<array_in_len1; i++) { 
			for (int j=0; j<array_in_len2; j++) { 
				array_in_collapse[i*array_in_len2 + j] = array_in[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<array_out_len1; i++) { 
			for (int j=0; j<array_out_len2; j++) { 
				array_out_collapse[i*array_out_len2 + j] = array_out[i][j]; 
			} 
		} 
		// Call native method
		filterLowPass(retVal, array_in_collapse,array_in_len1,array_in_len2,array_out_collapse,array_out_len1,array_out_len2);
		// expand array to two dimensions
		for (int i=0; i<array_in_len1; i++) { 
			for (int j=0; j<array_in_len2; j++) { 
				array_in[i][j] = array_in_collapse[i*array_in_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<array_out_len1; i++) { 
			for (int j=0; j<array_out_len2; j++) { 
				array_out[i][j] = array_out_collapse[i*array_out_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}