package org.tmt.aps.peas.lang.interop; 
public class JcompressA
{
	public native void compressA(RetVal retVal, float matrix_in[], int matrix_in_size_1, int matrix_in_size_2, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float matrix_out[], int matrix_out_size_1, int matrix_out_size_2, int nrow[], int ncol[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcompressA(RetVal retVal, float matrix_in[][], int row_flag[], int col_flag[], float matrix_out[][] ) {
		// Output variable definitions
		int nrow_outArray[] = new int[1];
		int ncol_outArray[] = new int[1];
		// Deal with Array Lengths
		int matrix_in_len1 = matrix_in.length;
		int matrix_in_len2 = matrix_in[0].length;
		float[] matrix_in_collapse = new float[matrix_in_len1 * matrix_in_len2];
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int matrix_out_len1 = matrix_out.length;
		int matrix_out_len2 = matrix_out[0].length;
		float[] matrix_out_collapse = new float[matrix_out_len1 * matrix_out_len2];
		// collapse array to one dimension
		for (int i=0; i<matrix_in_len1; i++) { 
			for (int j=0; j<matrix_in_len2; j++) { 
				matrix_in_collapse[i*matrix_in_len2 + j] = matrix_in[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<matrix_out_len1; i++) { 
			for (int j=0; j<matrix_out_len2; j++) { 
				matrix_out_collapse[i*matrix_out_len2 + j] = matrix_out[i][j]; 
			} 
		} 
		// Call native method
		compressA(retVal, matrix_in_collapse,matrix_in_len1,matrix_in_len2,row_flag,row_flag_len1,col_flag,col_flag_len1,matrix_out_collapse,matrix_out_len1,matrix_out_len2,nrow_outArray,ncol_outArray);
		// expand array to two dimensions
		for (int i=0; i<matrix_in_len1; i++) { 
			for (int j=0; j<matrix_in_len2; j++) { 
				matrix_in[i][j] = matrix_in_collapse[i*matrix_in_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<matrix_out_len1; i++) { 
			for (int j=0; j<matrix_out_len2; j++) { 
				matrix_out[i][j] = matrix_out_collapse[i*matrix_out_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = nrow_outArray[0];
		out[1] = ncol_outArray[0];
		return out;
	}
}