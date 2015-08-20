package org.tmt.aps.peas.lang.interop; 
public class JcalculateCentroidStats
{
	public native void calculateCentroidStats(RetVal retVal, float offsets[], int offsets_size_1, int offsets_size_2, int good_spots[], int good_spots_size_1, int spot_types[], int spot_types_size_1, int max_spot_number[], float max_offset[], float rrms_total[], float enclosed_energy[], float enclosed_50_energy[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcalculateCentroidStats(RetVal retVal, float offsets[][], int good_spots[], int spot_types[] ) {
		// Output variable definitions
		int max_spot_number_outArray[] = new int[1];
		float max_offset_outArray[] = new float[1];
		float rrms_total_outArray[] = new float[1];
		float enclosed_energy_outArray[] = new float[1];
		float enclosed_50_energy_outArray[] = new float[1];
		// Deal with Array Lengths
		int offsets_len1 = offsets.length;
		int offsets_len2 = offsets[0].length;
		float[] offsets_collapse = new float[offsets_len1 * offsets_len2];
		int good_spots_len1 = good_spots.length;
		int spot_types_len1 = spot_types.length;
		// collapse array to one dimension
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets_collapse[i*offsets_len2 + j] = offsets[i][j]; 
			} 
		} 
		// Call native method
		calculateCentroidStats(retVal, offsets_collapse,offsets_len1,offsets_len2,good_spots,good_spots_len1,spot_types,spot_types_len1,max_spot_number_outArray,max_offset_outArray,rrms_total_outArray,enclosed_energy_outArray,enclosed_50_energy_outArray);
		// expand array to two dimensions
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets[i][j] = offsets_collapse[i*offsets_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[5];
		out[0] = max_spot_number_outArray[0];
		out[1] = max_offset_outArray[0];
		out[2] = rrms_total_outArray[0];
		out[3] = enclosed_energy_outArray[0];
		out[4] = enclosed_50_energy_outArray[0];
		return out;
	}
}