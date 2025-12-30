package org.tmt.aps.peas.lang.interop; 
public class JdropLowPeaks
{
	public native void dropLowPeaks(RetVal retVal, int npeak, int nxt[], int nxt_size_1, int nyt[], int nyt_size_1, float peak_value[], int peak_value_size_1, int min_no_peaks, int max_no_peaks, int npeakOutput[], int nxtOutput[], int nxtOutput_size_1, int nytOutput[], int nytOutput_size_1, float peak_valueOutput[], int peak_valueOutput_size_1, float peak_max[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jdropLowPeaks(RetVal retVal, int npeak, int nxt[], int nyt[], float peak_value[], int min_no_peaks, int max_no_peaks, int nxtOutput[], int nytOutput[], float peak_valueOutput[] ) {
		// Output variable definitions
		int npeakOutput_outArray[] = new int[1];
		float peak_max_outArray[] = new float[1];
		// Deal with Array Lengths
		int nxt_len1 = nxt.length;
		int nyt_len1 = nyt.length;
		int peak_value_len1 = peak_value.length;
		int nxtOutput_len1 = nxtOutput.length;
		int nytOutput_len1 = nytOutput.length;
		int peak_valueOutput_len1 = peak_valueOutput.length;
		// Call native method
		dropLowPeaks(retVal, npeak,nxt,nxt_len1,nyt,nyt_len1,peak_value,peak_value_len1,min_no_peaks,max_no_peaks,npeakOutput_outArray,nxtOutput,nxtOutput_len1,nytOutput,nytOutput_len1,peak_valueOutput,peak_valueOutput_len1,peak_max_outArray);
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = npeakOutput_outArray[0];
		out[1] = peak_max_outArray[0];
		return out;
	}
}