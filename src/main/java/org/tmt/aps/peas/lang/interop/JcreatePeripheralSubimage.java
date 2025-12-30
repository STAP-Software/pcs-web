package org.tmt.aps.peas.lang.interop; 
public class JcreatePeripheralSubimage
{
	public native void createPeripheralSubimage(RetVal retVal, float ymax_mm, int iangle, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float r_microns, float template_1[], int template_1_size_1, int template_1_size_2 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcreatePeripheralSubimage(RetVal retVal, float ymax_mm, int iangle, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float r_microns, float template_1[][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int template_1_len1 = template_1.length;
		int template_1_len2 = template_1[0].length;
		float[] template_1_collapse = new float[template_1_len1 * template_1_len2];
		// collapse array to one dimension
		for (int i=0; i<template_1_len1; i++) { 
			for (int j=0; j<template_1_len2; j++) { 
				template_1_collapse[i*template_1_len2 + j] = template_1[i][j]; 
			} 
		} 
		// Call native method
		createPeripheralSubimage(retVal, ymax_mm,iangle,nbins,ntemp_size_fft,itermax,imargin,ngauss,irad_cent,ncent,sec_per_pix,xlambda0,r_microns,template_1_collapse,template_1_len1,template_1_len2);
		// expand array to two dimensions
		for (int i=0; i<template_1_len1; i++) { 
			for (int j=0; j<template_1_len2; j++) { 
				template_1[i][j] = template_1_collapse[i*template_1_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}