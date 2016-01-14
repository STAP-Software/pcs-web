package org.tmt.aps.peas.lang.interop; 
public class JwhFittingFactor
{
	public native void whFittingFactor(RetVal retVal, float wh_matrix[], int wh_matrix_size_1, int wh_matrix_size_2, float czern_calc[], int czern_calc_size_1, float b[], int b_size_1, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float wh_factor[], float b_th[], int b_th_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jwhFittingFactor(RetVal retVal, float wh_matrix[][], float czern_calc[], float b[], int row_flag[], int col_flag[], float b_th[] ) {
		// Output variable definitions
		float wh_factor_outArray[] = new float[1];
		// Deal with Array Lengths
		int wh_matrix_len1 = wh_matrix.length;
		int wh_matrix_len2 = wh_matrix[0].length;
		float[] wh_matrix_collapse = new float[wh_matrix_len1 * wh_matrix_len2];
		int czern_calc_len1 = czern_calc.length;
		int b_len1 = b.length;
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int b_th_len1 = b_th.length;
		// collapse array to one dimension
		for (int i=0; i<wh_matrix_len1; i++) { 
			for (int j=0; j<wh_matrix_len2; j++) { 
				wh_matrix_collapse[i*wh_matrix_len2 + j] = wh_matrix[i][j]; 
			} 
		} 
		// Call native method
		whFittingFactor(retVal, wh_matrix_collapse,wh_matrix_len1,wh_matrix_len2,czern_calc,czern_calc_len1,b,b_len1,row_flag,row_flag_len1,col_flag,col_flag_len1,wh_factor_outArray,b_th,b_th_len1);
		// expand array to two dimensions
		for (int i=0; i<wh_matrix_len1; i++) { 
			for (int j=0; j<wh_matrix_len2; j++) { 
				wh_matrix[i][j] = wh_matrix_collapse[i*wh_matrix_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = wh_factor_outArray[0];
		return out;
	}
}