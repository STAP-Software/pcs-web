package org.tmt.aps.peas.lang.interop; 
public class JfindAndIdentify
{
	public native void findAndIdentify(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, float centroids[], int centroids_size_1, int centroids_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfindAndIdentify(RetVal retVal, float frame[][], float centroids[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		int centroids_len1 = centroids.length;
		int centroids_len2 = centroids[0].length;
		float[] centroids_collapse = new float[centroids_len1 * centroids_len2];
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<centroids_len1; i++) { 
			for (int j=0; j<centroids_len2; j++) { 
				centroids_collapse[i*centroids_len2 + j] = centroids[i][j]; 
			} 
		} 
		// Call native method
		findAndIdentify(retVal, frame_collapse,frame_len1,frame_len2,centroids_collapse,centroids_len1,centroids_len2);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<centroids_len1; i++) { 
			for (int j=0; j<centroids_len2; j++) { 
				centroids[i][j] = centroids_collapse[i*centroids_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}