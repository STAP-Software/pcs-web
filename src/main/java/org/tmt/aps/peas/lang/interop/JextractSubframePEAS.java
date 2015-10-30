package org.tmt.aps.peas.lang.interop; 
public class JextractSubframePEAS
{
	public native void extractSubframePEAS(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, float x0, float y0, float subframe[], int subframe_size_1, int subframe_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jextractSubframePEAS(RetVal retVal, float frame[][], float x0, float y0, float subframe[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		int subframe_len1 = subframe.length;
		int subframe_len2 = subframe[0].length;
		float[] subframe_collapse = new float[subframe_len1 * subframe_len2];
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<subframe_len1; i++) { 
			for (int j=0; j<subframe_len2; j++) { 
				subframe_collapse[i*subframe_len2 + j] = subframe[i][j]; 
			} 
		} 
		// Call native method
		extractSubframePEAS(retVal, frame_collapse,frame_len1,frame_len2,x0,y0,subframe_collapse,subframe_len1,subframe_len2);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<subframe_len1; i++) { 
			for (int j=0; j<subframe_len2; j++) { 
				subframe[i][j] = subframe_collapse[i*subframe_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}