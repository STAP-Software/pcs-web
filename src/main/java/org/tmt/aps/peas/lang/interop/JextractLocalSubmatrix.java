package org.tmt.aps.peas.lang.interop; 
public class JextractLocalSubmatrix
{
	public native void extractLocalSubmatrix(RetVal retVal, float a_master[], int a_master_size_1, int a_master_size_2, int row_flag_calc[], int row_flag_calc_size_1, int nsegment, int nlenslet, float a_local[], int a_local_size_1, int a_local_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jextractLocalSubmatrix(RetVal retVal, float a_master[][], int row_flag_calc[], int nsegment, int nlenslet, float a_local[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int a_master_len1 = a_master.length;
		int a_master_len2 = a_master[0].length;
		float[] a_master_collapse = new float[a_master_len1 * a_master_len2];
		int row_flag_calc_len1 = row_flag_calc.length;
		int a_local_len1 = a_local.length;
		int a_local_len2 = a_local[0].length;
		float[] a_local_collapse = new float[a_local_len1 * a_local_len2];
		// collapse array to one dimension
		for (int i=0; i<a_master_len1; i++) { 
			for (int j=0; j<a_master_len2; j++) { 
				a_master_collapse[i*a_master_len2 + j] = a_master[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<a_local_len1; i++) { 
			for (int j=0; j<a_local_len2; j++) { 
				a_local_collapse[i*a_local_len2 + j] = a_local[i][j]; 
			} 
		} 
		// Call native method
		extractLocalSubmatrix(retVal, a_master_collapse,a_master_len1,a_master_len2,row_flag_calc,row_flag_calc_len1,nsegment,nlenslet,a_local_collapse,a_local_len1,a_local_len2);
		// expand array to two dimensions
		for (int i=0; i<a_master_len1; i++) { 
			for (int j=0; j<a_master_len2; j++) { 
				a_master[i][j] = a_master_collapse[i*a_master_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<a_local_len1; i++) { 
			for (int j=0; j<a_local_len2; j++) { 
				a_local[i][j] = a_local_collapse[i*a_local_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}