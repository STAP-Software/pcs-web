package org.tmt.aps.peas.lang.interop; 
public class JmakeVirtualCcd
{
	public native void makeVirtualCcd(RetVal retVal, int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, int npeak, float virtual_ccd[], int virtual_ccd_size_1, int virtual_ccd_size_2, int label_offset[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmakeVirtualCcd(RetVal retVal, int nxt[], int nyt[], int npeak, float virtual_ccd[][] ) {
		// Output variable definitions
		int label_offset_outArray[] = new int[1];
		// Deal with Array Lengths
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		int virtual_ccd_len1 = virtual_ccd.length;
		int virtual_ccd_len2 = virtual_ccd[0].length;
		float[] virtual_ccd_collapse = new float[virtual_ccd_len1 * virtual_ccd_len2];
		// collapse array to one dimension
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd_collapse[i*virtual_ccd_len2 + j] = virtual_ccd[i][j]; 
			} 
		} 
		// Call native method
		makeVirtualCcd(retVal, nxt,nxt_len1,nyt,nyt_len1,npeak,virtual_ccd_collapse,virtual_ccd_len1,virtual_ccd_len2,label_offset_outArray);
		// expand array to two dimensions
		for (int i=0; i<virtual_ccd_len1; i++) { 
			for (int j=0; j<virtual_ccd_len2; j++) { 
				virtual_ccd[i][j] = virtual_ccd_collapse[i*virtual_ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = label_offset_outArray[0];
		return out;
	}
}