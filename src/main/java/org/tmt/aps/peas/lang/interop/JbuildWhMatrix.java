package org.tmt.aps.peas.lang.interop; 
public class JbuildWhMatrix
{
	public native void buildWhMatrix(RetVal retVal, float x_subap[], int x_subap_size_1, float y_subap[], int y_subap_size_1, float ahex_mm, float wh_matrix[], int wh_matrix_size_1, int wh_matrix_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbuildWhMatrix(RetVal retVal, float x_subap[], float y_subap[], float ahex_mm, float wh_matrix[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_subap_len1 = x_subap.length;
		int y_subap_len1 = y_subap.length;
		int wh_matrix_len1 = wh_matrix.length;
		int wh_matrix_len2 = wh_matrix[0].length;
		float[] wh_matrix_collapse = new float[wh_matrix_len1 * wh_matrix_len2];
		// collapse array to one dimension
		for (int i=0; i<wh_matrix_len1; i++) { 
			for (int j=0; j<wh_matrix_len2; j++) { 
				wh_matrix_collapse[i*wh_matrix_len2 + j] = wh_matrix[i][j]; 
			} 
		} 
		// Call native method
		buildWhMatrix(retVal, x_subap,x_subap_len1,y_subap,y_subap_len1,ahex_mm,wh_matrix_collapse,wh_matrix_len1,wh_matrix_len2);
		// expand array to two dimensions
		for (int i=0; i<wh_matrix_len1; i++) { 
			for (int j=0; j<wh_matrix_len2; j++) { 
				wh_matrix[i][j] = wh_matrix_collapse[i*wh_matrix_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}