package org.tmt.aps.peas.lang.interop; 
public class JdoesSubapLieInSeg
{
	public native void doesSubapLieInSeg(RetVal retVal, float xap, float yap, float xseg, float yseg, float ahex, int n[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jdoesSubapLieInSeg(RetVal retVal, float xap, float yap, float xseg, float yseg, float ahex ) {
		// Output variable definitions
		int n_outArray[] = new int[1];
		// Deal with Array Lengths
		// Call native method
		doesSubapLieInSeg(retVal, xap,yap,xseg,yseg,ahex,n_outArray);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = n_outArray[0];
		return out;
	}
}