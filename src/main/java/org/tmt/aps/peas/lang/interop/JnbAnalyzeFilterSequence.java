package org.tmt.aps.peas.lang.interop; 
public class JnbAnalyzeFilterSequence
{
	public native void nbAnalyzeFilterSequence(RetVal retVal, int rowFlagIn[], int rowFlagIn_size_1, int rowFlagIn_size_2, float step_table[], int step_table_size_1, int step_table_size_2, float corr_table[], int corr_table_size_1, int corr_table_size_2, int corr_table_size_3, float xlambda[], int xlambda_size_1, float range, float r_int, float chi2_nm[], int chi2_nm_size_1, float nb_step[], int nb_step_size_1, int rowFlagOut[], int rowFlagOut_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbAnalyzeFilterSequence(RetVal retVal, int rowFlagIn[][], float step_table[][], float corr_table[][][], float xlambda[], float range, float r_int, float chi2_nm[], float nb_step[], int rowFlagOut[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int rowFlagIn_len1 = rowFlagIn.length;
		int rowFlagIn_len2 = rowFlagIn[0].length;
		int[] rowFlagIn_collapse = new int[rowFlagIn_len1 * rowFlagIn_len2];
		int step_table_len1 = step_table.length;
		int step_table_len2 = step_table[0].length;
		float[] step_table_collapse = new float[step_table_len1 * step_table_len2];
		int corr_table_len1 = corr_table.length;
		int corr_table_len2 = corr_table[0].length;
		int corr_table_len3 = corr_table[0][0].length;
		float[] corr_table_collapse = new float[corr_table_len1 * corr_table_len2 * corr_table_len3];
		int xlambda_len1 = xlambda.length;
		int chi2_nm_len1 = chi2_nm.length;
		int nb_step_len1 = nb_step.length;
		int rowFlagOut_len1 = rowFlagOut.length;
		// collapse array to one dimension
		for (int i=0; i<rowFlagIn_len1; i++) { 
			for (int j=0; j<rowFlagIn_len2; j++) { 
				rowFlagIn_collapse[i*rowFlagIn_len2 + j] = rowFlagIn[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<step_table_len1; i++) { 
			for (int j=0; j<step_table_len2; j++) { 
				step_table_collapse[i*step_table_len2 + j] = step_table[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<corr_table_len1; i++) { 
			for (int j=0; j<corr_table_len2; j++) { 
			for (int k=0; k<corr_table_len3; k++) { 
				corr_table_collapse[i*corr_table_len2 * corr_table_len3 + j * corr_table_len3 + k] = corr_table[i][j][k]; 
			} 
			} 
		} 
		// Call native method
		nbAnalyzeFilterSequence(retVal, rowFlagIn_collapse,rowFlagIn_len1,rowFlagIn_len2,step_table_collapse,step_table_len1,step_table_len2,corr_table_collapse,corr_table_len1,corr_table_len2,corr_table_len3,xlambda,xlambda_len1,range,r_int,chi2_nm,chi2_nm_len1,nb_step,nb_step_len1,rowFlagOut,rowFlagOut_len1);
		// expand array to two dimensions
		for (int i=0; i<rowFlagIn_len1; i++) { 
			for (int j=0; j<rowFlagIn_len2; j++) { 
				rowFlagIn[i][j] = rowFlagIn_collapse[i*rowFlagIn_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<step_table_len1; i++) { 
			for (int j=0; j<step_table_len2; j++) { 
				step_table[i][j] = step_table_collapse[i*step_table_len2 + j];
			} 
		} 
		// expand array to three dimensions
		for (int i=0; i<corr_table_len1; i++) { 
			for (int j=0; j<corr_table_len2; j++) { 
			for (int k=0; k<corr_table_len3; k++) { 
				corr_table[i][j][k] = corr_table_collapse[i*corr_table_len2 * corr_table_len3 +j * corr_table_len3 +   k];
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}