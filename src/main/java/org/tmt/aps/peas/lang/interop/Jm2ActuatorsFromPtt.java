package org.tmt.aps.peas.lang.interop; 
public class Jm2ActuatorsFromPtt
{
	public native void m2ActuatorsFromPtt(RetVal retVal, float epsilon_x_tel, float epsilon_y_tel, float delta_z, float m2_act_rad_mm, float dsecondact[], int dsecondact_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jm2ActuatorsFromPtt(RetVal retVal, float epsilon_x_tel, float epsilon_y_tel, float delta_z, float m2_act_rad_mm, float dsecondact[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int dsecondact_len1 = dsecondact.length;
		// Call native method
		m2ActuatorsFromPtt(retVal, epsilon_x_tel,epsilon_y_tel,delta_z,m2_act_rad_mm,dsecondact,dsecondact_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}