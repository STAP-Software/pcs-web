package org.tmt.aps.peas.lang.interop; 
public class JmeasuredValues
{
	public native void measuredValues(RetVal retVal, float u_theoritical, float v_theoritical, float u_actual, float v_actual, float scale_meas[], float phi_meas[], float phi_deg_meas[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmeasuredValues(RetVal retVal, float u_theoritical, float v_theoritical, float u_actual, float v_actual ) {
		// Output variable definitions
		float scale_meas_outArray[] = new float[1];
		float phi_meas_outArray[] = new float[1];
		float phi_deg_meas_outArray[] = new float[1];
		// Deal with Array Lengths
		// Call native method
		measuredValues(retVal, u_theoritical,v_theoritical,u_actual,v_actual,scale_meas_outArray,phi_meas_outArray,phi_deg_meas_outArray);
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = scale_meas_outArray[0];
		out[1] = phi_meas_outArray[0];
		out[2] = phi_deg_meas_outArray[0];
		return out;
	}
}