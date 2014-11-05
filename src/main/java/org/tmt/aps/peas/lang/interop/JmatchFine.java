package org.tmt.aps.peas.lang.interop; 
public class JmatchFine
{
	public native void matchFine(RetVal retVal, int matchbox, float xi_rst_initial[], int xi_rst_initial_size_1, float yi_rst_initial[], int yi_rst_initial_size_1, float scale_meas, float matchFineThresh, int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, float virtual_ccd[], int virtual_ccd_size_1, int virtual_ccd_size_2, int label_offset, int n_subap, float xi_rst_updated[], int xi_rst_updated_size_1, float yi_rst_updated[], int yi_rst_updated_size_1, float x_shift_tot[], float y_shift_tot[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmatchFine(RetVal retVal, int matchbox, float xi_rst_initial[], float yi_rst_initial[], float scale_meas, float matchFineThresh, int nxt[], int nyt[], float virtual_ccd[][], int label_offset, int n_subap, float xi_rst_updated[], float yi_rst_updated[] ) {
		// Output variable definitions
		float x_shift_tot_outArray[] = new float[1];
		float y_shift_tot_outArray[] = new float[1];
		// Deal with Array Lengths
		int xi_rst_initial_len1 = xi_rst_initial.length;
		int yi_rst_initial_len1 = yi_rst_initial.length;
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		int virtual_ccd_len1 = virtual_ccd.length;
		int virtual_ccd_len2 = virtual_ccd[0].length;
		float[] virtual_ccd_collapse = new float[virtual_ccd_len1 * virtual_ccd_len2];
		int xi_rst_updated_len1 = xi_rst_updated.length;
		int yi_rst_updated_len1 = yi_rst_updated.length;
		// collapse array to one dimension
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd_collapse[i*virtual_ccd_len2 + j] = virtual_ccd[i][j]; 
			} 
		} 
		// Call native method
		matchFine(retVal, matchbox,xi_rst_initial,xi_rst_initial_len1,yi_rst_initial,yi_rst_initial_len1,scale_meas,matchFineThresh,nxt,nxt_len1,nyt,nyt_len1,virtual_ccd_collapse,virtual_ccd_len1,virtual_ccd_len2,label_offset,n_subap,xi_rst_updated,xi_rst_updated_len1,yi_rst_updated,yi_rst_updated_len1,x_shift_tot_outArray,y_shift_tot_outArray);
		// expand array to two dimensions
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd[i][j] = virtual_ccd_collapse[i*virtual_ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = x_shift_tot_outArray[0];
		out[1] = y_shift_tot_outArray[0];
		return out;
	}
}