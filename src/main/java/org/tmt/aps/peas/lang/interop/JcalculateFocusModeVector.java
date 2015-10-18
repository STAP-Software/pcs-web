package org.tmt.aps.peas.lang.interop; 
public class JcalculateFocusModeVector
{
	public native void calculateFocusModeVector(RetVal retVal, float acsa[], int acsa_size_1, int acsa_size_2, float act_fm[], int act_fm_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcalculateFocusModeVector(RetVal retVal, float acsa[][], float act_fm[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		int act_fm_len1 = act_fm.length;
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		calculateFocusModeVector(retVal, acsa_collapse,acsa_len1,acsa_len2,act_fm,act_fm_len1);
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