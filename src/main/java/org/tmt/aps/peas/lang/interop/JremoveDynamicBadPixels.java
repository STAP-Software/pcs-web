package org.tmt.aps.peas.lang.interop; 
public class JremoveDynamicBadPixels
{
	public native void removeDynamicBadPixels(RetVal retVal, int arrayIn[], int arrayIn_size_1, int arrayIn_size_2, int removeBadPixelsFlag, float indexThreshold, int intensityThreshold, int arrayOut[], int arrayOut_size_1, int arrayOut_size_2, int badPixelLocationsX[], int badPixelLocationsX_size_1, int badPixelLocationsy[], int badPixelLocationsy_size_1, int badPixelCount[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jremoveDynamicBadPixels(RetVal retVal, int arrayIn[][], int removeBadPixelsFlag, float indexThreshold, int intensityThreshold, int arrayOut[][], int badPixelLocationsX[], int badPixelLocationsy[] ) {
		// Output variable definitions
		int badPixelCount_outArray[] = new int[1];
		// Deal with Array Lengths
		int arrayIn_len1 = arrayIn.length;
		int arrayIn_len2 = arrayIn[0].length;
		int[] arrayIn_collapse = new int[arrayIn_len1 * arrayIn_len2];
		int arrayOut_len1 = arrayOut.length;
		int arrayOut_len2 = arrayOut[0].length;
		int[] arrayOut_collapse = new int[arrayOut_len1 * arrayOut_len2];
		int badPixelLocationsX_len1 = badPixelLocationsX.length;
		int badPixelLocationsy_len1 = badPixelLocationsy.length;
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
		removeDynamicBadPixels(retVal, arrayIn_collapse,arrayIn_len1,arrayIn_len2,removeBadPixelsFlag,indexThreshold,intensityThreshold,arrayOut_collapse,arrayOut_len1,arrayOut_len2,badPixelLocationsX,badPixelLocationsX_len1,badPixelLocationsy,badPixelLocationsy_len1,badPixelCount_outArray);
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
		Object[] out = new Object[1];
		out[0] = badPixelCount_outArray[0];
		return out;
	}
}