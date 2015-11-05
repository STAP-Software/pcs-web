package org.tmt.aps.peas.lang.interop; 
public class JfsOffToM2PttViaZ
{
	public native void fsOffToM2PttViaZ(RetVal retVal, float x_lens_2[], int x_lens_2_size_1, int x_lens_2_size_2, float y_lens_2[], int y_lens_2_size_1, int y_lens_2_size_2, float b_long[], int b_long_size_1, float f_corr, float focal_length_1, float focal_length_2, float d1, float ahex_mm, float bfl, float m2_tt_correction_factor, int n1, int n2, int row_flag_det[], int row_flag_det_size_1, int row_flag_calc[], int row_flag_calc_size_1, float x_tilt[], float y_tilt[], float piston[], float x_tilt_tel[], float y_tilt_tel[], float b_pcs_cm2[], int b_pcs_cm2_size_1, float xtt_out_pcs[], int xtt_out_pcs_size_1, float ytt_out_pcs[], int ytt_out_pcs_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfsOffToM2PttViaZ(RetVal retVal, float x_lens_2[][], float y_lens_2[][], float b_long[], float f_corr, float focal_length_1, float focal_length_2, float d1, float ahex_mm, float bfl, float m2_tt_correction_factor, int n1, int n2, int row_flag_det[], int row_flag_calc[], float b_pcs_cm2[], float xtt_out_pcs[], float ytt_out_pcs[] ) {
		// Output variable definitions
		float x_tilt_outArray[] = new float[1];
		float y_tilt_outArray[] = new float[1];
		float piston_outArray[] = new float[1];
		float x_tilt_tel_outArray[] = new float[1];
		float y_tilt_tel_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_lens_2_len1 = x_lens_2.length;
		int x_lens_2_len2 = x_lens_2[0].length;
		float[] x_lens_2_collapse = new float[x_lens_2_len1 * x_lens_2_len2];
		int y_lens_2_len1 = y_lens_2.length;
		int y_lens_2_len2 = y_lens_2[0].length;
		float[] y_lens_2_collapse = new float[y_lens_2_len1 * y_lens_2_len2];
		int b_long_len1 = b_long.length;
		int row_flag_det_len1 = row_flag_det.length;
		int row_flag_calc_len1 = row_flag_calc.length;
		int b_pcs_cm2_len1 = b_pcs_cm2.length;
		int xtt_out_pcs_len1 = xtt_out_pcs.length;
		int ytt_out_pcs_len1 = ytt_out_pcs.length;
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
		fsOffToM2PttViaZ(retVal, x_lens_2_collapse,x_lens_2_len1,x_lens_2_len2,y_lens_2_collapse,y_lens_2_len1,y_lens_2_len2,b_long,b_long_len1,f_corr,focal_length_1,focal_length_2,d1,ahex_mm,bfl,m2_tt_correction_factor,n1,n2,row_flag_det,row_flag_det_len1,row_flag_calc,row_flag_calc_len1,x_tilt_outArray,y_tilt_outArray,piston_outArray,x_tilt_tel_outArray,y_tilt_tel_outArray,b_pcs_cm2,b_pcs_cm2_len1,xtt_out_pcs,xtt_out_pcs_len1,ytt_out_pcs,ytt_out_pcs_len1);
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
		Object[] out = new Object[5];
		out[0] = x_tilt_outArray[0];
		out[1] = y_tilt_outArray[0];
		out[2] = piston_outArray[0];
		out[3] = x_tilt_tel_outArray[0];
		out[4] = y_tilt_tel_outArray[0];
		return out;
	}
}