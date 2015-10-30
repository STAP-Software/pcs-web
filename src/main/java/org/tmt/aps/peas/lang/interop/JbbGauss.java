package org.tmt.aps.peas.lang.interop; 
public class JbbGauss
{
	public native void bbGauss(RetVal retVal, float corr_index[], int corr_index_size_1, float sigma, float step_size, float g_interval, float chisqmin[], float edge_step[], float sn[], int row_flag_temp[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbbGauss(RetVal retVal, float corr_index[], float sigma, float step_size, float g_interval ) {
		// Output variable definitions
		float chisqmin_outArray[] = new float[1];
		float edge_step_outArray[] = new float[1];
		float sn_outArray[] = new float[1];
		int row_flag_temp_outArray[] = new int[1];
		// Deal with Array Lengths
		int corr_index_len1 = corr_index.length;
		// Call native method
		bbGauss(retVal, corr_index,corr_index_len1,sigma,step_size,g_interval,chisqmin_outArray,edge_step_outArray,sn_outArray,row_flag_temp_outArray);
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = chisqmin_outArray[0];
		out[1] = edge_step_outArray[0];
		out[2] = sn_outArray[0];
		out[3] = row_flag_temp_outArray[0];
		return out;
	}
}