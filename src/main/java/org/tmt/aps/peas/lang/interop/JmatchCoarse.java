package org.tmt.aps.peas.lang.interop; 
public class JmatchCoarse
{
	public native void matchCoarse(RetVal retVal, int npattern, int matchbox, float xi_rst_initial[], int xi_rst_initial_size_1, float yi_rst_initial[], int yi_rst_initial_size_1, float x_shift_initial, float y_shift_initial, float virtual_ccd[], int virtual_ccd_size_1, int virtual_ccd_size_2, int n_subap, float scale_meas, float u_actual, float xi_rst_updated[], int xi_rst_updated_size_1, float yi_rst_updated[], int yi_rst_updated_size_1, float x_shift_tot_updated[], float y_shift_tot_updated[], int n_solution[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmatchCoarse(RetVal retVal, int npattern, int matchbox, float xi_rst_initial[], float yi_rst_initial[], float x_shift_initial, float y_shift_initial, float virtual_ccd[][], int n_subap, float scale_meas, float u_actual, float xi_rst_updated[], float yi_rst_updated[] ) {
		// Output variable definitions
		float x_shift_tot_updated_outArray[] = new float[1];
		float y_shift_tot_updated_outArray[] = new float[1];
		int n_solution_outArray[] = new int[1];
		// Deal with Array Lengths
		int xi_rst_initial_len1 = xi_rst_initial.length;
		int yi_rst_initial_len1 = yi_rst_initial.length;
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
		matchCoarse(retVal, npattern,matchbox,xi_rst_initial,xi_rst_initial_len1,yi_rst_initial,yi_rst_initial_len1,x_shift_initial,y_shift_initial,virtual_ccd_collapse,virtual_ccd_len1,virtual_ccd_len2,n_subap,scale_meas,u_actual,xi_rst_updated,xi_rst_updated_len1,yi_rst_updated,yi_rst_updated_len1,x_shift_tot_updated_outArray,y_shift_tot_updated_outArray,n_solution_outArray);
		// expand array to two dimensions
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd[i][j] = virtual_ccd_collapse[i*virtual_ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = x_shift_tot_updated_outArray[0];
		out[1] = y_shift_tot_updated_outArray[0];
		out[2] = n_solution_outArray[0];
		return out;
	}
}