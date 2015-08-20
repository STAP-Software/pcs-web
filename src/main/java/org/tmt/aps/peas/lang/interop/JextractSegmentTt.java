package org.tmt.aps.peas.lang.interop; 
public class JextractSegmentTt
{
	public native void extractSegmentTt(RetVal retVal, float x_global[], int x_global_size_1, float y_global[], int y_global_size_1, float a_global[], int a_global_size_1, int a_global_size_2, float act_calc[], int act_calc_size_1, int row_flag_det[], int row_flag_det_size_1, float xtt_in[], int xtt_in_size_1, float ytt_in[], int ytt_in_size_1, float xtt_out[], int xtt_out_size_1, float ytt_out[], int ytt_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jextractSegmentTt(RetVal retVal, float x_global[], float y_global[], float a_global[][], float act_calc[], int row_flag_det[], float xtt_in[], float ytt_in[], float xtt_out[], float ytt_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_global_len1 = x_global.length;
		int y_global_len1 = y_global.length;
		int a_global_len1 = a_global.length;
		int a_global_len2 = a_global[0].length;
		float[] a_global_collapse = new float[a_global_len1 * a_global_len2];
		int act_calc_len1 = act_calc.length;
		int row_flag_det_len1 = row_flag_det.length;
		int xtt_in_len1 = xtt_in.length;
		int ytt_in_len1 = ytt_in.length;
		int xtt_out_len1 = xtt_out.length;
		int ytt_out_len1 = ytt_out.length;
		// collapse array to one dimension
		for (int i=0; i<a_global_len1; i++) { 
			for (int j=0; j<a_global_len2; j++) { 
				a_global_collapse[i*a_global_len2 + j] = a_global[i][j]; 
			} 
		} 
		// Call native method
		extractSegmentTt(retVal, x_global,x_global_len1,y_global,y_global_len1,a_global_collapse,a_global_len1,a_global_len2,act_calc,act_calc_len1,row_flag_det,row_flag_det_len1,xtt_in,xtt_in_len1,ytt_in,ytt_in_len1,xtt_out,xtt_out_len1,ytt_out,ytt_out_len1);
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