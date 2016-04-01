package org.tmt.aps.peas.lang.interop; 
public class JsufsOffsetsToZernikes
{
	public native void sufsOffsetsToZernikes(RetVal retVal, float x_subap[], int x_subap_size_1, float y_subap[], int y_subap_size_1, float delta_x[], int delta_x_size_1, float delta_y[], int delta_y_size_1, float ahex_mm, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float czern_calc[], int czern_calc_size_1, float wh_factor[], float delta_x_th[], int delta_x_th_size_1, float delta_y_th[], int delta_y_th_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsufsOffsetsToZernikes(RetVal retVal, float x_subap[], float y_subap[], float delta_x[], float delta_y[], float ahex_mm, int row_flag[], int col_flag[], float czern_calc[], float delta_x_th[], float delta_y_th[] ) {
		// Output variable definitions
		float wh_factor_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_subap_len1 = x_subap.length;
		int y_subap_len1 = y_subap.length;
		int delta_x_len1 = delta_x.length;
		int delta_y_len1 = delta_y.length;
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int czern_calc_len1 = czern_calc.length;
		int delta_x_th_len1 = delta_x_th.length;
		int delta_y_th_len1 = delta_y_th.length;
		// Call native method
		sufsOffsetsToZernikes(retVal, x_subap,x_subap_len1,y_subap,y_subap_len1,delta_x,delta_x_len1,delta_y,delta_y_len1,ahex_mm,row_flag,row_flag_len1,col_flag,col_flag_len1,czern_calc,czern_calc_len1,wh_factor_outArray,delta_x_th,delta_x_th_len1,delta_y_th,delta_y_th_len1);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = wh_factor_outArray[0];
		return out;
	}
}