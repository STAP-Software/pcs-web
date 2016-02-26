package org.tmt.aps.peas.lang.interop; 
public class JzernikeGradient
{
	public native void zernikeGradient(RetVal retVal, float x_in, float y_in, float ahex_mm, int ncoef_in, float dzdx_out[], int dzdx_out_size_1, float dzdy_out[], int dzdy_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jzernikeGradient(RetVal retVal, float x_in, float y_in, float ahex_mm, int ncoef_in, float dzdx_out[], float dzdy_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int dzdx_out_len1 = dzdx_out.length;
		int dzdy_out_len1 = dzdy_out.length;
		// Call native method
		zernikeGradient(retVal, x_in,y_in,ahex_mm,ncoef_in,dzdx_out,dzdx_out_len1,dzdy_out,dzdy_out_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}