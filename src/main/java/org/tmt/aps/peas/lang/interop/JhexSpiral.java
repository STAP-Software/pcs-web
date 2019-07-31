package org.tmt.aps.peas.lang.interop; 
public class JhexSpiral
{
	public native void hexSpiral(RetVal retVal, int lensletOrientation, int spiralRingCount, float x_spiral[], int x_spiral_size_1, float y_spiral[], int y_spiral_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jhexSpiral(RetVal retVal, int lensletOrientation, int spiralRingCount, float x_spiral[], float y_spiral[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_spiral_len1 = x_spiral.length;
		int y_spiral_len1 = y_spiral.length;
		// Call native method
		hexSpiral(retVal, lensletOrientation,spiralRingCount,x_spiral,x_spiral_len1,y_spiral,y_spiral_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}