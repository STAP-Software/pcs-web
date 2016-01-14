package org.tmt.aps.peas.lang.interop; 
public class JslopesNew
{
	public native void slopesNew(RetVal retVal, float x, float y, float ahex_mm, int ncoef_in, float uslope[], int uslope_size_1, float vslope[], int vslope_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jslopesNew(RetVal retVal, float x, float y, float ahex_mm, int ncoef_in, float uslope[], float vslope[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int uslope_len1 = uslope.length;
		int vslope_len1 = vslope.length;
		// Call native method
		slopesNew(retVal, x,y,ahex_mm,ncoef_in,uslope,uslope_len1,vslope,vslope_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}