package org.tmt.aps.peas.lang.interop; 
public class JcompressSpotList
{
	public native void compressSpotList(RetVal retVal, float xp_subap_in[], int xp_subap_in_size_1, float yp_subap_in[], int yp_subap_in_size_1, int spot_flag_config[], int spot_flag_config_size_1, float xp_subap_out[], int xp_subap_out_size_1, float yp_subap_out[], int yp_subap_out_size_1, int spot_flag_out[], int spot_flag_out_size_1, int n_subap[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcompressSpotList(RetVal retVal, float xp_subap_in[], float yp_subap_in[], int spot_flag_config[], float xp_subap_out[], float yp_subap_out[], int spot_flag_out[] ) {
		// Output variable definitions
		int n_subap_outArray[] = new int[1];
		// Deal with Array Lengths
		int xp_subap_in_len1 = xp_subap_in.length;
		int yp_subap_in_len1 = yp_subap_in.length;
		int spot_flag_config_len1 = spot_flag_config.length;
		int xp_subap_out_len1 = xp_subap_out.length;
		int yp_subap_out_len1 = yp_subap_out.length;
		int spot_flag_out_len1 = spot_flag_out.length;
		// Call native method
		compressSpotList(retVal, xp_subap_in,xp_subap_in_len1,yp_subap_in,yp_subap_in_len1,spot_flag_config,spot_flag_config_len1,xp_subap_out,xp_subap_out_len1,yp_subap_out,yp_subap_out_len1,spot_flag_out,spot_flag_out_len1,n_subap_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = n_subap_outArray[0];
		return out;
	}
}