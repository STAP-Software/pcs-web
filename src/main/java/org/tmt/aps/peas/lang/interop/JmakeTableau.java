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
		// collapse array to one dimension
		for (int i=0; i<table_len1; i++) { 
			for (int j=0; j<table_len2; j++) { 
			for (int k=0; k<table_len3; k++) { 
			for (int l=0; l<table_len4; l++) { 
				table_collapse[i*table_len2 * table_len3 * table_len4 + j * table_len3 * table_len4 + k * table_len4 + l] = table[i][j][k][l]; 
			} 
			} 
			} 
		} 
		// Call native method
		makeTableau(retVal, table_collapse,table_len1,table_len2,table_len3,table_len4);
		// expand array to four dimensions
		for (int i=0; i<table_len1; i++) { 
			for (int j=0; j<table_len2; j++) { 
			for (int k=0; k<table_len3; k++) { 
			for (int l=0; l<table_len4; l++) { 
				table[i][j][k][l] = table_collapse[i*table_len2 * table_len3 *table_len4 +j * table_len3 *  table_len4 +  k * table_len4 +   l];
			} 
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}