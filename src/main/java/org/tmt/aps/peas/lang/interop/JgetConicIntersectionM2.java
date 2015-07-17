package org.tmt.aps.peas.lang.interop; 
public class JgetConicIntersectionM2
{
	public native void getConicIntersectionM2(RetVal retVal, double delta_x, double delta_y, double delta_z, double epsilon_x, double epsilon_y, double h2, double xx_in[], int xx_in_size_1, double xk[], int xk_size_1, double alpha[], int alpha_size_1, double rcurv[], int rcurv_size_1, double xx_out[], int xx_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetConicIntersectionM2(RetVal retVal, double delta_x, double delta_y, double delta_z, double epsilon_x, double epsilon_y, double h2, double xx_in[], double xk[], double alpha[], double rcurv[], double xx_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xx_in_len1 = xx_in.length;
		int xk_len1 = xk.length;
		int alpha_len1 = alpha.length;
		int rcurv_len1 = rcurv.length;
		int xx_out_len1 = xx_out.length;
		// Call native method
		getConicIntersectionM2(retVal, delta_x,delta_y,delta_z,epsilon_x,epsilon_y,h2,xx_in,xx_in_len1,xk,xk_len1,alpha,alpha_len1,rcurv,rcurv_len1,xx_out,xx_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}