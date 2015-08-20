package org.tmt.aps.peas.lang.interop; 
public class Jm2AnalyzeRt
{
	public native void m2AnalyzeRt(RetVal retVal, float b[], int b_size_1, int row_flag_det[], int row_flag_det_size_1, float unit_piston, float unit_tt, float dsecondact_rt[], int dsecondact_rt_size_1, float act_calc[], int act_calc_size_1, float b_smz[], int b_smz_size_1, float b_cm2[], int b_cm2_size_1, float rt_resid[], float xmult_piston[], float xmult_tip[], float xmult_tilt[], float xtt_in[], int xtt_in_size_1, float ytt_in[], int ytt_in_size_1, float xtt_out_rt[], int xtt_out_rt_size_1, float ytt_out_rt[], int ytt_out_rt_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jm2AnalyzeRt(RetVal retVal, float b[], int row_flag_det[], float unit_piston, float unit_tt, float dsecondact_rt[], float act_calc[], float b_smz[], float b_cm2[], float xtt_in[], float ytt_in[], float xtt_out_rt[], float ytt_out_rt[] ) {
		// Output variable definitions
		float rt_resid_outArray[] = new float[1];
		float xmult_piston_outArray[] = new float[1];
		float xmult_tip_outArray[] = new float[1];
		float xmult_tilt_outArray[] = new float[1];
		// Deal with Array Lengths
		int b_len1 = b.length;
		int row_flag_det_len1 = row_flag_det.length;
		int dsecondact_rt_len1 = dsecondact_rt.length;
		int act_calc_len1 = act_calc.length;
		int b_smz_len1 = b_smz.length;
		int b_cm2_len1 = b_cm2.length;
		int xtt_in_len1 = xtt_in.length;
		int ytt_in_len1 = ytt_in.length;
		int xtt_out_rt_len1 = xtt_out_rt.length;
		int ytt_out_rt_len1 = ytt_out_rt.length;
		// Call native method
		m2AnalyzeRt(retVal, b,b_len1,row_flag_det,row_flag_det_len1,unit_piston,unit_tt,dsecondact_rt,dsecondact_rt_len1,act_calc,act_calc_len1,b_smz,b_smz_len1,b_cm2,b_cm2_len1,rt_resid_outArray,xmult_piston_outArray,xmult_tip_outArray,xmult_tilt_outArray,xtt_in,xtt_in_len1,ytt_in,ytt_in_len1,xtt_out_rt,xtt_out_rt_len1,ytt_out_rt,ytt_out_rt_len1);
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = rt_resid_outArray[0];
		out[1] = xmult_piston_outArray[0];
		out[2] = xmult_tip_outArray[0];
		out[3] = xmult_tilt_outArray[0];
		return out;
	}
}