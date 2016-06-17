package org.tmt.aps.peas.lang.interop; 
public class JnbFourierLineFit
{
	public native void nbFourierLineFit(RetVal retVal, float z[], int z_size_1, float phi_0, float a0[], float as[], float ac[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jnbFourierLineFit(RetVal retVal, float z[], float phi_0 ) {
		// Output variable definitions
		float a0_outArray[] = new float[1];
		float as_outArray[] = new float[1];
		float ac_outArray[] = new float[1];
		// Deal with Array Lengths
		int z_len1 = z.length;
		// Call native method
		nbFourierLineFit(retVal, z,z_len1,phi_0,a0_outArray,as_outArray,ac_outArray);
		// Assign output variables
		Object[] out = new Object[3];
		out[0] = a0_outArray[0];
		out[1] = as_outArray[0];
		out[2] = ac_outArray[0];
		return out;
	}
}