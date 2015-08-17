package org.tmt.aps.peas.lang.interop; 
public class Jm2ActuatorsFromPtt
{
	public native void m2ActuatorsFromPtt(RetVal retVal, float epsilon_x, float epsilon_y, float delta_z, float m2_act_rad_mm, float m2_tt_correction_factor, float dsecondact[], int dsecondact_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jm2ActuatorsFromPtt(RetVal retVal, float epsilon_x, float epsilon_y, float delta_z, float m2_act_rad_mm, float m2_tt_correction_factor, float dsecondact[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int dsecondact_len1 = dsecondact.length;
		// Call native method
		m2ActuatorsFromPtt(retVal, epsilon_x,epsilon_y,delta_z,m2_act_rad_mm,m2_tt_correction_factor,dsecondact,dsecondact_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}