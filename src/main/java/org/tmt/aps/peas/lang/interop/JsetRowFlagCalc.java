package org.tmt.aps.peas.lang.interop; 
public class JsetRowFlagCalc
{
	public native void setRowFlagCalc(RetVal retVal, int nsegment, int nlenslet, int row_flag_det[], int row_flag_det_size_1, int row_flag_calc[], int row_flag_calc_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jsetRowFlagCalc(RetVal retVal, int nsegment, int nlenslet, int row_flag_det[], int row_flag_calc[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int row_flag_det_len1 = row_flag_det.length;
		int row_flag_calc_len1 = row_flag_calc.length;
		// Call native method
		setRowFlagCalc(retVal, nsegment,nlenslet,row_flag_det,row_flag_det_len1,row_flag_calc,row_flag_calc_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}