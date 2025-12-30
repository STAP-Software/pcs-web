package org.tmt.aps.peas.lang.interop; 
public class JmatchFinal
{
	public native void matchFinal(RetVal retVal, float ccd_image[], int ccd_image_size_1, int ccd_image_size_2, int matchbox, float peak_max, float xi_rst_initial[], int xi_rst_initial_size_1, float yi_rst_initial[], int yi_rst_initial_size_1, float x_shift_tot_initial, float y_shift_tot_initial, float virtual_ccd[], int virtual_ccd_size_1, int virtual_ccd_size_2, int spot_flag[], int spot_flag_size_1, float scale_meas, int label_offset, int n_subap, float x_peak[], int x_peak_size_1, float y_peak[], int y_peak_size_1, float xi_rst_updated[], int xi_rst_updated_size_1, float yi_rst_updated[], int yi_rst_updated_size_1, int n_detect[], int n_detect_size_1, int n0123[], int n0123_size_1, float x_shift_tot_updated[], float y_shift_tot_updated[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmatchFinal(RetVal retVal, float ccd_image[][], int matchbox, float peak_max, float xi_rst_initial[], float yi_rst_initial[], float x_shift_tot_initial, float y_shift_tot_initial, float virtual_ccd[][], int spot_flag[], float scale_meas, int label_offset, int n_subap, float x_peak[], float y_peak[], float xi_rst_updated[], float yi_rst_updated[], int n_detect[], int n0123[] ) {
		// Output variable definitions
		float x_shift_tot_updated_outArray[] = new float[1];
		float y_shift_tot_updated_outArray[] = new float[1];
		// Deal with Array Lengths
		int ccd_image_len1 = ccd_image.length;
		int ccd_image_len2 = ccd_image[0].length;
		float[] ccd_image_collapse = new float[ccd_image_len1 * ccd_image_len2];
		int xi_rst_initial_len1 = xi_rst_initial.length;
		int yi_rst_initial_len1 = yi_rst_initial.length;
		int virtual_ccd_len1 = virtual_ccd.length;
		int virtual_ccd_len2 = virtual_ccd[0].length;
		float[] virtual_ccd_collapse = new float[virtual_ccd_len1 * virtual_ccd_len2];
		int spot_flag_len1 = spot_flag.length;
		int x_peak_len1 = x_peak.length;
		int y_peak_len1 = y_peak.length;
		int xi_rst_updated_len1 = xi_rst_updated.length;
		int yi_rst_updated_len1 = yi_rst_updated.length;
		int n_detect_len1 = n_detect.length;
		int n0123_len1 = n0123.length;
		// collapse array to one dimension
		for (int i=0; i<ccd_image_len1; i++) { 
			for (int j=0; j<ccd_image_len2; j++) { 
				ccd_image_collapse[i*ccd_image_len2 + j] = ccd_image[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd_collapse[i*virtual_ccd_len2 + j] = virtual_ccd[i][j]; 
			} 
		} 
		// Call native method
		matchFinal(retVal, ccd_image_collapse,ccd_image_len1,ccd_image_len2,matchbox,peak_max,xi_rst_initial,xi_rst_initial_len1,yi_rst_initial,yi_rst_initial_len1,x_shift_tot_initial,y_shift_tot_initial,virtual_ccd_collapse,virtual_ccd_len1,virtual_ccd_len2,spot_flag,spot_flag_len1,scale_meas,label_offset,n_subap,x_peak,x_peak_len1,y_peak,y_peak_len1,xi_rst_updated,xi_rst_updated_len1,yi_rst_updated,yi_rst_updated_len1,n_detect,n_detect_len1,n0123,n0123_len1,x_shift_tot_updated_outArray,y_shift_tot_updated_outArray);
		// expand array to two dimensions
		for (int i=0; i<ccd_image_len1; i++) { 
			for (int j=0; j<ccd_image_len2; j++) { 
				ccd_image[i][j] = ccd_image_collapse[i*ccd_image_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd[i][j] = virtual_ccd_collapse[i*virtual_ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = x_shift_tot_updated_outArray[0];
		out[1] = y_shift_tot_updated_outArray[0];
		return out;
	}
}