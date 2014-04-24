package org.tmt.aps.peas.lang.interop; 
public class JfirstFourierPeakTheory
{
	public native void firstFourierPeakTheory(RetVal retVal, float u_est, float u_delta_0, int n_subap, float xp_subaps[], int xp_subaps_size_1, float yp_subaps[], int yp_subaps_size_1, float u_theoritical[], float v_theoritical[], float trans_theory[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfirstFourierPeakTheory(RetVal retVal, float u_est, float u_delta_0, int n_subap, float xp_subaps[], float yp_subaps[] ) {
		// Output variable definitions
		float u_theoritical_outArray[] = new float[1];
		float v_theoritical_outArray[] = new float[1];
		float trans_theory_outArray[] = new float[1];
		// Deal with Array Lengths
		int xp_subaps_len1 = xp_subaps.length;
		int yp_subaps_len1 = yp_subaps.length;
		// Call native method
		firstFourierPeakTheory(retVal, u_est,u_delta_0,n_subap,xp_subaps,xp_subaps_len1,yp_subaps,yp_subaps_len1,u_theoritical_outArray,v_theoritical_outArray,trans_theory_outArray);
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = u_theoritical_outArray[0];
		out[1] = v_theoritical_outArray[0];
		out[2] = trans_theory_outArray[0];
		return out;
	}
}