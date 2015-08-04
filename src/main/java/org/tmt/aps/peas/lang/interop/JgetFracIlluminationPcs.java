package org.tmt.aps.peas.lang.interop; 
public class JgetFracIlluminationPcs
{
	public native void getFracIlluminationPcs(RetVal retVal, float peripheral_int[], int peripheral_int_size_1, int good_spots[], int good_spots_size_1, float frac_illumination[], int frac_illumination_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetFracIlluminationPcs(RetVal retVal, float peripheral_int[], int good_spots[], float frac_illumination[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int peripheral_int_len1 = peripheral_int.length;
		int good_spots_len1 = good_spots.length;
		int frac_illumination_len1 = frac_illumination.length;
		// Call native method
		getFracIlluminationPcs(retVal, peripheral_int,peripheral_int_len1,good_spots,good_spots_len1,frac_illumination,frac_illumination_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}