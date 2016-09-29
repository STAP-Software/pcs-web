package org.tmt.aps.peas.lang.interop; 
public class JnbAnalyzeStepSequence
{
	public native void nbAnalyzeStepSequence(RetVal retVal, float nb_table[], int nb_table_size_1, int nb_table_size_2, float corr_table[], int corr_table_size_1, int corr_table_size_2, float xlambda0, int row_flag_in[], int row_flag_in_size_1, int edge_color[], int edge_color_size_1, int ntemplate, float coherenceThreshold, int row_flag_out[], int row_flag_out_size_1, float steps_out[], int steps_out_size_1, float index_table_out[], int index_table_out_size_1, int index_table_out_size_2, int index_table_out_size_3, float edge_error_steps[], float edge_error_microns[], float line_slope_avg[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbAnalyzeStepSequence(RetVal retVal, float nb_table[][], float corr_table[][], float xlambda0, int row_flag_in[], int edge_color[], int ntemplate, float coherenceThreshold, int row_flag_out[], float steps_out[], float index_table_out[][][] ) {
		// Output variable definitions
		float edge_error_steps_outArray[] = new float[1];
		float edge_error_microns_outArray[] = new float[1];
		float line_slope_avg_outArray[] = new float[1];
		// Deal with Array Lengths
		int nb_table_len1 = nb_table.length;
		int nb_table_len2 = nb_table[0].length;
		float[] nb_table_collapse = new float[nb_table_len1 * nb_table_len2];
		int corr_table_len1 = corr_table.length;
		int corr_table_len2 = corr_table[0].length;
		float[] corr_table_collapse = new float[corr_table_len1 * corr_table_len2];
		int row_flag_in_len1 = row_flag_in.length;
		int edge_color_len1 = edge_color.length;
		int row_flag_out_len1 = row_flag_out.length;
		int steps_out_len1 = steps_out.length;
		int index_table_out_len1 = index_table_out.length;
		int index_table_out_len2 = index_table_out[0].length;
		int index_table_out_len3 = index_table_out[0][0].length;
		float[] index_table_out_collapse = new float[index_table_out_len1 * index_table_out_len2 * index_table_out_len3];
		// collapse array to one dimension
		for (int i=0; i<nb_table_len1; i++) { 
			for (int j=0; j<nb_table_len2; j++) { 
				nb_table_collapse[i*nb_table_len2 + j] = nb_table[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<corr_table_len1; i++) { 
			for (int j=0; j<corr_table_len2; j++) { 
				corr_table_collapse[i*corr_table_len2 + j] = corr_table[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<index_table_out_len1; i++) { 
			for (int j=0; j<index_table_out_len2; j++) { 
			for (int k=0; k<index_table_out_len3; k++) { 
				index_table_out_collapse[i*index_table_out_len2 * index_table_out_len3 + j * index_table_out_len3 + k] = index_table_out[i][j][k]; 
			} 
			} 
		} 
		// Call native method
		nbAnalyzeStepSequence(retVal, nb_table_collapse,nb_table_len1,nb_table_len2,corr_table_collapse,corr_table_len1,corr_table_len2,xlambda0,row_flag_in,row_flag_in_len1,edge_color,edge_color_len1,ntemplate,coherenceThreshold,row_flag_out,row_flag_out_len1,steps_out,steps_out_len1,index_table_out_collapse,index_table_out_len1,index_table_out_len2,index_table_out_len3,edge_error_steps_outArray,edge_error_microns_outArray,line_slope_avg_outArray);
		// expand array to two dimensions
		for (int i=0; i<nb_table_len1; i++) { 
			for (int j=0; j<nb_table_len2; j++) { 
				nb_table[i][j] = nb_table_collapse[i*nb_table_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<corr_table_len1; i++) { 
			for (int j=0; j<corr_table_len2; j++) { 
				corr_table[i][j] = corr_table_collapse[i*corr_table_len2 + j];
			} 
		} 
		// expand array to three dimensions
		for (int i=0; i<index_table_out_len1; i++) { 
			for (int j=0; j<index_table_out_len2; j++) { 
			for (int k=0; k<index_table_out_len3; k++) { 
				index_table_out[i][j][k] = index_table_out_collapse[i*index_table_out_len2 * index_table_out_len3 +j * index_table_out_len3 +   k];
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = edge_error_steps_outArray[0];
		out[1] = edge_error_microns_outArray[0];
		out[2] = line_slope_avg_outArray[0];
		return out;
	}
}