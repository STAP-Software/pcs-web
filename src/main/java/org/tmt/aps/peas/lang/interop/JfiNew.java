package org.tmt.aps.peas.lang.interop; 
public class JfiNew
{
	public native void fiNew(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, int npattern, int nsp, int ngp, float frame_avg, float frame_sigma, float x_ref_def[], int x_ref_def_size_1, float y_ref_def[], int y_ref_def_size_1, float u_est, float u_delta_0, int matchbox, int nthresh0, int ncut, int nPeakMinThresh, int nPeakMaxThresh, int spot_flag[], int spot_flag_size_1, float xi_rst[], int xi_rst_size_1, float yi_rst[], int yi_rst_size_1, float x_peak[], int x_peak_size_1, float y_peak[], int y_peak_size_1, int n_detect[], int n_detect_size_1, float fi_param[], int fi_param_size_1, int n0123[], int n0123_size_1, int n_solution[], float ccd[], int ccd_size_1, int ccd_size_2, float ccd_boxes_sha[], int ccd_boxes_sha_size_1, int ccd_boxes_sha_size_2, float ccd_boxes_num[], int ccd_boxes_num_size_1, int ccd_boxes_num_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfiNew(RetVal retVal, float frame[][], int npattern, int nsp, int ngp, float frame_avg, float frame_sigma, float x_ref_def[], float y_ref_def[], float u_est, float u_delta_0, int matchbox, int nthresh0, int ncut, int nPeakMinThresh, int nPeakMaxThresh, int spot_flag[], float xi_rst[], float yi_rst[], float x_peak[], float y_peak[], int n_detect[], float fi_param[], int n0123[], float ccd[][], float ccd_boxes_sha[][], float ccd_boxes_num[][] ) {
		// Output variable definitions
		int n_solution_outArray[] = new int[1];
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		int x_ref_def_len1 = x_ref_def.length;
		int y_ref_def_len1 = y_ref_def.length;
		int spot_flag_len1 = spot_flag.length;
		int xi_rst_len1 = xi_rst.length;
		int yi_rst_len1 = yi_rst.length;
		int x_peak_len1 = x_peak.length;
		int y_peak_len1 = y_peak.length;
		int n_detect_len1 = n_detect.length;
		int fi_param_len1 = fi_param.length;
		int n0123_len1 = n0123.length;
		int ccd_len1 = ccd.length;
		int ccd_len2 = ccd[0].length;
		float[] ccd_collapse = new float[ccd_len1 * ccd_len2];
		int ccd_boxes_sha_len1 = ccd_boxes_sha.length;
		int ccd_boxes_sha_len2 = ccd_boxes_sha[0].length;
		float[] ccd_boxes_sha_collapse = new float[ccd_boxes_sha_len1 * ccd_boxes_sha_len2];
		int ccd_boxes_num_len1 = ccd_boxes_num.length;
		int ccd_boxes_num_len2 = ccd_boxes_num[0].length;
		float[] ccd_boxes_num_collapse = new float[ccd_boxes_num_len1 * ccd_boxes_num_len2];
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd_collapse[i*ccd_len2 + j] = ccd[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<ccd_boxes_sha_len1; i++) { 
			for (int j=0; j<ccd_boxes_sha_len2; j++) { 
				ccd_boxes_sha_collapse[i*ccd_boxes_sha_len2 + j] = ccd_boxes_sha[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<ccd_boxes_num_len1; i++) { 
			for (int j=0; j<ccd_boxes_num_len2; j++) { 
				ccd_boxes_num_collapse[i*ccd_boxes_num_len2 + j] = ccd_boxes_num[i][j]; 
			} 
		} 
		// Call native method
		fiNew(retVal, frame_collapse,frame_len1,frame_len2,npattern,nsp,ngp,frame_avg,frame_sigma,x_ref_def,x_ref_def_len1,y_ref_def,y_ref_def_len1,u_est,u_delta_0,matchbox,nthresh0,ncut,nPeakMinThresh,nPeakMaxThresh,spot_flag,spot_flag_len1,xi_rst,xi_rst_len1,yi_rst,yi_rst_len1,x_peak,x_peak_len1,y_peak,y_peak_len1,n_detect,n_detect_len1,fi_param,fi_param_len1,n0123,n0123_len1,n_solution_outArray,ccd_collapse,ccd_len1,ccd_len2,ccd_boxes_sha_collapse,ccd_boxes_sha_len1,ccd_boxes_sha_len2,ccd_boxes_num_collapse,ccd_boxes_num_len1,ccd_boxes_num_len2);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<ccd_len1; i++) { 
			for (int j=0; j<ccd_len2; j++) { 
				ccd[i][j] = ccd_collapse[i*ccd_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<ccd_boxes_sha_len1; i++) { 
			for (int j=0; j<ccd_boxes_sha_len2; j++) { 
				ccd_boxes_sha[i][j] = ccd_boxes_sha_collapse[i*ccd_boxes_sha_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<ccd_boxes_num_len1; i++) { 
			for (int j=0; j<ccd_boxes_num_len2; j++) { 
				ccd_boxes_num[i][j] = ccd_boxes_num_collapse[i*ccd_boxes_num_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = n_solution_outArray[0];
		return out;
	}
}