package org.tmt.aps.peas.lang.interop; 
public class JfindAndIdentifyDetectionDiagnostic
{
	public native void findAndIdentifyDetectionDiagnostic(RetVal retVal, int matchbox, float xi_rst[], int xi_rst_size_1, float yi_rst[], int yi_rst_size_1, int n_detect[], int n_detect_size_1, int box_flag[], int box_flag_size_1, float ccdDetectionBoxes[], int ccdDetectionBoxes_size_1, int ccdDetectionBoxes_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfindAndIdentifyDetectionDiagnostic(RetVal retVal, int matchbox, float xi_rst[], float yi_rst[], int n_detect[], int box_flag[], float ccdDetectionBoxes[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xi_rst_len1 = xi_rst.length;
		int yi_rst_len1 = yi_rst.length;
		int n_detect_len1 = n_detect.length;
		int box_flag_len1 = box_flag.length;
		int ccdDetectionBoxes_len1 = ccdDetectionBoxes.length;
		int ccdDetectionBoxes_len2 = ccdDetectionBoxes[0].length;
		float[] ccdDetectionBoxes_collapse = new float[ccdDetectionBoxes_len1 * ccdDetectionBoxes_len2];
		// collapse array to one dimension
		for (int i=0; i<ccdDetectionBoxes_len1; i++) { 
			for (int j=0; j<ccdDetectionBoxes_len2; j++) { 
				ccdDetectionBoxes_collapse[i*ccdDetectionBoxes_len2 + j] = ccdDetectionBoxes[i][j]; 
			} 
		} 
		// Call native method
		findAndIdentifyDetectionDiagnostic(retVal, matchbox,xi_rst,xi_rst_len1,yi_rst,yi_rst_len1,n_detect,n_detect_len1,box_flag,box_flag_len1,ccdDetectionBoxes_collapse,ccdDetectionBoxes_len1,ccdDetectionBoxes_len2);
		// expand array to two dimensions
		for (int i=0; i<ccdDetectionBoxes_len1; i++) { 
			for (int j=0; j<ccdDetectionBoxes_len2; j++) { 
				ccdDetectionBoxes[i][j] = ccdDetectionBoxes_collapse[i*ccdDetectionBoxes_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}