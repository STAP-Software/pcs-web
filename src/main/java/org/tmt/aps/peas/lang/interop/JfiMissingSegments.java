package org.tmt.aps.peas.lang.interop; 
public class JfiMissingSegments
{
	public native void fiMissingSegments(RetVal retVal, float x_ref_def[], int x_ref_def_size_1, float y_ref_def[], int y_ref_def_size_1, float xc_pix[], int xc_pix_size_1, float yc_pix[], int yc_pix_size_1, float ahex_pix, int segment_flag[], int segment_flag_size_1, int spot_flag_config[], int spot_flag_config_size_1, int n_subap_config, int incompleteMirrorType, int spot_flag_out[], int spot_flag_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfiMissingSegments(RetVal retVal, float x_ref_def[], float y_ref_def[], float xc_pix[], float yc_pix[], float ahex_pix, int segment_flag[], int spot_flag_config[], int n_subap_config, int incompleteMirrorType, int spot_flag_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_ref_def_len1 = x_ref_def.length;
		int y_ref_def_len1 = y_ref_def.length;
		int xc_pix_len1 = xc_pix.length;
		int yc_pix_len1 = yc_pix.length;
		int segment_flag_len1 = segment_flag.length;
		int spot_flag_config_len1 = spot_flag_config.length;
		int spot_flag_out_len1 = spot_flag_out.length;
		// Call native method
		fiMissingSegments(retVal, x_ref_def,x_ref_def_len1,y_ref_def,y_ref_def_len1,xc_pix,xc_pix_len1,yc_pix,yc_pix_len1,ahex_pix,segment_flag,segment_flag_len1,spot_flag_config,spot_flag_config_len1,n_subap_config,incompleteMirrorType,spot_flag_out,spot_flag_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}