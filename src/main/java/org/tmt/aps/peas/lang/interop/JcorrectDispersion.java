package org.tmt.aps.peas.lang.interop; 
public class JcorrectDispersion
{
	public native void correctDispersion(RetVal retVal, float xin[], int xin_size_1, float coeff, float ring_mode[], int ring_mode_size_1, int row_flag[], int row_flag_size_1, float xout[], int xout_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcorrectDispersion(RetVal retVal, float xin[], float coeff, float ring_mode[], int row_flag[], float xout[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xin_len1 = xin.length;
		int ring_mode_len1 = ring_mode.length;
		int row_flag_len1 = row_flag.length;
		int xout_len1 = xout.length;
		// Call native method
		correctDispersion(retVal, xin,xin_len1,coeff,ring_mode,ring_mode_len1,row_flag,row_flag_len1,xout,xout_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}