package org.tmt.aps.peas.lang.interop; 
public class JcolorStep
{
	public native void colorStep(RetVal retVal, int n, float stepSize, float dPColor[], int dPColor_size_1, int dPColor_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcolorStep(RetVal retVal, int n, float stepSize, float dPColor[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int dPColor_len1 = dPColor.length;
		int dPColor_len2 = dPColor[0].length;
		float[] dPColor_collapse = new float[dPColor_len1 * dPColor_len2];
		// collapse array to one dimension
		for (int i=0; i<dPColor_len1; i++) { 
			for (int j=0; j<dPColor_len2; j++) { 
				dPColor_collapse[i*dPColor_len2 + j] = dPColor[i][j]; 
			} 
		} 
		// Call native method
		colorStep(retVal, n,stepSize,dPColor_collapse,dPColor_len1,dPColor_len2);
		// expand array to two dimensions
		for (int i=0; i<dPColor_len1; i++) { 
			for (int j=0; j<dPColor_len2; j++) { 
				dPColor[i][j] = dPColor_collapse[i*dPColor_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}