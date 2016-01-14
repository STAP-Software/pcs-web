package org.tmt.aps.peas.lang.interop; 
public class JsufsZerToOff
{
	public native void sufsZerToOff(RetVal retVal, float wh_matrix[], int wh_matrix_size_1, int wh_matrix_size_2, float czern[], int czern_size_1, float b[], int b_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsufsZerToOff(RetVal retVal, float wh_matrix[][], float czern[], float b[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int wh_matrix_len1 = wh_matrix.length;
		int wh_matrix_len2 = wh_matrix[0].length;
		float[] wh_matrix_collapse = new float[wh_matrix_len1 * wh_matrix_len2];
		int czern_len1 = czern.length;
		int b_len1 = b.length;
		// collapse array to one dimension
		for (int i=0; i<wh_matrix_len1; i++) { 
			for (int j=0; j<wh_matrix_len2; j++) { 
				wh_matrix_collapse[i*wh_matrix_len2 + j] = wh_matrix[i][j]; 
			} 
		} 
		// Call native method
		sufsZerToOff(retVal, wh_matrix_collapse,wh_matrix_len1,wh_matrix_len2,czern,czern_len1,b,b_len1);
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