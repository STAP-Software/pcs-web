package org.tmt.aps.peas.lang.interop; 
public class JreverseTemplate
{
	public native void reverseTemplate(RetVal retVal, float template_c[], int template_c_size_1, int template_c_size_2, int template_c_size_3, int template_c_size_4, float template_r[], int template_r_size_1, int template_r_size_2, int template_r_size_3, int template_r_size_4 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jreverseTemplate(RetVal retVal, float template_c[][][][], float template_r[][][][] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int template_c_len1 = template_c.length;
		int template_c_len2 = template_c[0].length;
		int template_c_len3 = template_c[0][0].length;
		int template_c_len4 = template_c[0][0][0].length;
		float[] template_c_collapse = new float[template_c_len1 * template_c_len2 * template_c_len3 * template_c_len4];
		int template_r_len1 = template_r.length;
		int template_r_len2 = template_r[0].length;
		int template_r_len3 = template_r[0][0].length;
		int template_r_len4 = template_r[0][0][0].length;
		float[] template_r_collapse = new float[template_r_len1 * template_r_len2 * template_r_len3 * template_r_len4];
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
		// collapse array to one dimension
		for (int i=0; i<template_r_len1; i++) { 
			for (int j=0; j<template_r_len2; j++) { 
			for (int k=0; k<template_r_len3; k++) { 
			for (int l=0; l<template_r_len4; l++) { 
				template_r_collapse[i*template_r_len2 * template_r_len3 * template_r_len4 + j * template_r_len3 * template_r_len4 + k * template_r_len4 + l] = template_r[i][j][k][l]; 
			} 
			} 
			} 
		} 
		// Call native method
		reverseTemplate(retVal, template_c_collapse,template_c_len1,template_c_len2,template_c_len3,template_c_len4,template_r_collapse,template_r_len1,template_r_len2,template_r_len3,template_r_len4);
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
		// expand array to four dimensions
		for (int i=0; i<template_r_len1; i++) { 
			for (int j=0; j<template_r_len2; j++) { 
			for (int k=0; k<template_r_len3; k++) { 
			for (int l=0; l<template_r_len4; l++) { 
				template_r[i][j][k][l] = template_r_collapse[i*template_r_len2 * template_r_len3 *template_r_len4 +j * template_r_len3 *  template_r_len4 +  k * template_r_len4 +   l];
			} 
			} 
			} 
		} 
		// Assign output variables
		Object[] out = new Object[0];
		return out;
	}
}