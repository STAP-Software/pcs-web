package org.tmt.aps.peas.lang.interop; 
public class JoptimalPistons
{
	public native void optimalPistons(RetVal retVal, float acsa[], int acsa_size_1, int acsa_size_2, float act_tt[], int act_tt_size_1, float b[], int b_size_1, int mtest, float act_p[], int act_p_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] joptimalPistons(RetVal retVal, float acsa[][], float act_tt[], float b[], int mtest, float act_p[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		int act_tt_len1 = act_tt.length;
		int b_len1 = b.length;
		int act_p_len1 = act_p.length;
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		optimalPistons(retVal, acsa_collapse,acsa_len1,acsa_len2,act_tt,act_tt_len1,b,b_len1,mtest,act_p,act_p_len1);
		// expand array to two dimensions
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa[i][j] = acsa_collapse[i*acsa_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}