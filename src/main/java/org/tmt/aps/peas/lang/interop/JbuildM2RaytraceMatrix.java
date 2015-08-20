package org.tmt.aps.peas.lang.interop; 
public class JbuildM2RaytraceMatrix
{
	public native void buildM2RaytraceMatrix(RetVal retVal, float x_lens_2[], int x_lens_2_size_1, int x_lens_2_size_2, float y_lens_2[], int y_lens_2_size_1, int y_lens_2_size_2, float unit_piston, float unit_tt, float phi_x, float phi_y, float f_final, float rcurv1, float f1, float bfd, float acsa[], int acsa_size_1, int acsa_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbuildM2RaytraceMatrix(RetVal retVal, float x_lens_2[][], float y_lens_2[][], float unit_piston, float unit_tt, float phi_x, float phi_y, float f_final, float rcurv1, float f1, float bfd, float acsa[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int x_lens_2_len1 = x_lens_2.length;
		int x_lens_2_len2 = x_lens_2[0].length;
		float[] x_lens_2_collapse = new float[x_lens_2_len1 * x_lens_2_len2];
		int y_lens_2_len1 = y_lens_2.length;
		int y_lens_2_len2 = y_lens_2[0].length;
		float[] y_lens_2_collapse = new float[y_lens_2_len1 * y_lens_2_len2];
		int acsa_len1 = acsa.length;
		int acsa_len2 = acsa[0].length;
		float[] acsa_collapse = new float[acsa_len1 * acsa_len2];
		// collapse array to one dimension
		for (int i=0; i<x_lens_2_len1; i++) { 
			for (int j=0; j<x_lens_2_len2; j++) { 
				x_lens_2_collapse[i*x_lens_2_len2 + j] = x_lens_2[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<y_lens_2_len1; i++) { 
			for (int j=0; j<y_lens_2_len2; j++) { 
				y_lens_2_collapse[i*y_lens_2_len2 + j] = y_lens_2[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa_collapse[i*acsa_len2 + j] = acsa[i][j]; 
			} 
		} 
		// Call native method
		buildM2RaytraceMatrix(retVal, x_lens_2_collapse,x_lens_2_len1,x_lens_2_len2,y_lens_2_collapse,y_lens_2_len1,y_lens_2_len2,unit_piston,unit_tt,phi_x,phi_y,f_final,rcurv1,f1,bfd,acsa_collapse,acsa_len1,acsa_len2);
		// expand array to two dimensions
		for (int i=0; i<x_lens_2_len1; i++) { 
			for (int j=0; j<x_lens_2_len2; j++) { 
				x_lens_2[i][j] = x_lens_2_collapse[i*x_lens_2_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<y_lens_2_len1; i++) { 
			for (int j=0; j<y_lens_2_len2; j++) { 
				y_lens_2[i][j] = y_lens_2_collapse[i*y_lens_2_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<acsa_len1; i++) { 
			for (int j=0; j<acsa_len2; j++) { 
				acsa[i][j] = acsa_collapse[i*acsa_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}