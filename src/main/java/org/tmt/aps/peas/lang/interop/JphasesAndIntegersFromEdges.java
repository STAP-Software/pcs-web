package org.tmt.aps.peas.lang.interop; 
public class JphasesAndIntegersFromEdges
{
	public native void phasesAndIntegersFromEdges(RetVal retVal, float s, float xlambda[], int xlambda_size_1, float p[], int p_size_1, int m[], int m_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jphasesAndIntegersFromEdges(RetVal retVal, float s, float xlambda[], float p[], int m[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int xlambda_len1 = xlambda.length;
		int p_len1 = p.length;
		int m_len1 = m.length;
		// Call native method
		phasesAndIntegersFromEdges(retVal, s,xlambda,xlambda_len1,p,p_len1,m,m_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}