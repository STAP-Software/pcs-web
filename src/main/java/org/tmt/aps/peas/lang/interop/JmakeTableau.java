package org.tmt.aps.peas.lang.interop; 
public class JmakeTableau
{
	public native void makeTableau(RetVal retVal, float table[], int table_size_1, int table_size_2, int table_size_3, int table_size_4 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmakeTableau(RetVal retVal, float table[][][][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int table_len1 = table.length;
		int table_len2 = table[0].length;
		int table_len3 = table[0][0].length;
		int table_len4 = table[0][0][0].length;
		float[] table_collapse = new float[table_len1 * table_len2 * table_len3 * table_len4];
		// Call native method
		makeTableau(retVal, table_collapse,table_len1,table_len2,table_len3,table_len4);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}