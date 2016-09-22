package org.tmt.aps.peas.lang.interop; 
public class JcorrInterp
{
	public native void corrInterp(RetVal retVal, float x[], int x_size_1, int x_size_2, float z[], int z_size_1, int z_size_2, int z_size_3, float best_index[], float cmax[], float cmin[], float a_fit[], float b_fit[], float phi_fit[], float chisq_f[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcorrInterp(RetVal retVal, float x[][], float z[][][] ) {
		// Output variable definitions
		float best_index_outArray[] = new float[1];
		float cmax_outArray[] = new float[1];
		float cmin_outArray[] = new float[1];
		float a_fit_outArray[] = new float[1];
		float b_fit_outArray[] = new float[1];
		float phi_fit_outArray[] = new float[1];
		float chisq_f_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_len1 = x.length;
		int x_len2 = x[0].length;
		float[] x_collapse = new float[x_len1 * x_len2];
		int z_len1 = z.length;
		int z_len2 = z[0].length;
		int z_len3 = z[0][0].length;
		float[] z_collapse = new float[z_len1 * z_len2 * z_len3];
		// collapse array to one dimension
		for (int i=0; i<x_len1; i++) { 
			for (int j=0; j<x_len2; j++) { 
				x_collapse[i*x_len2 + j] = x[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<z_len1; i++) { 
			for (int j=0; j<z_len2; j++) { 
			for (int k=0; k<z_len3; k++) { 
				z_collapse[i*z_len2 * z_len3 + j * z_len3 + k] = z[i][j][k]; 
			} 
			} 
		} 
		// Call native method
		corrInterp(retVal, x_collapse,x_len1,x_len2,z_collapse,z_len1,z_len2,z_len3,best_index_outArray,cmax_outArray,cmin_outArray,a_fit_outArray,b_fit_outArray,phi_fit_outArray,chisq_f_outArray);
		// expand array to two dimensions
		for (int i=0; i<x_len1; i++) { 
			for (int j=0; j<x_len2; j++) { 
				x[i][j] = x_collapse[i*x_len2 + j];
			} 
		} 
		// expand array to three dimensions
		for (int i=0; i<z_len1; i++) { 
			for (int j=0; j<z_len2; j++) { 
			for (int k=0; k<z_len3; k++) { 
				z[i][j][k] = z_collapse[i*z_len2 * z_len3 +j * z_len3 +   k];
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[7];
		out[0] = best_index_outArray[0];
		out[1] = cmax_outArray[0];
		out[2] = cmin_outArray[0];
		out[3] = a_fit_outArray[0];
		out[4] = b_fit_outArray[0];
		out[5] = phi_fit_outArray[0];
		out[6] = chisq_f_outArray[0];
		return out;
	}
}