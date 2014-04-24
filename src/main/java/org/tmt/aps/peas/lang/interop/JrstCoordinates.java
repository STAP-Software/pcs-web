package org.tmt.aps.peas.lang.interop; 
public class JrstCoordinates
{
	public native void rstCoordinates(RetVal retVal, float xp_subap[], int xp_subap_size_1, float yp_subap[], int yp_subap_size_1, int icenter, int jcenter, float phi_meas, float scale_meas, int n_subap, float xi_rst[], int xi_rst_size_1, float yi_rst[], int yi_rst_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jrstCoordinates(RetVal retVal, float xp_subap[], float yp_subap[], int icenter, int jcenter, float phi_meas, float scale_meas, int n_subap, float xi_rst[], float yi_rst[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xp_subap_len1 = xp_subap.length;
		int yp_subap_len1 = yp_subap.length;
		int xi_rst_len1 = xi_rst.length;
		int yi_rst_len1 = yi_rst.length;
		// Call native method
		rstCoordinates(retVal, xp_subap,xp_subap_len1,yp_subap,yp_subap_len1,icenter,jcenter,phi_meas,scale_meas,n_subap,xi_rst,xi_rst_len1,yi_rst,yi_rst_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}