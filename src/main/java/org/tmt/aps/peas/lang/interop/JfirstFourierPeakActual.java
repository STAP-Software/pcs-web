package org.tmt.aps.peas.lang.interop; 
public class JfirstFourierPeakActual
{
	public native void firstFourierPeakActual(RetVal retVal, float u_theoretical, float v_theoretical, float u_delta_0, int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, int npeak, int icenter, int jcenter, float u_actual, float v_actual, float q_factor[], float trans_actual[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfirstFourierPeakActual(RetVal retVal, float u_theoretical, float v_theoretical, float u_delta_0, int nxt[], int nyt[], int npeak, int icenter, int jcenter, float u_actual, float v_actual ) {
		// Output variable definitions
		float q_factor_outArray[] = new float[1];
		float trans_actual_outArray[] = new float[1];
		// Deal with Array Lengths
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		// Call native method
		firstFourierPeakActual(retVal, u_theoretical,v_theoretical,u_delta_0,nxt,nxt_len1,nyt,nyt_len1,npeak,icenter,jcenter,u_actual,v_actual,q_factor_outArray,trans_actual_outArray);
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = q_factor_outArray[0];
		out[1] = trans_actual_outArray[0];
		return out;
	}
}