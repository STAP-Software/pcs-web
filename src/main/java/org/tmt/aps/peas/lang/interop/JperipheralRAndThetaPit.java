package org.tmt.aps.peas.lang.interop; 
public class JperipheralRAndThetaPit
{
	public native void peripheralRAndThetaPit(RetVal retVal, int activeSegments[], int activeSegments_size_1, float xcenter[], int xcenter_size_1, float ycenter[], int ycenter_size_1, float x_subap[], int x_subap_size_1, float y_subap[], int y_subap_size_1, int spot_type[], int spot_type_size_1, float theta_peri[], int theta_peri_size_1, float r_parallel[], int r_parallel_size_1, float r_perp[], int r_perp_size_1, int peripheral_segment[], int peripheral_segment_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jperipheralRAndThetaPit(RetVal retVal, int activeSegments[], float xcenter[], float ycenter[], float x_subap[], float y_subap[], int spot_type[], float theta_peri[], float r_parallel[], float r_perp[], int peripheral_segment[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int activeSegments_len1 = activeSegments.length;
		int xcenter_len1 = xcenter.length;
		int ycenter_len1 = ycenter.length;
		int x_subap_len1 = x_subap.length;
		int y_subap_len1 = y_subap.length;
		int spot_type_len1 = spot_type.length;
		int theta_peri_len1 = theta_peri.length;
		int r_parallel_len1 = r_parallel.length;
		int r_perp_len1 = r_perp.length;
		int peripheral_segment_len1 = peripheral_segment.length;
		// Call native method
		peripheralRAndThetaPit(retVal, activeSegments,activeSegments_len1,xcenter,xcenter_len1,ycenter,ycenter_len1,x_subap,x_subap_len1,y_subap,y_subap_len1,spot_type,spot_type_len1,theta_peri,theta_peri_len1,r_parallel,r_parallel_len1,r_perp,r_perp_len1,peripheral_segment,peripheral_segment_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}