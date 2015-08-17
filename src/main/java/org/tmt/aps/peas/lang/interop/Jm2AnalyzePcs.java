package org.tmt.aps.peas.lang.interop; 
public class Jm2AnalyzePcs
{
	public native void m2AnalyzePcs(RetVal retVal, float b[], int b_size_1, int row_flag_det[], int row_flag_det_size_1, float dsecondact_pcs[], int dsecondact_pcs_size_1, float piston_pcs[], float x_tilt_pcs[], float y_tilt_pcs[], float b_pcs_cm2[], int b_pcs_cm2_size_1, float pcs_resid[], float xtt_out_pcs[], int xtt_out_pcs_size_1, float ytt_out_pcs[], int ytt_out_pcs_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jm2AnalyzePcs(RetVal retVal, float b[], int row_flag_det[], float dsecondact_pcs[], float b_pcs_cm2[], float xtt_out_pcs[], float ytt_out_pcs[] ) {
		// Output variable definitions
		float piston_pcs_outArray[] = new float[1];
		float x_tilt_pcs_outArray[] = new float[1];
		float y_tilt_pcs_outArray[] = new float[1];
		float pcs_resid_outArray[] = new float[1];
		// Deal with Array Lengths
		int b_len1 = b.length;
		int row_flag_det_len1 = row_flag_det.length;
		int dsecondact_pcs_len1 = dsecondact_pcs.length;
		int b_pcs_cm2_len1 = b_pcs_cm2.length;
		int xtt_out_pcs_len1 = xtt_out_pcs.length;
		int ytt_out_pcs_len1 = ytt_out_pcs.length;
		// Call native method
		m2AnalyzePcs(retVal, b,b_len1,row_flag_det,row_flag_det_len1,dsecondact_pcs,dsecondact_pcs_len1,piston_pcs_outArray,x_tilt_pcs_outArray,y_tilt_pcs_outArray,b_pcs_cm2,b_pcs_cm2_len1,pcs_resid_outArray,xtt_out_pcs,xtt_out_pcs_len1,ytt_out_pcs,ytt_out_pcs_len1);
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = piston_pcs_outArray[0];
		out[1] = x_tilt_pcs_outArray[0];
		out[2] = y_tilt_pcs_outArray[0];
		out[3] = pcs_resid_outArray[0];
		return out;
	}
}