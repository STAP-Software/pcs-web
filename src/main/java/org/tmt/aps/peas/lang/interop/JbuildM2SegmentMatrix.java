package org.tmt.aps.peas.lang.interop; 
public class JbuildM2SegmentMatrix
{
	public native void buildM2SegmentMatrix(RetVal retVal, float x_subap[], int x_subap_size_1, float y_subap[], int y_subap_size_1, float ahex_mm, float segment_matrix[], int segment_matrix_size_1, int segment_matrix_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbuildM2SegmentMatrix(RetVal retVal, float x_subap[], float y_subap[], float ahex_mm, float segment_matrix[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_subap_len1 = x_subap.length;
		int y_subap_len1 = y_subap.length;
		int segment_matrix_len1 = segment_matrix.length;
		int segment_matrix_len2 = segment_matrix[0].length;
		float[] segment_matrix_collapse = new float[segment_matrix_len1 * segment_matrix_len2];
		// collapse array to one dimension
		for (int i=0; i<segment_matrix_len1; i++) { 
			for (int j=0; j<segment_matrix_len2; j++) { 
				segment_matrix_collapse[i*segment_matrix_len2 + j] = segment_matrix[i][j]; 
			} 
		} 
		// Call native method
		buildM2SegmentMatrix(retVal, x_subap,x_subap_len1,y_subap,y_subap_len1,ahex_mm,segment_matrix_collapse,segment_matrix_len1,segment_matrix_len2);
		// expand array to two dimensions
		for (int i=0; i<segment_matrix_len1; i++) { 
			for (int j=0; j<segment_matrix_len2; j++) { 
				segment_matrix[i][j] = segment_matrix_collapse[i*segment_matrix_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}