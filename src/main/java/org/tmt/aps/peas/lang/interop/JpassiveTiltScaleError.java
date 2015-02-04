package org.tmt.aps.peas.lang.interop; 
public class JpassiveTiltScaleError
{
	public native void passiveTiltScaleError(RetVal retVal, float offsets[], int offsets_size_1, int offsets_size_2, float x_spot[], int x_spot_size_1, float y_spot[], int y_spot_size_1, float brms[], float fslope[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jpassiveTiltScaleError(RetVal retVal, float offsets[][], float x_spot[], float y_spot[] ) {
		// Output variable definitions
		float brms_outArray[] = new float[1];
		float fslope_outArray[] = new float[1];
		// Deal with Array Lengths
		int offsets_len1 = offsets.length;
		int offsets_len2 = offsets[0].length;
		float[] offsets_collapse = new float[offsets_len1 * offsets_len2];
		int x_spot_len1 = x_spot.length;
		int y_spot_len1 = y_spot.length;
		// collapse array to one dimension
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets_collapse[i*offsets_len2 + j] = offsets[i][j]; 
			} 
		} 
		// Call native method
		passiveTiltScaleError(retVal, offsets_collapse,offsets_len1,offsets_len2,x_spot,x_spot_len1,y_spot,y_spot_len1,brms_outArray,fslope_outArray);
		// expand array to two dimensions
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets[i][j] = offsets_collapse[i*offsets_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = brms_outArray[0];
		out[1] = fslope_outArray[0];
		return out;
	}
}