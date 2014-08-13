package org.tmt.aps.peas.lang.interop; 
public class JcalculateCentroidOffsets
{
	public native void calculateCentroidOffsets(RetVal retVal, float centroid[], int centroid_size_1, int centroid_size_2, float ref_cent[], int ref_cent_size_1, int ref_cent_size_2, int remove_scale, int remove_rotation, int good_spots[], int good_spots_size_1, float offsets[], int offsets_size_1, int offsets_size_2, float image_translation[], int image_translation_size_1, float image_scale[], float image_rotation[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcalculateCentroidOffsets(RetVal retVal, float centroid[][], float ref_cent[][], int remove_scale, int remove_rotation, int good_spots[], float offsets[][], float image_translation[] ) {
		// Output variable definitions
		float image_scale_outArray[] = new float[1];
		float image_rotation_outArray[] = new float[1];
		// Deal with Array Lengths
		int centroid_len1 = centroid.length;
		int centroid_len2 = centroid[0].length;
		float[] centroid_collapse = new float[centroid_len1 * centroid_len2];
		int ref_cent_len1 = ref_cent.length;
		int ref_cent_len2 = ref_cent[0].length;
		float[] ref_cent_collapse = new float[ref_cent_len1 * ref_cent_len2];
		int good_spots_len1 = good_spots.length;
		int offsets_len1 = offsets.length;
		int offsets_len2 = offsets[0].length;
		float[] offsets_collapse = new float[offsets_len1 * offsets_len2];
		int image_translation_len1 = image_translation.length;
		// collapse array to one dimension
		for (int i=0; i<centroid_len1; i++) { 
			for (int j=0; j<centroid_len2; j++) { 
				centroid_collapse[i*centroid_len2 + j] = centroid[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<ref_cent_len1; i++) { 
			for (int j=0; j<ref_cent_len2; j++) { 
				ref_cent_collapse[i*ref_cent_len2 + j] = ref_cent[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets_collapse[i*offsets_len2 + j] = offsets[i][j]; 
			} 
		} 
		// Call native method
		calculateCentroidOffsets(retVal, centroid_collapse,centroid_len1,centroid_len2,ref_cent_collapse,ref_cent_len1,ref_cent_len2,remove_scale,remove_rotation,good_spots,good_spots_len1,offsets_collapse,offsets_len1,offsets_len2,image_translation,image_translation_len1,image_scale_outArray,image_rotation_outArray);
		// expand array to two dimensions
		for (int i=0; i<centroid_len1; i++) { 
			for (int j=0; j<centroid_len2; j++) { 
				centroid[i][j] = centroid_collapse[i*centroid_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<ref_cent_len1; i++) { 
			for (int j=0; j<ref_cent_len2; j++) { 
				ref_cent[i][j] = ref_cent_collapse[i*ref_cent_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<offsets_len1; i++) { 
			for (int j=0; j<offsets_len2; j++) { 
				offsets[i][j] = offsets_collapse[i*offsets_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = image_scale_outArray[0];
		out[1] = image_rotation_outArray[0];
		return out;
	}
}