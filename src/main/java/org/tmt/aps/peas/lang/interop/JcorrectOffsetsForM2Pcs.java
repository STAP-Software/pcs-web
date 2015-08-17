package org.tmt.aps.peas.lang.interop; 
public class JcorrectOffsetsForM2Pcs
{
	public native void correctOffsetsForM2Pcs(RetVal retVal, float a20, float a3m1, float a3p1, float m1_radius, float offsets[], int offsets_size_1, int offsets_size_2, float f_cm2, float x_lens_2[], int x_lens_2_size_1, int x_lens_2_size_2, float y_lens_2[], int y_lens_2_size_1, int y_lens_2_size_2, int row_flag_det[], int row_flag_det_size_1, float b_pcs_cm2[], int b_pcs_cm2_size_1, float xtt_out_pcs[], int xtt_out_pcs_size_1, float ytt_out_pcs[], int ytt_out_pcs_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcorrectOffsetsForM2Pcs(RetVal retVal, float a20, float a3m1, float a3p1, float m1_radius, float offsets[][], float f_cm2, float x_lens_2[][], float y_lens_2[][], int row_flag_det[], float b_pcs_cm2[], float xtt_out_pcs[], float ytt_out_pcs[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int offsets_len1 = offsets.length;
		int offsets_len2 = offsets[0].length;
		float[] offsets_collapse = new float[offsets_len1 * offsets_len2];
		int x_lens_2_len1 = x_lens_2.length;
		int x_lens_2_len2 = x_lens_2[0].length;
		float[] x_lens_2_collapse = new float[x_lens_2_len1 * x_lens_2_len2];
		int y_lens_2_len1 = y_lens_2.length;
		int y_lens_2_len2 = y_lens_2[0].length;
		float[] y_lens_2_collapse = new float[y_lens_2_len1 * y_lens_2_len2];
		int row_flag_det_len1 = row_flag_det.length;
		int b_pcs_cm2_len1 = b_pcs_cm2.length;
		int xtt_out_pcs_len1 = xtt_out_pcs.length;
		int ytt_out_pcs_len1 = ytt_out_pcs.length;
		// collapse array to one dimension
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets_collapse[i*offsets_len2 + j] = offsets[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<x_lens_2_len1; i++) { 
			for (int j=0; j<x_lens_2_len2; j++) { 
				x_lens_2_collapse[i*x_lens_2_len2 + j] = x_lens_2[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<y_lens_2_len1; i++) { 
			for (int j=0; j<y_lens_2_len2; j++) { 
				y_lens_2_collapse[i*y_lens_2_len2 + j] = y_lens_2[i][j]; 
			} 
		} 
		// Call native method
		correctOffsetsForM2Pcs(retVal, a20,a3m1,a3p1,m1_radius,offsets_collapse,offsets_len1,offsets_len2,f_cm2,x_lens_2_collapse,x_lens_2_len1,x_lens_2_len2,y_lens_2_collapse,y_lens_2_len1,y_lens_2_len2,row_flag_det,row_flag_det_len1,b_pcs_cm2,b_pcs_cm2_len1,xtt_out_pcs,xtt_out_pcs_len1,ytt_out_pcs,ytt_out_pcs_len1);
		// expand array to two dimensions
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets[i][j] = offsets_collapse[i*offsets_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<x_lens_2_len1; i++) { 
			for (int j=0; j<x_lens_2_len2; j++) { 
				x_lens_2[i][j] = x_lens_2_collapse[i*x_lens_2_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<y_lens_2_len1; i++) { 
			for (int j=0; j<y_lens_2_len2; j++) { 
				y_lens_2[i][j] = y_lens_2_collapse[i*y_lens_2_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}