package org.tmt.aps.peas.lang.interop; 
public class JzeroMeanSegmentCentroids
{
	public native void zeroMeanSegmentCentroids(RetVal retVal, float ximage[], int ximage_size_1, float yimage[], int yimage_size_1, int row_flag[], int row_flag_size_1, int nlenslet, int nsegment, float ximage_smz[], int ximage_smz_size_1, float yimage_smz[], int yimage_smz_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jzeroMeanSegmentCentroids(RetVal retVal, float ximage[], float yimage[], int row_flag[], int nlenslet, int nsegment, float ximage_smz[], float yimage_smz[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int ximage_len1 = ximage.length;
		int yimage_len1 = yimage.length;
		int row_flag_len1 = row_flag.length;
		int ximage_smz_len1 = ximage_smz.length;
		int yimage_smz_len1 = yimage_smz.length;
		// Call native method
		zeroMeanSegmentCentroids(retVal, ximage,ximage_len1,yimage,yimage_len1,row_flag,row_flag_len1,nlenslet,nsegment,ximage_smz,ximage_smz_len1,yimage_smz,yimage_smz_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}