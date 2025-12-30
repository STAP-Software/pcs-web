package org.tmt.aps.peas.lang.interop; 
public class JmakePeripheralTemplate
{
	public native void makePeripheralTemplate(RetVal retVal, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float r_microns, float template_p[], int template_p_size_1, int template_p_size_2, int template_p_size_3, int template_p_size_4 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jmakePeripheralTemplate(RetVal retVal, int nbins, int ntemp_size_fft, int itermax, int imargin, int ngauss, int irad_cent, int ncent, float sec_per_pix, float xlambda0, float r_microns, float template_p[][][][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int template_p_len1 = template_p.length;
		int template_p_len2 = template_p[0].length;
		int template_p_len3 = template_p[0][0].length;
		int template_p_len4 = template_p[0][0][0].length;
		float[] template_p_collapse = new float[template_p_len1 * template_p_len2 * template_p_len3 * template_p_len4];
		// collapse array to one dimension
		for (int i=0; i<template_p_len1; i++) { 
			for (int j=0; j<template_p_len2; j++) { 
			for (int k=0; k<template_p_len3; k++) { 
			for (int l=0; l<template_p_len4; l++) { 
				template_p_collapse[i*template_p_len2 * template_p_len3 * template_p_len4 + j * template_p_len3 * template_p_len4 + k * template_p_len4 + l] = template_p[i][j][k][l]; 
			} 
			} 
			} 
		} 
		// Call native method
		makePeripheralTemplate(retVal, nbins,ntemp_size_fft,itermax,imargin,ngauss,irad_cent,ncent,sec_per_pix,xlambda0,r_microns,template_p_collapse,template_p_len1,template_p_len2,template_p_len3,template_p_len4);
		// expand array to four dimensions
		for (int i=0; i<template_p_len1; i++) { 
			for (int j=0; j<template_p_len2; j++) { 
			for (int k=0; k<template_p_len3; k++) { 
			for (int l=0; l<template_p_len4; l++) { 
				template_p[i][j][k][l] = template_p_collapse[i*template_p_len2 * template_p_len3 *template_p_len4 +j * template_p_len3 *  template_p_len4 +  k * template_p_len4 +   l];
			} 
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}