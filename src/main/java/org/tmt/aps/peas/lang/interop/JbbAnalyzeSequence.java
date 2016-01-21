package org.tmt.aps.peas.lang.interop; 
public class JbbAnalyzeSequence
{
	public native void bbAnalyzeSequence(RetVal retVal, float coherence_table[], int coherence_table_size_1, int coherence_table_size_2, float sigma_microns, float step_size, float g_interval, float acsa[], int acsa_size_1, int acsa_size_2, int edge_angle[], int edge_angle_size_1, int edge_color[], int edge_color_size_1, int row_flag_in[], int row_flag_in_size_1, float ring_mode[], int ring_mode_size_1, float keckPhRingModeCorrectionFactor, float step_corr[], int step_corr_size_1, float act_calc[], int act_calc_size_1, float resid[], int resid_size_1, int row_flag_out[], int row_flag_out_size_1, int constrainedSegmentCount[], float segmentPistonRms[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbbAnalyzeSequence(RetVal retVal, float coherence_table[][], float sigma_microns, float step_size, float g_interval, float acsa[][], int edge_angle[], int edge_color[], int row_flag_in[], float ring_mode[], float keckPhRingModeCorrectionFactor, float step_corr[], float act_calc[], float resid[], int row_flag_out[] ) {
		// Output variable definitions
		int constrainedSegmentCount_outArray[] = new int[1];
		float segmentPistonRms_outArray[] = new float[1];
		// Deal with Array Lengths
		int coherence_table_len1 = coherence_table.length;
		int coherence_table_len2 = coherence_table[0].length;
		float[] coherence_table_collapse = new float[coherence_table_len1 * coherence_table_len2];
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		int edge_angle_len1 = edge_angle.length;
		int edge_color_len1 = edge_color.length;
		int row_flag_in_len1 = row_flag_in.length;
		int ring_mode_len1 = ring_mode.length;
		int step_corr_len1 = step_corr.length;
		int act_calc_len1 = act_calc.length;
		int resid_len1 = resid.length;
		int row_flag_out_len1 = row_flag_out.length;
		// collapse array to one dimension
		for (int i=0; i<coherence_table_len1; i++) { 
			for (int j=0; j<coherence_table_len2; j++) { 
				coherence_table_collapse[i*coherence_table_len2 + j] = coherence_table[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		bbAnalyzeSequence(retVal, coherence_table_collapse,coherence_table_len1,coherence_table_len2,sigma_microns,step_size,g_interval,acsa_collapse,acsa_len1,acsa_len2,edge_angle,edge_angle_len1,edge_color,edge_color_len1,row_flag_in,row_flag_in_len1,ring_mode,ring_mode_len1,keckPhRingModeCorrectionFactor,step_corr,step_corr_len1,act_calc,act_calc_len1,resid,resid_len1,row_flag_out,row_flag_out_len1,constrainedSegmentCount_outArray,segmentPistonRms_outArray);
		// expand array to two dimensions
		for (int i=0; i<coherence_table_len1; i++) { 
			for (int j=0; j<coherence_table_len2; j++) { 
				coherence_table[i][j] = coherence_table_collapse[i*coherence_table_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa[i][j] = acsa_collapse[i*acsa_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = constrainedSegmentCount_outArray[0];
		out[1] = segmentPistonRms_outArray[0];
		return out;
	}
}