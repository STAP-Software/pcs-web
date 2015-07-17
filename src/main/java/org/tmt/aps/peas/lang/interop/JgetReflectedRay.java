package org.tmt.aps.peas.lang.interop; 
public class JgetReflectedRay
{
	public native void getReflectedRay(RetVal retVal, double alpha[], int alpha_size_1, double beta[], int beta_size_1, double gamma[], int gamma_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetReflectedRay(RetVal retVal, double alpha[], double beta[], double gamma[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int alpha_len1 = alpha.length;
		int beta_len1 = beta.length;
		int gamma_len1 = gamma.length;
		// Call native method
		getReflectedRay(retVal, alpha,alpha_len1,beta,beta_len1,gamma,gamma_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}