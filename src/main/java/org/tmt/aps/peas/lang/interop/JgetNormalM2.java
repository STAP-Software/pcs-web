package org.tmt.aps.peas.lang.interop; 
public class JgetNormalM2
{
	public native void getNormalM2(RetVal retVal, double xx[], int xx_size_1, double rcurv[], int rcurv_size_1, double xk[], int xk_size_1, double h2, double delta_x, double delta_y, double delta_z, double epsilon_x, double epsilon_y, double beta_out[], int beta_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetNormalM2(RetVal retVal, double xx[], double rcurv[], double xk[], double h2, double delta_x, double delta_y, double delta_z, double epsilon_x, double epsilon_y, double beta_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xx_len1 = xx.length;
		int rcurv_len1 = rcurv.length;
		int xk_len1 = xk.length;
		int beta_out_len1 = beta_out.length;
		// Call native method
		getNormalM2(retVal, xx,xx_len1,rcurv,rcurv_len1,xk,xk_len1,h2,delta_x,delta_y,delta_z,epsilon_x,epsilon_y,beta_out,beta_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}