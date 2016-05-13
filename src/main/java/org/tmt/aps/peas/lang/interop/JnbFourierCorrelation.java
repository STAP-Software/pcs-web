package org.tmt.aps.peas.lang.interop; 
public class JnbFourierCorrelation
{
	public native void nbFourierCorrelation(RetVal retVal, float c_meas[], int c_meas_size_1, float a_fit[], float b_fit[], float phi_fit[], float chisq_f[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbFourierCorrelation(RetVal retVal, float c_meas[] ) {
		// Output variable definitions
		float a_fit_outArray[] = new float[1];
		float b_fit_outArray[] = new float[1];
		float phi_fit_outArray[] = new float[1];
		float chisq_f_outArray[] = new float[1];
		// Deal with Array Lengths
		int c_meas_len1 = c_meas.length;
		// Call native method
		nbFourierCorrelation(retVal, c_meas,c_meas_len1,a_fit_outArray,b_fit_outArray,phi_fit_outArray,chisq_f_outArray);
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = a_fit_outArray[0];
		out[1] = b_fit_outArray[0];
		out[2] = phi_fit_outArray[0];
		out[3] = chisq_f_outArray[0];
		return out;
	}
}