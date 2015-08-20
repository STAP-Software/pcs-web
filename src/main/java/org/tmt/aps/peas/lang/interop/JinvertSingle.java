package org.tmt.aps.peas.lang.interop; 
public class JinvertSingle
{
	public native void invertSingle(RetVal retVal, int nrow, float A[], int A_size_1, int A_size_2, float b[], int b_size_1, float ww[], int ww_size_1, float act_calc[], int act_calc_size_1, float emult_j[], int emult_j_size_1, float emult_tot[], float a_out[], int a_out_size_1, int a_out_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jinvertSingle(RetVal retVal, int nrow, float A[][], float b[], float ww[], float act_calc[], float emult_j[], float a_out[][] ) {
		// Output variable definitions
		float emult_tot_outArray[] = new float[1];
		// Deal with Array Lengths
		int A_len1 = A.length;
		int A_len2 = A[0].length;
		float[] A_collapse = new float[A_len1 * A_len2];
		int b_len1 = b.length;
		int ww_len1 = ww.length;
		int act_calc_len1 = act_calc.length;
		int emult_j_len1 = emult_j.length;
		int a_out_len1 = a_out.length;
		int a_out_len2 = a_out[0].length;
		float[] a_out_collapse = new float[a_out_len1 * a_out_len2];
		// collapse array to one dimension
		for (int i=0; i<A_len1; i++) { 
			for (int j=0; j<A_len2; j++) { 
				A_collapse[i*A_len2 + j] = A[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<a_out_len1; i++) { 
			for (int j=0; j<a_out_len2; j++) { 
				a_out_collapse[i*a_out_len2 + j] = a_out[i][j]; 
			} 
		} 
		// Call native method
		invertSingle(retVal, nrow,A_collapse,A_len1,A_len2,b,b_len1,ww,ww_len1,act_calc,act_calc_len1,emult_j,emult_j_len1,emult_tot_outArray,a_out_collapse,a_out_len1,a_out_len2);
		// expand array to two dimensions
		for (int i=0; i<A_len1; i++) { 
			for (int j=0; j<A_len2; j++) { 
				A[i][j] = A_collapse[i*A_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<a_out_len1; i++) { 
			for (int j=0; j<a_out_len2; j++) { 
				a_out[i][j] = a_out_collapse[i*a_out_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = emult_tot_outArray[0];
		return out;
	}
}