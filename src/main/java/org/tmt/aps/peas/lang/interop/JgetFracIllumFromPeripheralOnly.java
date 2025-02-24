package org.tmt.aps.peas.lang.interop; 
public class JgetFracIllumFromPeripheralOnly
{
	public native void getFracIllumFromPeripheralOnly(RetVal retVal, float spotintensity[], int spotintensity_size_1, int goodspots[], int goodspots_size_1, int spotType[], int spotType_size_1, float frac_illumination[], int frac_illumination_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetFracIllumFromPeripheralOnly(RetVal retVal, float spotintensity[], int goodspots[], int spotType[], float frac_illumination[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int spotintensity_len1 = spotintensity.length;
		int goodspots_len1 = goodspots.length;
		int spotType_len1 = spotType.length;
		int frac_illumination_len1 = frac_illumination.length;
		// Call native method
		getFracIllumFromPeripheralOnly(retVal, spotintensity,spotintensity_len1,goodspots,goodspots_len1,spotType,spotType_len1,frac_illumination,frac_illumination_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}