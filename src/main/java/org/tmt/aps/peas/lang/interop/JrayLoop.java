package org.tmt.aps.peas.lang.interop; 
public class JrayLoop
{
	public native void rayLoop(RetVal retVal, float arcsec_per_m, float phi_x, float phi_y, float d1, float xlenslet[], int xlenslet_size_1, int xlenslet_size_2, float ylenslet[], int ylenslet_size_1, int ylenslet_size_2, float delta_x, float delta_y, float delta_z, float epsilon_x, float epsilon_y, float rcurv1, float f1, float bfd, float f_final, int ncount[], int c_flag[], float xinit[], int xinit_size_1, float yinit[], int yinit_size_1, float ximage[], int ximage_size_1, float yimage[], int yimage_size_1, float zimage[], int zimage_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jrayLoop(RetVal retVal, float arcsec_per_m, float phi_x, float phi_y, float d1, float xlenslet[][], float ylenslet[][], float delta_x, float delta_y, float delta_z, float epsilon_x, float epsilon_y, float rcurv1, float f1, float bfd, float f_final, float xinit[], float yinit[], float ximage[], float yimage[], float zimage[] ) {
		// Output variable definitions
		int ncount_outArray[] = new int[1];
		int c_flag_outArray[] = new int[1];
		// Deal with Array Lengths
		int xlenslet_len1 = xlenslet.length;
		int xlenslet_len2 = xlenslet[0].length;
		float[] xlenslet_collapse = new float[xlenslet_len1 * xlenslet_len2];
		int ylenslet_len1 = ylenslet.length;
		int ylenslet_len2 = ylenslet[0].length;
		float[] ylenslet_collapse = new float[ylenslet_len1 * ylenslet_len2];
		int xinit_len1 = xinit.length;
		int yinit_len1 = yinit.length;
		int ximage_len1 = ximage.length;
		int yimage_len1 = yimage.length;
		int zimage_len1 = zimage.length;
		// collapse array to one dimension
		for (int i=0; i<xlenslet_len1; i++) { 
			for (int j=0; j<xlenslet_len2; j++) { 
				xlenslet_collapse[i*xlenslet_len2 + j] = xlenslet[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<ylenslet_len1; i++) { 
			for (int j=0; j<ylenslet_len2; j++) { 
				ylenslet_collapse[i*ylenslet_len2 + j] = ylenslet[i][j]; 
			} 
		} 
		// Call native method
		rayLoop(retVal, arcsec_per_m,phi_x,phi_y,d1,xlenslet_collapse,xlenslet_len1,xlenslet_len2,ylenslet_collapse,ylenslet_len1,ylenslet_len2,delta_x,delta_y,delta_z,epsilon_x,epsilon_y,rcurv1,f1,bfd,f_final,ncount_outArray,c_flag_outArray,xinit,xinit_len1,yinit,yinit_len1,ximage,ximage_len1,yimage,yimage_len1,zimage,zimage_len1);
		// expand array to two dimensions
		for (int i=0; i<xlenslet_len1; i++) { 
			for (int j=0; j<xlenslet_len2; j++) { 
				xlenslet[i][j] = xlenslet_collapse[i*xlenslet_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<ylenslet_len1; i++) { 
			for (int j=0; j<ylenslet_len2; j++) { 
				ylenslet[i][j] = ylenslet_collapse[i*ylenslet_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = ncount_outArray[0];
		out[1] = c_flag_outArray[0];
		return out;
	}
}