package org.tmt.aps.peas.lang.interop; 
public class JoldPcsThreshold
{
	public native void oldPcsThreshold(RetVal retVal, float keck_frame[], int keck_frame_size_1, int keck_frame_size_2, float old_thresh[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] joldPcsThreshold(RetVal retVal, float keck_frame[][] ) {
		// Output variable definitions
		float old_thresh_outArray[] = new float[1];
		// Deal with Array Lengths
		int keck_frame_len1 = keck_frame.length;
		int keck_frame_len2 = keck_frame[0].length;
		float[] keck_frame_collapse = new float[keck_frame_len1 * keck_frame_len2];
		// collapse array to one dimension
		for (int i=0; i<keck_frame_len1; i++) { 
			for (int j=0; j<keck_frame_len2; j++) { 
				keck_frame_collapse[i*keck_frame_len2 + j] = keck_frame[i][j]; 
			} 
		} 
		// Call native method
		oldPcsThreshold(retVal, keck_frame_collapse,keck_frame_len1,keck_frame_len2,old_thresh_outArray);
		// expand array to two dimensions
		for (int i=0; i<keck_frame_len1; i++) { 
			for (int j=0; j<keck_frame_len2; j++) { 
				keck_frame[i][j] = keck_frame_collapse[i*keck_frame_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = old_thresh_outArray[0];
		return out;
	}
}