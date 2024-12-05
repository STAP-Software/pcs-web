package org.tmt.aps.peas.lang.interop; 
public class JeClass
{
	public native void eClass(RetVal retVal, int n, int lista[], int lista_size_1, int listb[], int listb_size_1, int m, int nf[], int nf_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jeClass(RetVal retVal, int n, int lista[], int listb[], int m, int nf[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int lista_len1 = lista.length;
		int listb_len1 = listb.length;
		int nf_len1 = nf.length;
		// Call native method
		eClass(retVal, n,lista,lista_len1,listb,listb_len1,m,nf,nf_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}