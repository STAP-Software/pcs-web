package org.tmt.aps.peas.lang.interop; 
public class JcalculatePupilRegError
{
	public native void calculatePupilRegError(RetVal retVal, int fractionalIntensityCalcMethod, float subimage_intensity[], int subimage_intensity_size_1, int good_spots[], int good_spots_size_1, int spotType[], int spotType_size_1, float theta_peri[], int theta_peri_size_1, float r_perp[], int r_perp_size_1, float r_parallel[], int r_parallel_size_1, float aHexM, float d_peripheral, float x_meas[], float y_meas[], float phi_meas[], float x_approx[], float y_approx[], float phi_approx[], float scale_error[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcalculatePupilRegError(RetVal retVal, int fractionalIntensityCalcMethod, float subimage_intensity[], int good_spots[], int spotType[], float theta_peri[], float r_perp[], float r_parallel[], float aHexM, float d_peripheral ) {
		// Output variable definitions
		float x_meas_outArray[] = new float[1];
		float y_meas_outArray[] = new float[1];
		float phi_meas_outArray[] = new float[1];
		float x_approx_outArray[] = new float[1];
		float y_approx_outArray[] = new float[1];
		float phi_approx_outArray[] = new float[1];
		float scale_error_outArray[] = new float[1];
		// Deal with Array Lengths
		int subimage_intensity_len1 = subimage_intensity.length;
		int good_spots_len1 = good_spots.length;
		int spotType_len1 = spotType.length;
		int theta_peri_len1 = theta_peri.length;
		int r_perp_len1 = r_perp.length;
		int r_parallel_len1 = r_parallel.length;
		// Call native method
		calculatePupilRegError(retVal, fractionalIntensityCalcMethod,subimage_intensity,subimage_intensity_len1,good_spots,good_spots_len1,spotType,spotType_len1,theta_peri,theta_peri_len1,r_perp,r_perp_len1,r_parallel,r_parallel_len1,aHexM,d_peripheral,x_meas_outArray,y_meas_outArray,phi_meas_outArray,x_approx_outArray,y_approx_outArray,phi_approx_outArray,scale_error_outArray);
		// Assign output variables
		Object[] out = new Object[7];
		out[0] = x_meas_outArray[0];
		out[1] = y_meas_outArray[0];
		out[2] = phi_meas_outArray[0];
		out[3] = x_approx_outArray[0];
		out[4] = y_approx_outArray[0];
		out[5] = phi_approx_outArray[0];
		out[6] = scale_error_outArray[0];
		return out;
	}
}