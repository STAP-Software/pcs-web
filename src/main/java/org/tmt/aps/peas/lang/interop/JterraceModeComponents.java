package org.tmt.aps.peas.lang.interop; 
public class JterraceModeComponents
{
	public native void terraceModeComponents(RetVal retVal, float xact[], int xact_size_1, float yact[], int yact_size_1, float p_in[], int p_in_size_1, int activeSegments[], int activeSegments_size_1, float x_terrace_p[], float y_terrace_p[], float x_terrace_a[], float y_terrace_a[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jterraceModeComponents(RetVal retVal, float xact[], float yact[], float p_in[], int activeSegments[] ) {
		// Output variable definitions
		float x_terrace_p_outArray[] = new float[1];
		float y_terrace_p_outArray[] = new float[1];
		float x_terrace_a_outArray[] = new float[1];
		float y_terrace_a_outArray[] = new float[1];
		// Deal with Array Lengths
		int xact_len1 = xact.length;
		int yact_len1 = yact.length;
		int p_in_len1 = p_in.length;
		int activeSegments_len1 = activeSegments.length;
		// Call native method
		terraceModeComponents(retVal, xact,xact_len1,yact,yact_len1,p_in,p_in_len1,activeSegments,activeSegments_len1,x_terrace_p_outArray,y_terrace_p_outArray,x_terrace_a_outArray,y_terrace_a_outArray);
		// Assign output variables
		Object[] out = new Object[4];
		out[0] = x_terrace_p_outArray[0];
		out[1] = y_terrace_p_outArray[0];
		out[2] = x_terrace_a_outArray[0];
		out[3] = y_terrace_a_outArray[0];
		return out;
	}
}