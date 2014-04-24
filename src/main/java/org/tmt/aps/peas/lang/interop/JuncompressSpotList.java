package org.tmt.aps.peas.lang.interop; 
public class JuncompressSpotList
{
	public native void uncompressSpotList(RetVal retVal, float xi_rst_in[], int xi_rst_in_size_1, float yi_rst_in[], int yi_rst_in_size_1, float x_peak_in[], int x_peak_in_size_1, float y_peak_in[], int y_peak_in_size_1, int n_detect_in[], int n_detect_in_size_1, int spot_flag_config[], int spot_flag_config_size_1, float xi_rst_out[], int xi_rst_out_size_1, float yi_rst_out[], int yi_rst_out_size_1, float x_peak_out[], int x_peak_out_size_1, float y_peak_out[], int y_peak_out_size_1, int n_detect_out[], int n_detect_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] juncompressSpotList(RetVal retVal, float xi_rst_in[], float yi_rst_in[], float x_peak_in[], float y_peak_in[], int n_detect_in[], int spot_flag_config[], float xi_rst_out[], float yi_rst_out[], float x_peak_out[], float y_peak_out[], int n_detect_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xi_rst_in_len1 = xi_rst_in.length;
		int yi_rst_in_len1 = yi_rst_in.length;
		int x_peak_in_len1 = x_peak_in.length;
		int y_peak_in_len1 = y_peak_in.length;
		int n_detect_in_len1 = n_detect_in.length;
		int spot_flag_config_len1 = spot_flag_config.length;
		int xi_rst_out_len1 = xi_rst_out.length;
		int yi_rst_out_len1 = yi_rst_out.length;
		int x_peak_out_len1 = x_peak_out.length;
		int y_peak_out_len1 = y_peak_out.length;
		int n_detect_out_len1 = n_detect_out.length;
		// Call native method
		uncompressSpotList(retVal, xi_rst_in,xi_rst_in_len1,yi_rst_in,yi_rst_in_len1,x_peak_in,x_peak_in_len1,y_peak_in,y_peak_in_len1,n_detect_in,n_detect_in_len1,spot_flag_config,spot_flag_config_len1,xi_rst_out,xi_rst_out_len1,yi_rst_out,yi_rst_out_len1,x_peak_out,x_peak_out_len1,y_peak_out,y_peak_out_len1,n_detect_out,n_detect_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}