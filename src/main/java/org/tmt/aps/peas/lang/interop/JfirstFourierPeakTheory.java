package org.tmt.aps.peas.lang.interop; 
public class JfirstFourierPeakTheory
{
	public native void firstFourierPeakTheory(RetVal retVal, float u_est, float u_delta_0, int n_subap, float xp_subap[], int xp_subap_size_1, float yp_subap[], int yp_subap_size_1, float u_theoretical[], float v_theoretical[], float trans_theory[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfirstFourierPeakTheory(RetVal retVal, float u_est, float u_delta_0, int n_subap, float xp_subap[], float yp_subap[] ) {
		// Output variable definitions
		float u_theoretical_outArray[] = new float[1];
		float v_theoretical_outArray[] = new float[1];
		float trans_theory_outArray[] = new float[1];
		// Deal with Array Lengths
		int xp_subap_len1 = xp_subap.length;
		int yp_subap_len1 = yp_subap.length;
		// Call native method
		firstFourierPeakTheory(retVal, u_est,u_delta_0,n_subap,xp_subap,xp_subap_len1,yp_subap,yp_subap_len1,u_theoretical_outArray,v_theoretical_outArray,trans_theory_outArray);
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = u_theoretical_outArray[0];
		out[1] = v_theoretical_outArray[0];
		out[2] = trans_theory_outArray[0];
		return out;
	}
}