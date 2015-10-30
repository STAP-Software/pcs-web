package org.tmt.aps.peas.lang.interop; 
public class JbbAnalyzeFrame
{
	public native void bbAnalyzeFrame(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, float x_peak[], int x_peak_size_1, float y_peak[], int y_peak_size_1, float xi_rst[], int xi_rst_size_1, float yi_rst[], int yi_rst_size_1, int n_detect[], int n_detect_size_1, int irad_cent, int imargin, int ngauss, int nspot_type, int itermax, int edge_angle[], int edge_angle_size_1, int nsegments, int edgesForPhasingCalculation[], int edgesForPhasingCalculation_size_1, float template_c[], int template_c_size_1, int template_c_size_2, int template_c_size_3, int template_c_size_4, float coherence_out[], int coherence_out_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbbAnalyzeFrame(RetVal retVal, float frame[][], float x_peak[], float y_peak[], float xi_rst[], float yi_rst[], int n_detect[], int irad_cent, int imargin, int ngauss, int nspot_type, int itermax, int edge_angle[], int nsegments, int edgesForPhasingCalculation[], float template_c[][][][], float coherence_out[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		int x_peak_len1 = x_peak.length;
		int y_peak_len1 = y_peak.length;
		int xi_rst_len1 = xi_rst.length;
		int yi_rst_len1 = yi_rst.length;
		int n_detect_len1 = n_detect.length;
		int edge_angle_len1 = edge_angle.length;
		int edgesForPhasingCalculation_len1 = edgesForPhasingCalculation.length;
		int template_c_len1 = template_c.length;
		int template_c_len2 = template_c[0].length;
		int template_c_len3 = template_c[0][0].length;
		int template_c_len4 = template_c[0][0][0].length;
		float[] template_c_collapse = new float[template_c_len1 * template_c_len2 * template_c_len3 * template_c_len4];
		int coherence_out_len1 = coherence_out.length;
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
		// Call native method
		bbAnalyzeFrame(retVal, frame_collapse,frame_len1,frame_len2,x_peak,x_peak_len1,y_peak,y_peak_len1,xi_rst,xi_rst_len1,yi_rst,yi_rst_len1,n_detect,n_detect_len1,irad_cent,imargin,ngauss,nspot_type,itermax,edge_angle,edge_angle_len1,nsegments,edgesForPhasingCalculation,edgesForPhasingCalculation_len1,template_c_collapse,template_c_len1,template_c_len2,template_c_len3,template_c_len4,coherence_out,coherence_out_len1);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}