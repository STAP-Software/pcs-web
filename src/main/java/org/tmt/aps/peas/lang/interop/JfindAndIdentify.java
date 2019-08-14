package org.tmt.aps.peas.lang.interop; 
public class JfindAndIdentify
{
	public native void findAndIdentify(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, int nsp, int ngp, float x_ref_def[], int x_ref_def_size_1, float y_ref_def[], int y_ref_def_size_1, float u_est, float u_delta_0, int matchbox, int nthresh0, int nPeakMinThresh, int nPeakMaxThresh, int forceScaleFlg, float forceScaleValue, int forceRotFlg, float forceRotValue, float matchFineThresh, int lensletOrientation, int spiralRingCount, int hexArrayFlag, int spot_flag[], int spot_flag_size_1, float xi_rst[], int xi_rst_size_1, float yi_rst[], int yi_rst_size_1, float x_peak[], int x_peak_size_1, float y_peak[], int y_peak_size_1, int n_detect[], int n_detect_size_1, float fi_param[], int fi_param_size_1, int n0123[], int n0123_size_1, int numFilledBoxes[], float fracFilledBoxes[], float fracFilledAnalysisBoxes[], int n_solution[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jfindAndIdentify(RetVal retVal, float frame[][], int nsp, int ngp, float x_ref_def[], float y_ref_def[], float u_est, float u_delta_0, int matchbox, int nthresh0, int nPeakMinThresh, int nPeakMaxThresh, int forceScaleFlg, float forceScaleValue, int forceRotFlg, float forceRotValue, float matchFineThresh, int lensletOrientation, int spiralRingCount, int hexArrayFlag, int spot_flag[], float xi_rst[], float yi_rst[], float x_peak[], float y_peak[], int n_detect[], float fi_param[], int n0123[] ) {
		// Output variable definitions
		int numFilledBoxes_outArray[] = new int[1];
		float fracFilledBoxes_outArray[] = new float[1];
		float fracFilledAnalysisBoxes_outArray[] = new float[1];
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
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// Call native method
		findAndIdentify(retVal, frame_collapse,frame_len1,frame_len2,nsp,ngp,x_ref_def,x_ref_def_len1,y_ref_def,y_ref_def_len1,u_est,u_delta_0,matchbox,nthresh0,nPeakMinThresh,nPeakMaxThresh,forceScaleFlg,forceScaleValue,forceRotFlg,forceRotValue,matchFineThresh,lensletOrientation,spiralRingCount,hexArrayFlag,spot_flag,spot_flag_len1,xi_rst,xi_rst_len1,yi_rst,yi_rst_len1,x_peak,x_peak_len1,y_peak,y_peak_len1,n_detect,n_detect_len1,fi_param,fi_param_len1,n0123,n0123_len1,numFilledBoxes_outArray,fracFilledBoxes_outArray,fracFilledAnalysisBoxes_outArray,n_solution_outArray);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = numFilledBoxes_outArray[0];
		out[1] = fracFilledBoxes_outArray[0];
		out[2] = fracFilledAnalysisBoxes_outArray[0];
		out[3] = n_solution_outArray[0];
		return out;
	}
}