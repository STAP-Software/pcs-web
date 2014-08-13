package org.tmt.aps.peas.lang.interop; 
public class JdecomposeActs
{
	public native void decomposeActs(RetVal retVal, float act_input[], int act_input_size_1, float act_tt[], int act_tt_size_1, float act_p[], int act_p_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jdecomposeActs(RetVal retVal, float act_input[], float act_tt[], float act_p[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int act_input_len1 = act_input.length;
		int act_tt_len1 = act_tt.length;
		int act_p_len1 = act_p.length;
		// Call native method
		decomposeActs(retVal, act_input,act_input_len1,act_tt,act_tt_len1,act_p,act_p_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}