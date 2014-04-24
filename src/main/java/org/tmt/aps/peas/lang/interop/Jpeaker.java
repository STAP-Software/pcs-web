package org.tmt.aps.peas.lang.interop; 
public class Jpeaker
{
	public native void peaker(RetVal retVal, float zimage[], int zimage_size_1, int zimage_size_2, float thresh, int npeak[], int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, float peak_values[], int peak_values_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jpeaker(RetVal retVal, float zimage[][], float thresh, int nxt[], int nyt[], float peak_values[] ) {
		// Output variable definitions
		int npeak_outArray[] = new int[1];
		// Deal with Array Lengths
		int zimage_len1 = zimage.length;
		int zimage_len2 = zimage[0].length;
		float[] zimage_collapse = new float[zimage_len1 * zimage_len2];
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		int peak_values_len1 = peak_values.length;
		// collapse array to one dimension
		for (int i=0; i<zimage_len1; i++) { 
			for (int j=0; j<zimage_len2; j++) { 
				zimage_collapse[i*zimage_len2 + j] = zimage[i][j]; 
			} 
		} 
		// Call native method
		peaker(retVal, zimage_collapse,zimage_len1,zimage_len2,thresh,npeak_outArray,nxt,nxt_len1,nyt,nyt_len1,peak_values,peak_values_len1);
		// expand array to two dimensions
		for (int i=0; i<zimage_len1; i++) { 
			for (int j=0; j<zimage_len2; j++) { 
				zimage[i][j] = zimage_collapse[i*zimage_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = npeak_outArray[0];
		return out;
	}
}