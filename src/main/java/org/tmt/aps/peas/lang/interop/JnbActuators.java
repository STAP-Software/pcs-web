package org.tmt.aps.peas.lang.interop; 
public class JnbActuators
{
	public native void nbActuators(RetVal retVal, float nb_step[], int nb_step_size_1, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float acsa[], int acsa_size_1, int acsa_size_2, int activeSegments[], int activeSegments_size_1, int plusPiston[], int plusPiston_size_1, int minusPiston[], int minusPiston_size_1, int constrainedSegmentCount[], float actCalc[], int actCalc_size_1, float resid[], int resid_size_1, float segmentPistonRms[], int numberOfIslands[], int SegmentIslandNumber[], int SegmentIslandNumber_size_1, int islandSegmentCount[], int islandSegmentCount_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbActuators(RetVal retVal, float nb_step[], int row_flag[], int col_flag[], float acsa[][], int activeSegments[], int plusPiston[], int minusPiston[], float actCalc[], float resid[], int SegmentIslandNumber[], int islandSegmentCount[] ) {
		// Output variable definitions
		int constrainedSegmentCount_outArray[] = new int[1];
		float segmentPistonRms_outArray[] = new float[1];
		int numberOfIslands_outArray[] = new int[1];
		// Deal with Array Lengths
		int nb_step_len1 = nb_step.length;
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		int activeSegments_len1 = activeSegments.length;
		int plusPiston_len1 = plusPiston.length;
		int minusPiston_len1 = minusPiston.length;
		int actCalc_len1 = actCalc.length;
		int resid_len1 = resid.length;
		int SegmentIslandNumber_len1 = SegmentIslandNumber.length;
		int islandSegmentCount_len1 = islandSegmentCount.length;
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		nbActuators(retVal, nb_step,nb_step_len1,row_flag,row_flag_len1,col_flag,col_flag_len1,acsa_collapse,acsa_len1,acsa_len2,activeSegments,activeSegments_len1,plusPiston,plusPiston_len1,minusPiston,minusPiston_len1,constrainedSegmentCount_outArray,actCalc,actCalc_len1,resid,resid_len1,segmentPistonRms_outArray,numberOfIslands_outArray,SegmentIslandNumber,SegmentIslandNumber_len1,islandSegmentCount,islandSegmentCount_len1);
		// expand array to two dimensions
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa[i][j] = acsa_collapse[i*acsa_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = constrainedSegmentCount_outArray[0];
		out[1] = segmentPistonRms_outArray[0];
		out[2] = numberOfIslands_outArray[0];
		return out;
	}
}