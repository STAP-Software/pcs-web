package org.tmt.aps.peas.lang.interop; 
public class JfindCentroids
{
	public native void findCentroids(RetVal retVal, float ccd[], int ccd_size_1, int ccd_size_2, int irad, int imargin, int i_inits[], int i_inits_size_1, int j_inits[], int j_inits_size_1, int itermax, int nspot_types[], int nspot_types_size_1, int ngauss, float x_cents[], int x_cents_size_1, float y_cents[], int y_cents_size_1, float subimage_intensities[], int subimage_intensities_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfindCentroids(RetVal retVal, float ccd[][], int irad, int imargin, int i_inits[], int j_inits[], int itermax, int nspot_types[], int ngauss, float x_cents[], float y_cents[], float subimage_intensities[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int ccd_len1 = ccd.length;
		int ccd_len2 = ccd[0].length;
		float[] ccd_collapse = new float[ccd_len1 * ccd_len2];
		int i_inits_len1 = i_inits.length;
		int j_inits_len1 = j_inits.length;
		int nspot_types_len1 = nspot_types.length;
		int x_cents_len1 = x_cents.length;
		int y_cents_len1 = y_cents.length;
		int subimage_intensities_len1 = subimage_intensities.length;
		// collapse array to one dimension
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd_collapse[i*ccd_len2 + j] = ccd[i][j]; 
			} 
		} 
		// Call native method
		findCentroids(retVal, ccd_collapse,ccd_len1,ccd_len2,irad,imargin,i_inits,i_inits_len1,j_inits,j_inits_len1,itermax,nspot_types,nspot_types_len1,ngauss,x_cents,x_cents_len1,y_cents,y_cents_len1,subimage_intensities,subimage_intensities_len1);
		// expand array to two dimensions
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd[i][j] = ccd_collapse[i*ccd_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}