package org.tmt.aps.peas.lang.interop; 
public class JidentifyIslands
{
	public native void identifyIslands(RetVal retVal, int numSegment, int numSensor, int plusPiston[], int plusPiston_size_1, int minusPiston[], int minusPiston_size_1, int phasingFlag[], int phasingFlag_size_1, int numClassTot[], int classNumber[], int classNumber_size_1, int classContents[], int classContents_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jidentifyIslands(RetVal retVal, int numSegment, int numSensor, int plusPiston[], int minusPiston[], int phasingFlag[], int classNumber[], int classContents[] ) {
		// Output variable definitions
		int numClassTot_outArray[] = new int[1];
		// Deal with Array Lengths
		int plusPiston_len1 = plusPiston.length;
		int minusPiston_len1 = minusPiston.length;
		int phasingFlag_len1 = phasingFlag.length;
		int classNumber_len1 = classNumber.length;
		int classContents_len1 = classContents.length;
		// Call native method
		identifyIslands(retVal, numSegment,numSensor,plusPiston,plusPiston_len1,minusPiston,minusPiston_len1,phasingFlag,phasingFlag_len1,numClassTot_outArray,classNumber,classNumber_len1,classContents,classContents_len1);
		// Assign output variables
		Object[] out = new Object[1];
		out[0] = numClassTot_outArray[0];
		return out;
	}
}