package org.tmt.aps.peas.lang.interop; 
public class JsubInterpolate
{
	public native void subInterpolate(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, float x, float y, float total[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsubInterpolate(RetVal retVal, float frame[][], float x, float y ) {
		// Output variable definitions
		float total_outArray[] = new float[1];
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// Call native method
		subInterpolate(retVal, frame_collapse,frame_len1,frame_len2,x,y,total_outArray);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = total_outArray[0];
		return out;
	}
}