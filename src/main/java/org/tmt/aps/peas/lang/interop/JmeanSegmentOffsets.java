package org.tmt.aps.peas.lang.interop; 
public class JmeanSegmentOffsets
{
	public native void meanSegmentOffsets(RetVal retVal, float ximage[], int ximage_size_1, float yimage[], int yimage_size_1, int row_flag_det[], int row_flag_det_size_1, float xtt[], int xtt_size_1, float ytt[], int ytt_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmeanSegmentOffsets(RetVal retVal, float ximage[], float yimage[], int row_flag_det[], float xtt[], float ytt[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int ximage_len1 = ximage.length;
		int yimage_len1 = yimage.length;
		int row_flag_det_len1 = row_flag_det.length;
		int xtt_len1 = xtt.length;
		int ytt_len1 = ytt.length;
		// Call native method
		meanSegmentOffsets(retVal, ximage,ximage_len1,yimage,yimage_len1,row_flag_det,row_flag_det_len1,xtt,xtt_len1,ytt,ytt_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}