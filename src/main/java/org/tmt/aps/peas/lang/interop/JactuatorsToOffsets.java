package org.tmt.aps.peas.lang.interop; 
public class JactuatorsToOffsets
{
	public native void actuatorsToOffsets(RetVal retVal, float z[], int z_size_1, float x_act[], int x_act_size_1, float y_act[], int y_act_size_1, float secperpix, float x_offset_act[], int x_offset_act_size_1, float y_offset_act[], int y_offset_act_size_1, float piston_act[], int piston_act_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jactuatorsToOffsets(RetVal retVal, float z[], float x_act[], float y_act[], float secperpix, float x_offset_act[], float y_offset_act[], float piston_act[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int z_len1 = z.length;
		int x_act_len1 = x_act.length;
		int y_act_len1 = y_act.length;
		int x_offset_act_len1 = x_offset_act.length;
		int y_offset_act_len1 = y_offset_act.length;
		int piston_act_len1 = piston_act.length;
		// Call native method
		actuatorsToOffsets(retVal, z,z_len1,x_act,x_act_len1,y_act,y_act_len1,secperpix,x_offset_act,x_offset_act_len1,y_offset_act,y_offset_act_len1,piston_act,piston_act_len1);
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}