package org.tmt.aps.peas.lang.interop; 
public class Jm2SvdTheory
{
	public native void m2SvdTheory(RetVal retVal, float eigenvectors[], int eigenvectors_size_1, int eigenvectors_size_2, float ww[], int ww_size_1, float unit_piston, float unit_tt, float xmult_piston[], float xmult_tip[], float xmult_tilt[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jm2SvdTheory(RetVal retVal, float eigenvectors[][], float ww[], float unit_piston, float unit_tt ) {
		// Output variable definitions
		float xmult_piston_outArray[] = new float[1];
		float xmult_tip_outArray[] = new float[1];
		float xmult_tilt_outArray[] = new float[1];
		// Deal with Array Lengths
		int eigenvectors_len1 = eigenvectors.length;
		int eigenvectors_len2 = eigenvectors[0].length;
		float[] eigenvectors_collapse = new float[eigenvectors_len1 * eigenvectors_len2];
		int ww_len1 = ww.length;
		// collapse array to one dimension
		for (int i=0; i<eigenvectors_len1; i++) { 
			for (int j=0; j<eigenvectors_len2; j++) { 
				eigenvectors_collapse[i*eigenvectors_len2 + j] = eigenvectors[i][j]; 
			} 
		} 
		// Call native method
		m2SvdTheory(retVal, eigenvectors_collapse,eigenvectors_len1,eigenvectors_len2,ww,ww_len1,unit_piston,unit_tt,xmult_piston_outArray,xmult_tip_outArray,xmult_tilt_outArray);
		// expand array to two dimensions
		for (int i=0; i<eigenvectors_len1; i++) { 
			for (int j=0; j<eigenvectors_len2; j++) { 
				eigenvectors[i][j] = eigenvectors_collapse[i*eigenvectors_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = xmult_piston_outArray[0];
		out[1] = xmult_tip_outArray[0];
		out[2] = xmult_tilt_outArray[0];
		return out;
	}
}