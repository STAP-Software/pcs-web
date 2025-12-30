package org.tmt.aps.peas.lang.interop; 
public class JperipheralRAndThetaPsh
{
	public native void peripheralRAndThetaPsh(RetVal retVal, int segment_flag[], int segment_flag_size_1, float xcenter[], int xcenter_size_1, float ycenter[], int ycenter_size_1, float px[], int px_size_1, float py[], int py_size_1, float theta_peri[], int theta_peri_size_1, float r_parallel[], int r_parallel_size_1, float r_perp[], int r_perp_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jperipheralRAndThetaPsh(RetVal retVal, int segment_flag[], float xcenter[], float ycenter[], float px[], float py[], float theta_peri[], float r_parallel[], float r_perp[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int segment_flag_len1 = segment_flag.length;
		int xcenter_len1 = xcenter.length;
		int ycenter_len1 = ycenter.length;
		int px_len1 = px.length;
		int py_len1 = py.length;
		int theta_peri_len1 = theta_peri.length;
		int r_parallel_len1 = r_parallel.length;
		int r_perp_len1 = r_perp.length;
		// Call native method
		peripheralRAndThetaPsh(retVal, segment_flag,segment_flag_len1,xcenter,xcenter_len1,ycenter,ycenter_len1,px,px_len1,py,py_len1,theta_peri,theta_peri_len1,r_parallel,r_parallel_len1,r_perp,r_perp_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}