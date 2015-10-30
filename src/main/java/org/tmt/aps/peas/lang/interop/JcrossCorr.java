package org.tmt.aps.peas.lang.interop; 
public class JcrossCorr
{
	public native void crossCorr(RetVal retVal, float x[], int x_size_1, int x_size_2, float y[], int y_size_1, int y_size_2, float c[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcrossCorr(RetVal retVal, float x[][], float y[][] ) {
		// Output variable definitions
		float c_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_len1 = x.length;
		int x_len2 = x[0].length;
		float[] x_collapse = new float[x_len1 * x_len2];
		int y_len1 = y.length;
		int y_len2 = y[0].length;
		float[] y_collapse = new float[y_len1 * y_len2];
		// collapse array to one dimension
		for (int i=0; i<x_len1; i++) { 
			for (int j=0; j<x_len2; j++) { 
				x_collapse[i*x_len2 + j] = x[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<y_len1; i++) { 
			for (int j=0; j<y_len2; j++) { 
				y_collapse[i*y_len2 + j] = y[i][j]; 
			} 
		} 
		// Call native method
		crossCorr(retVal, x_collapse,x_len1,x_len2,y_collapse,y_len1,y_len2,c_outArray);
		// expand array to two dimensions
		for (int i=0; i<x_len1; i++) { 
			for (int j=0; j<x_len2; j++) { 
				x[i][j] = x_collapse[i*x_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<y_len1; i++) { 
			for (int j=0; j<y_len2; j++) { 
				y[i][j] = y_collapse[i*y_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = c_outArray[0];
		return out;
	}
}