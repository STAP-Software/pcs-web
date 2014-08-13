package org.tmt.aps.peas.lang.interop; 
public class JttOffsetsToActs
{
	public native void ttOffsetsToActs(RetVal retVal, float x_act[], int x_act_size_1, float y_act[], int y_act_size_1, float secperpix, float delta_x_pix_in[], int delta_x_pix_in_size_1, float delta_y_pix_in[], int delta_y_pix_in_size_1, float delta_x_pix_out[], int delta_x_pix_out_size_1, float delta_y_pix_out[], int delta_y_pix_out_size_1, float dz_act[], int dz_act_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jttOffsetsToActs(RetVal retVal, float x_act[], float y_act[], float secperpix, float delta_x_pix_in[], float delta_y_pix_in[], float delta_x_pix_out[], float delta_y_pix_out[], float dz_act[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_act_len1 = x_act.length;
		int y_act_len1 = y_act.length;
		int delta_x_pix_in_len1 = delta_x_pix_in.length;
		int delta_y_pix_in_len1 = delta_y_pix_in.length;
		int delta_x_pix_out_len1 = delta_x_pix_out.length;
		int delta_y_pix_out_len1 = delta_y_pix_out.length;
		int dz_act_len1 = dz_act.length;
		// Call native method
		ttOffsetsToActs(retVal, x_act,x_act_len1,y_act,y_act_len1,secperpix,delta_x_pix_in,delta_x_pix_in_len1,delta_y_pix_in,delta_y_pix_in_len1,delta_x_pix_out,delta_x_pix_out_len1,delta_y_pix_out,delta_y_pix_out_len1,dz_act,dz_act_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}