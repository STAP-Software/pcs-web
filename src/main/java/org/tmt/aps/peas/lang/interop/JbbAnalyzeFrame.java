package org.tmt.aps.peas.lang.interop; 
public class JbbAnalyzeFrame
{
	public native void bbAnalyzeFrame(RetVal retVal, float frame[], int frame_size_1, int frame_size_2, float xCent[], int xCent_size_1, float yCent[], int yCent_size_1, int validSubimages[], int validSubimages_size_1, int edge_angle[], int edge_angle_size_1, float template_c[], int template_c_size_1, int template_c_size_2, int template_c_size_3, int template_c_size_4, float coherence_out[], int coherence_out_size_1, float bestCorrelationIndex[], int bestCorrelationIndex_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jbbAnalyzeFrame(RetVal retVal, float frame[][], float xCent[], float yCent[], int validSubimages[], int edge_angle[], float template_c[][][][], float coherence_out[], float bestCorrelationIndex[] ) {
		// Output variable definitions
		// Deal with Array Lengths
		int frame_len1 = frame.length;
		int frame_len2 = frame[0].length;
		float[] frame_collapse = new float[frame_len1 * frame_len2];
		int xCent_len1 = xCent.length;
		int yCent_len1 = yCent.length;
		int validSubimages_len1 = validSubimages.length;
		int edge_angle_len1 = edge_angle.length;
		int template_c_len1 = template_c.length;
		int template_c_len2 = template_c[0].length;
		int template_c_len3 = template_c[0][0].length;
		int template_c_len4 = template_c[0][0][0].length;
		float[] template_c_collapse = new float[template_c_len1 * template_c_len2 * template_c_len3 * template_c_len4];
		int coherence_out_len1 = coherence_out.length;
		int bestCorrelationIndex_len1 = bestCorrelationIndex.length;
		// collapse array to one dimension
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame_collapse[i*frame_len2 + j] = frame[i][j]; 
			} 
		} 
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
		bbAnalyzeFrame(retVal, frame_collapse,frame_len1,frame_len2,xCent,xCent_len1,yCent,yCent_len1,validSubimages,validSubimages_len1,edge_angle,edge_angle_len1,template_c_collapse,template_c_len1,template_c_len2,template_c_len3,template_c_len4,coherence_out,coherence_out_len1,bestCorrelationIndex,bestCorrelationIndex_len1);
		// expand array to two dimensions
		for (int i=0; i<frame_len1; i++) { 
			for (int j=0; j<frame_len2; j++) { 
				frame[i][j] = frame_collapse[i*frame_len2 + j];
			} 
		} 
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