package org.tmt.aps.peas.lang.interop; 
public class JgetFracIlluminationAps
{
	public native void getFracIlluminationAps(RetVal retVal, float subimage_intensity[], int subimage_intensity_size_1, int n_start, int n_subimage, float frac_illumination[], int frac_illumination_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jgetFracIlluminationAps(RetVal retVal, float subimage_intensity[], int n_start, int n_subimage, float frac_illumination[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int subimage_intensity_len1 = subimage_intensity.length;
		int frac_illumination_len1 = frac_illumination.length;
		// Call native method
		getFracIlluminationAps(retVal, subimage_intensity,subimage_intensity_len1,n_start,n_subimage,frac_illumination,frac_illumination_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}