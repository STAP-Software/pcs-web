package org.tmt.aps.peas.lang.interop; 
public class JsufsOffsetsToZernikes
{
	public native void sufsOffsetsToZernikes(RetVal retVal, float x_subap[], int x_subap_size_1, float y_subap[], int y_subap_size_1, float b_in[], int b_in_size_1, float ahex_mm, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float czern_calc[], int czern_calc_size_1, float wh_factor[], float b_th[], int b_th_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsufsOffsetsToZernikes(RetVal retVal, float x_subap[], float y_subap[], float b_in[], float ahex_mm, int row_flag[], int col_flag[], float czern_calc[], float b_th[] ) {
		// Output variable definitions
		float wh_factor_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_subap_len1 = x_subap.length;
		int y_subap_len1 = y_subap.length;
		int b_in_len1 = b_in.length;
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int czern_calc_len1 = czern_calc.length;
		int b_th_len1 = b_th.length;
		// Call native method
		sufsOffsetsToZernikes(retVal, x_subap,x_subap_len1,y_subap,y_subap_len1,b_in,b_in_len1,ahex_mm,row_flag,row_flag_len1,col_flag,col_flag_len1,czern_calc,czern_calc_len1,wh_factor_outArray,b_th,b_th_len1);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = wh_factor_outArray[0];
		return out;
	}
}