package org.tmt.aps.peas.lang.interop; 
public class JedgeFromPhasesViaChisq
{
	public native void edgeFromPhasesViaChisq(RetVal retVal, float xlambda[], int xlambda_size_1, float ph_meas[], int ph_meas_size_1, float range, float r_int, float ph_calc[], int ph_calc_size_1, float chisq_min[], float s_calc[], float ds_calc[], int ds_calc_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jedgeFromPhasesViaChisq(RetVal retVal, float xlambda[], float ph_meas[], float range, float r_int, float ph_calc[], float ds_calc[] ) {
		// Output variable definitions
		float chisq_min_outArray[] = new float[1];
		float s_calc_outArray[] = new float[1];
		// Deal with Array Lengths
		int xlambda_len1 = xlambda.length;
		int ph_meas_len1 = ph_meas.length;
		int ph_calc_len1 = ph_calc.length;
		int ds_calc_len1 = ds_calc.length;
		// Call native method
		edgeFromPhasesViaChisq(retVal, xlambda,xlambda_len1,ph_meas,ph_meas_len1,range,r_int,ph_calc,ph_calc_len1,chisq_min_outArray,s_calc_outArray,ds_calc,ds_calc_len1);
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = chisq_min_outArray[0];
		out[1] = s_calc_outArray[0];
		return out;
	}
}