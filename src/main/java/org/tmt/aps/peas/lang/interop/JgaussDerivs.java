package org.tmt.aps.peas.lang.interop; 
public class JgaussDerivs
{
	public native void gaussDerivs(RetVal retVal, float X, float alpha[], int alpha_size_1, float z[], float dz[], int dz_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgaussDerivs(RetVal retVal, float X, float alpha[], float dz[] ) {
		// Output variable definitions
		float z_outArray[] = new float[1];
		// Deal with Array Lengths
		int alpha_len1 = alpha.length;
		int dz_len1 = dz.length;
		// Call native method
		gaussDerivs(retVal, X,alpha,alpha_len1,z_outArray,dz,dz_len1);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = z_outArray[0];
		return out;
	}
}