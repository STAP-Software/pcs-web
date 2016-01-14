package org.tmt.aps.peas.lang.interop; 
public class JmakeTemplate
{
	public native void makeTemplate(RetVal retVal, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int nspot_type, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float hw_microns, float r_microns, float template_c[], int template_c_size_1, int template_c_size_2, int template_c_size_3, int template_c_size_4 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmakeTemplate(RetVal retVal, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int nspot_type, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float hw_microns, float r_microns, float template_c[][][][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int template_c_len1 = template_c.length;
		int template_c_len2 = template_c[0].length;
		int template_c_len3 = template_c[0][0].length;
		int template_c_len4 = template_c[0][0][0].length;
		float[] template_c_collapse = new float[template_c_len1 * template_c_len2 * template_c_len3 * template_c_len4];
		// collapse array to one dimension
		for (int i=0; i<template_c_len1; i++) { 
			for (int j=0; j<template_c_len2; j++) { 
			for (int k=0; k<template_c_len3; k++) { 
			for (int l=0; l<template_c_len4; l++) { 
				template_c_collapse[i*template_c_len2 * template_c_len3 * template_c_len4 + j * template_c_len3 * template_c_len4 + k * template_c_len4 + l] = template_c[i][j][k][l]; 
			} 
			} 
			} 
		} 
		// Call native method
		makeTemplate(retVal, nbins,ntemp_size_fft,itermax,imargin,ngauss,nspot_type,irad_cent,ncent,sec_per_pix,xlambda0,hw_microns,r_microns,template_c_collapse,template_c_len1,template_c_len2,template_c_len3,template_c_len4);
		// expand array to four dimensions
		for (int i=0; i<template_c_len1; i++) { 
			for (int j=0; j<template_c_len2; j++) { 
			for (int k=0; k<template_c_len3; k++) { 
			for (int l=0; l<template_c_len4; l++) { 
				template_c[i][j][k][l] = template_c_collapse[i*template_c_len2 * template_c_len3 *template_c_len4 +j * template_c_len3 *  template_c_len4 +  k * template_c_len4 +   l];
			} 
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}