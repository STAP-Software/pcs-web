package org.tmt.aps.peas.lang.interop; 
public class JdropLowPeaks
{
	public native void dropLowPeaks(RetVal retVal, int npeak, int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, float peak_values[], int peak_values_size_1, int min_no_peak, int max_no_peak, int npeakOutput[], int nxtOutput[], int nxtOutput_size_1, int nytOutput[], int nytOutput_size_1, float peak_valuesOutput[], int peak_valuesOutput_size_1, float peak_max[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jdropLowPeaks(RetVal retVal, int npeak, int nxt[], int nyt[], float peak_values[], int min_no_peak, int max_no_peak, int nxtOutput[], int nytOutput[], float peak_valuesOutput[] ) {
		// Output variable definitions
		int npeakOutput_outArray[] = new int[1];
		float peak_max_outArray[] = new float[1];
		// Deal with Array Lengths
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		int peak_values_len1 = peak_values.length;
		int nxtOutput_len1 = nxtOutput.length;
		int nytOutput_len1 = nytOutput.length;
		int peak_valuesOutput_len1 = peak_valuesOutput.length;
		// Call native method
		dropLowPeaks(retVal, npeak,nxt,nxt_len1,nyt,nyt_len1,peak_values,peak_values_len1,min_no_peak,max_no_peak,npeakOutput_outArray,nxtOutput,nxtOutput_len1,nytOutput,nytOutput_len1,peak_valuesOutput,peak_valuesOutput_len1,peak_max_outArray);
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = npeakOutput_outArray[0];
		out[1] = peak_max_outArray[0];
		return out;
	}
}