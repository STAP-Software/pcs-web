package org.tmt.aps.peas.lang.interop; 
public class JsecondaryActuator
{
	public native void secondaryActuator(RetVal retVal, float xmirror[], int xmirror_size_1, float ymirror[], int ymirror_size_1, float x_lens_2[], int x_lens_2_size_1, int x_lens_2_size_2, float y_lens_2[], int y_lens_2_size_1, int y_lens_2_size_2, float segment_zernike[], int segment_zernike_size_1, int segment_zernike_size_2, float offsets[], int offsets_size_1, int offsets_size_2, float f_corr, float m2_tt_correction_factor, float ahex_mm, float d1, float bfl, float focal_length_1, float focal_length_2, int n1, int n2, int row_flag_det[], int row_flag_det_size_1, float epsilon_x[], float epsilon_y[], float epsilon_x_tel[], float epsilon_y_tel[], float delta_z[], float b_pcs_cm2[], int b_pcs_cm2_size_1, float xtt_out_pcs[], int xtt_out_pcs_size_1, float ytt_out_pcs[], int ytt_out_pcs_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsecondaryActuator(RetVal retVal, float xmirror[], float ymirror[], float x_lens_2[][], float y_lens_2[][], float segment_zernike[][], float offsets[][], float f_corr, float m2_tt_correction_factor, float ahex_mm, float d1, float bfl, float focal_length_1, float focal_length_2, int n1, int n2, int row_flag_det[], float b_pcs_cm2[], float xtt_out_pcs[], float ytt_out_pcs[] ) {
		// Output variable definitions
		float epsilon_x_outArray[] = new float[1];
		float epsilon_y_outArray[] = new float[1];
		float epsilon_x_tel_outArray[] = new float[1];
		float epsilon_y_tel_outArray[] = new float[1];
		float delta_z_outArray[] = new float[1];
		// Deal with Array Lengths
		int xmirror_len1 = xmirror.length;
		int ymirror_len1 = ymirror.length;
		int x_lens_2_len1 = x_lens_2.length;
		int x_lens_2_len2 = x_lens_2[0].length;
		float[] x_lens_2_collapse = new float[x_lens_2_len1 * x_lens_2_len2];
		int y_lens_2_len1 = y_lens_2.length;
		int y_lens_2_len2 = y_lens_2[0].length;
		float[] y_lens_2_collapse = new float[y_lens_2_len1 * y_lens_2_len2];
		int segment_zernike_len1 = segment_zernike.length;
		int segment_zernike_len2 = segment_zernike[0].length;
		float[] segment_zernike_collapse = new float[segment_zernike_len1 * segment_zernike_len2];
		int offsets_len1 = offsets.length;
		int offsets_len2 = offsets[0].length;
		float[] offsets_collapse = new float[offsets_len1 * offsets_len2];
		int row_flag_det_len1 = row_flag_det.length;
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
		// collapse array to one dimension
		for (int i=0; i<segment_zernike_len1; i++) { 
			for (int j=0; j<segment_zernike_len2; j++) { 
				segment_zernike_collapse[i*segment_zernike_len2 + j] = segment_zernike[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets_collapse[i*offsets_len2 + j] = offsets[i][j]; 
			} 
		} 
		// Call native method
		secondaryActuator(retVal, xmirror,xmirror_len1,ymirror,ymirror_len1,x_lens_2_collapse,x_lens_2_len1,x_lens_2_len2,y_lens_2_collapse,y_lens_2_len1,y_lens_2_len2,segment_zernike_collapse,segment_zernike_len1,segment_zernike_len2,offsets_collapse,offsets_len1,offsets_len2,f_corr,m2_tt_correction_factor,ahex_mm,d1,bfl,focal_length_1,focal_length_2,n1,n2,row_flag_det,row_flag_det_len1,epsilon_x_outArray,epsilon_y_outArray,epsilon_x_tel_outArray,epsilon_y_tel_outArray,delta_z_outArray,b_pcs_cm2,b_pcs_cm2_len1,xtt_out_pcs,xtt_out_pcs_len1,ytt_out_pcs,ytt_out_pcs_len1);
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
		// expand array to two dimensions
		for (int i=0; i<segment_zernike_len1; i++) { 
			for (int j=0; j<segment_zernike_len2; j++) { 
				segment_zernike[i][j] = segment_zernike_collapse[i*segment_zernike_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets[i][j] = offsets_collapse[i*offsets_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[5];
		out[0] = epsilon_x_outArray[0];
		out[1] = epsilon_y_outArray[0];
		out[2] = epsilon_x_tel_outArray[0];
		out[3] = epsilon_y_tel_outArray[0];
		out[4] = delta_z_outArray[0];
		return out;
	}
}