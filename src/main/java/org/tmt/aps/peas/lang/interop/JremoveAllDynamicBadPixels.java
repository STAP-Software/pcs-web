package org.tmt.aps.peas.lang.interop; 
public class JremoveAllDynamicBadPixels
{
	public native void removeAllDynamicBadPixels(RetVal retVal, int arrayIn[], int arrayIn_size_1, int arrayIn_size_2, int removeBadPixelsFlag, float indexThreshold, int intensityThreshold, int badPixelIterationLimit, int arrayOut[], int arrayOut_size_1, int arrayOut_size_2, int badPixelLocationsX[], int badPixelLocationsX_size_1, int badPixelLocationsY[], int badPixelLocationsY_size_1, int badPixelCount[], int allBadPixelsFound[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jremoveAllDynamicBadPixels(RetVal retVal, int arrayIn[][], int removeBadPixelsFlag, float indexThreshold, int intensityThreshold, int badPixelIterationLimit, int arrayOut[][], int badPixelLocationsX[], int badPixelLocationsY[] ) {
		// Output variable definitions
		int badPixelCount_outArray[] = new int[1];
		int allBadPixelsFound_outArray[] = new int[1];
		// Deal with Array Lengths
		int arrayIn_len1 = arrayIn.length;
		int arrayIn_len2 = arrayIn[0].length;
		int[] arrayIn_collapse = new int[arrayIn_len1 * arrayIn_len2];
		int arrayOut_len1 = arrayOut.length;
		int arrayOut_len2 = arrayOut[0].length;
		int[] arrayOut_collapse = new int[arrayOut_len1 * arrayOut_len2];
		int badPixelLocationsX_len1 = badPixelLocationsX.length;
		int badPixelLocationsY_len1 = badPixelLocationsY.length;
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
		removeAllDynamicBadPixels(retVal, arrayIn_collapse,arrayIn_len1,arrayIn_len2,removeBadPixelsFlag,indexThreshold,intensityThreshold,badPixelIterationLimit,arrayOut_collapse,arrayOut_len1,arrayOut_len2,badPixelLocationsX,badPixelLocationsX_len1,badPixelLocationsY,badPixelLocationsY_len1,badPixelCount_outArray,allBadPixelsFound_outArray);
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
		Object[] out = new Object[2];
		out[0] = badPixelCount_outArray[0];
		out[1] = allBadPixelsFound_outArray[0];
		return out;
	}
}