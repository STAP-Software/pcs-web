package org.tmt.aps.peas.lang.interop; 
public class JremoveBadPixels
{
	public native void removeBadPixels(RetVal retVal, int arrayIn[], int arrayIn_size_1, int arrayIn_size_2, int boundingBoxPixelLocationX1[], int boundingBoxPixelLocationX1_size_1, int boundingBoxPixelLocationY1[], int boundingBoxPixelLocationY1_size_1, int boundingBoxPixelLocationX2[], int boundingBoxPixelLocationX2_size_1, int boundingBoxPixelLocationY2[], int boundingBoxPixelLocationY2_size_1, int arrayOut[], int arrayOut_size_1, int arrayOut_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jremoveBadPixels(RetVal retVal, int arrayIn[][], int boundingBoxPixelLocationX1[], int boundingBoxPixelLocationY1[], int boundingBoxPixelLocationX2[], int boundingBoxPixelLocationY2[], int arrayOut[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int arrayIn_len1 = arrayIn.length;
		int arrayIn_len2 = arrayIn[0].length;
		int[] arrayIn_collapse = new int[arrayIn_len1 * arrayIn_len2];
		int boundingBoxPixelLocationX1_len1 = boundingBoxPixelLocationX1.length;
		int boundingBoxPixelLocationY1_len1 = boundingBoxPixelLocationY1.length;
		int boundingBoxPixelLocationX2_len1 = boundingBoxPixelLocationX2.length;
		int boundingBoxPixelLocationY2_len1 = boundingBoxPixelLocationY2.length;
		int arrayOut_len1 = arrayOut.length;
		int arrayOut_len2 = arrayOut[0].length;
		int[] arrayOut_collapse = new int[arrayOut_len1 * arrayOut_len2];
		// collapse array to one dimension
		for (int i=0; i<arrayIn_len1; i++) { 
			for (int j=0; j<arrayIn_len2; j++) { 
				arrayIn_collapse[i*arrayIn_len2 + j] = arrayIn[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<arrayOut_len1; i++) { 
			for (int j=0; j<arrayOut_len2; j++) { 
				arrayOut_collapse[i*arrayOut_len2 + j] = arrayOut[i][j]; 
			} 
		} 
		// Call native method
		removeBadPixels(retVal, arrayIn_collapse,arrayIn_len1,arrayIn_len2,boundingBoxPixelLocationX1,boundingBoxPixelLocationX1_len1,boundingBoxPixelLocationY1,boundingBoxPixelLocationY1_len1,boundingBoxPixelLocationX2,boundingBoxPixelLocationX2_len1,boundingBoxPixelLocationY2,boundingBoxPixelLocationY2_len1,arrayOut_collapse,arrayOut_len1,arrayOut_len2);
		// expand array to two dimensions
		for (int i=0; i<arrayIn_len1; i++) { 
			for (int j=0; j<arrayIn_len2; j++) { 
				arrayIn[i][j] = arrayIn_collapse[i*arrayIn_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<arrayOut_len1; i++) { 
			for (int j=0; j<arrayOut_len2; j++) { 
				arrayOut[i][j] = arrayOut_collapse[i*arrayOut_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}