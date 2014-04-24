package org.tmt.aps.peas.lang.interop; 
public class JgFit
{
	public native void gFit(RetVal retVal, int niter, int nrow, float X[], int X_size_1, float Z[], int Z_size_1, float alpha0[], int alpha0_size_1, float alpha[], int alpha_size_1, int gflag[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgFit(RetVal retVal, int niter, int nrow, float X[], float Z[], float alpha0[], float alpha[] ) {
		// Output variable definitions
		int gflag_outArray[] = new int[1];
		// Deal with Array Lengths
		int X_len1 = X.length;
		int Z_len1 = Z.length;
		int alpha0_len1 = alpha0.length;
		int alpha_len1 = alpha.length;
		// Call native method
		gFit(retVal, niter,nrow,X,X_len1,Z,Z_len1,alpha0,alpha0_len1,alpha,alpha_len1,gflag_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = gflag_outArray[0];
		return out;
	}
}