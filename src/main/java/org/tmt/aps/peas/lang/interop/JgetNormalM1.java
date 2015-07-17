package org.tmt.aps.peas.lang.interop; 
public class JgetNormalM1
{
	public native void getNormalM1(RetVal retVal, double xx[], int xx_size_1, double rcurv[], int rcurv_size_1, double xk[], int xk_size_1, double beta[], int beta_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetNormalM1(RetVal retVal, double xx[], double rcurv[], double xk[], double beta[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xx_len1 = xx.length;
		int rcurv_len1 = rcurv.length;
		int xk_len1 = xk.length;
		int beta_len1 = beta.length;
		// Call native method
		getNormalM1(retVal, xx,xx_len1,rcurv,rcurv_len1,xk,xk_len1,beta,beta_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}