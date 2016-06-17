package org.tmt.aps.peas.lang.interop; 
public class JlineFit
{
	public native void lineFit(RetVal retVal, float x[], int x_size_1, float y[], int y_size_1, float xlim, float ylim, int m[], float a[], float b[], float rms[], float rms0[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jlineFit(RetVal retVal, float x[], float y[], float xlim, float ylim ) {
		// Output variable definitions
		int m_outArray[] = new int[1];
		float a_outArray[] = new float[1];
		float b_outArray[] = new float[1];
		float rms_outArray[] = new float[1];
		float rms0_outArray[] = new float[1];
		// Deal with Array Lengths
		int x_len1 = x.length;
		int y_len1 = y.length;
		// Call native method
		lineFit(retVal, x,x_len1,y,y_len1,xlim,ylim,m_outArray,a_outArray,b_outArray,rms_outArray,rms0_outArray);
		// Assign output variables
		Object[] out = new Object[5];
		out[0] = m_outArray[0];
		out[1] = a_outArray[0];
		out[2] = b_outArray[0];
		out[3] = rms_outArray[0];
		out[4] = rms0_outArray[0];
		return out;
	}
}