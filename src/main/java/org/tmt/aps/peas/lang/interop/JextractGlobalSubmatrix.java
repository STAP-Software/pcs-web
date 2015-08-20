package org.tmt.aps.peas.lang.interop; 
public class JextractGlobalSubmatrix
{
	public native void extractGlobalSubmatrix(RetVal retVal, float a_complete[], int a_complete_size_1, int a_complete_size_2, int row_flag_det[], int row_flag_det_size_1, float a_global[], int a_global_size_1, int a_global_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jextractGlobalSubmatrix(RetVal retVal, float a_complete[][], int row_flag_det[], float a_global[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int a_complete_len1 = a_complete.length;
		int a_complete_len2 = a_complete[0].length;
		float[] a_complete_collapse = new float[a_complete_len1 * a_complete_len2];
		int row_flag_det_len1 = row_flag_det.length;
		int a_global_len1 = a_global.length;
		int a_global_len2 = a_global[0].length;
		float[] a_global_collapse = new float[a_global_len1 * a_global_len2];
		// collapse array to one dimension
		for (int i=0; i<a_complete_len1; i++) { 
			for (int j=0; j<a_complete_len2; j++) { 
				a_complete_collapse[i*a_complete_len2 + j] = a_complete[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<a_global_len1; i++) { 
			for (int j=0; j<a_global_len2; j++) { 
				a_global_collapse[i*a_global_len2 + j] = a_global[i][j]; 
			} 
		} 
		// Call native method
		extractGlobalSubmatrix(retVal, a_complete_collapse,a_complete_len1,a_complete_len2,row_flag_det,row_flag_det_len1,a_global_collapse,a_global_len1,a_global_len2);
		// expand array to two dimensions
		for (int i=0; i<a_complete_len1; i++) { 
			for (int j=0; j<a_complete_len2; j++) { 
				a_complete[i][j] = a_complete_collapse[i*a_complete_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<a_global_len1; i++) { 
			for (int j=0; j<a_global_len2; j++) { 
				a_global[i][j] = a_global_collapse[i*a_global_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}