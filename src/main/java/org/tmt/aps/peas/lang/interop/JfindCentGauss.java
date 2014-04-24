package org.tmt.aps.peas.lang.interop; 
public class JfindCentGauss
{
	public native void findCentGauss(RetVal retVal, float ccd[], int ccd_size_1, int ccd_size_2, int irad, int imargin, int i_init, int j_init, int itermax, int nspot_type, int ngauss, float x_cent[], float y_cent[], int centroid[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfindCentGauss(RetVal retVal, float ccd[][], int irad, int imargin, int i_init, int j_init, int itermax, int nspot_type, int ngauss ) {
		// Output variable definitions
		float x_cent_outArray[] = new float[1];
		float y_cent_outArray[] = new float[1];
		int centroid_outArray[] = new int[1];
		// Deal with Array Lengths
		int ccd_len1 = ccd.length;
		int ccd_len2 = ccd[0].length;
		float[] ccd_collapse = new float[ccd_len1 * ccd_len2];
		// collapse array to one dimension
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd_collapse[i*ccd_len2 + j] = ccd[i][j]; 
			} 
		} 
		// Call native method
		findCentGauss(retVal, ccd_collapse,ccd_len1,ccd_len2,irad,imargin,i_init,j_init,itermax,nspot_type,ngauss,x_cent_outArray,y_cent_outArray,centroid_outArray);
		// expand array to two dimensions
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd[i][j] = ccd_collapse[i*ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = x_cent_outArray[0];
		out[1] = y_cent_outArray[0];
		out[2] = centroid_outArray[0];
		return out;
	}
}