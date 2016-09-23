package org.tmt.aps.peas.lang.interop; 
public class JnbActuators
{
	public native void nbActuators(RetVal retVal, float nb_step[], int nb_step_size_1, int row_flag[], int row_flag_size_1, int col_flag[], int col_flag_size_1, float acsa[], int acsa_size_1, int acsa_size_2, int constrainedSegmentCount[], int num_good_edges[], float edge_res_max[], float edge_res_rms[], float actCalc[], int actCalc_size_1, float resid[], int resid_size_1, float segmentPistonRms[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbActuators(RetVal retVal, float nb_step[], int row_flag[], int col_flag[], float acsa[][], float actCalc[], float resid[] ) {
		// Output variable definitions
		int constrainedSegmentCount_outArray[] = new int[1];
		int num_good_edges_outArray[] = new int[1];
		float edge_res_max_outArray[] = new float[1];
		float edge_res_rms_outArray[] = new float[1];
		float segmentPistonRms_outArray[] = new float[1];
		// Deal with Array Lengths
		int nb_step_len1 = nb_step.length;
		int row_flag_len1 = row_flag.length;
		int col_flag_len1 = col_flag.length;
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		int actCalc_len1 = actCalc.length;
		int resid_len1 = resid.length;
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		nbActuators(retVal, nb_step,nb_step_len1,row_flag,row_flag_len1,col_flag,col_flag_len1,acsa_collapse,acsa_len1,acsa_len2,constrainedSegmentCount_outArray,num_good_edges_outArray,edge_res_max_outArray,edge_res_rms_outArray,actCalc,actCalc_len1,resid,resid_len1,segmentPistonRms_outArray);
		// expand array to two dimensions
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa[i][j] = acsa_collapse[i*acsa_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[5];
		out[0] = constrainedSegmentCount_outArray[0];
		out[1] = num_good_edges_outArray[0];
		out[2] = edge_res_max_outArray[0];
		out[3] = edge_res_rms_outArray[0];
		out[4] = segmentPistonRms_outArray[0];
		return out;
	}
}