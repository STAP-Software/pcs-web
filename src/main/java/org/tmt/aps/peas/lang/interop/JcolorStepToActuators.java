package org.tmt.aps.peas.lang.interop; 
public class JcolorStepToActuators
{
	public native void colorStepToActuators(RetVal retVal, float dPColor[], int dPColor_size_1, int segmentColor[], int segmentColor_size_1, float m1Commands[], int m1Commands_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcolorStepToActuators(RetVal retVal, float dPColor[], int segmentColor[], float m1Commands[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int dPColor_len1 = dPColor.length;
		int segmentColor_len1 = segmentColor.length;
		int m1Commands_len1 = m1Commands.length;
		// Call native method
		colorStepToActuators(retVal, dPColor,dPColor_len1,segmentColor,segmentColor_len1,m1Commands,m1Commands_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}