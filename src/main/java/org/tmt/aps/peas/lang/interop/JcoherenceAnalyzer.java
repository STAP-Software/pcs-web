package org.tmt.aps.peas.lang.interop; 
public class JcoherenceAnalyzer
{
	public native void coherenceAnalyzer(RetVal retVal, float coherence[], int coherence_size_1, int edgeFlag[], int edgeFlag_size_1, float fracSeeing, int numberSeeingEdgesEdges[], float coherenceMean[], float coherenceSeeing[], float coherenceAsymmetry[], float coherenceAsymmetryUncertainty[] );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcoherenceAnalyzer(RetVal retVal, float coherence[], int edgeFlag[], float fracSeeing ) {
		// Output variable definitions
		int numberSeeingEdgesEdges_outArray[] = new int[1];
		float coherenceMean_outArray[] = new float[1];
		float coherenceSeeing_outArray[] = new float[1];
		float coherenceAsymmetry_outArray[] = new float[1];
		float coherenceAsymmetryUncertainty_outArray[] = new float[1];
		// Deal with Array Lengths
		int coherence_len1 = coherence.length;
		int edgeFlag_len1 = edgeFlag.length;
		// Call native method
		coherenceAnalyzer(retVal, coherence,coherence_len1,edgeFlag,edgeFlag_len1,fracSeeing,numberSeeingEdgesEdges_outArray,coherenceMean_outArray,coherenceSeeing_outArray,coherenceAsymmetry_outArray,coherenceAsymmetryUncertainty_outArray);
		// Assign output variables
		Object[] out = new Object[5];
		out[0] = numberSeeingEdgesEdges_outArray[0];
		out[1] = coherenceMean_outArray[0];
		out[2] = coherenceSeeing_outArray[0];
		out[3] = coherenceAsymmetry_outArray[0];
		out[4] = coherenceAsymmetryUncertainty_outArray[0];
		return out;
	}
}