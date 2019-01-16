package org.tmt.aps.peas.lang.interop; 
public class JcalculateM2M1Analytical
{
	public native void calculateM2M1Analytical(RetVal retVal, float rawFsCentroidOffsetsX[], int rawFsCentroidOffsetsX_size_1, float rawFsCentroidOffsetsY[], int rawFsCentroidOffsetsY_size_1, int validSubimages[], int validSubimages_size_1, int subimagesForM2Calculation[], int subimagesForM2Calculation_size_1, float xLensletLocations[], int xLensletLocations_size_1, int xLensletLocations_size_2, float yLensletLocations[], int yLensletLocations_size_1, int yLensletLocations_size_2, float bfd, float f1, float f_final, float m2_tt_correction_factor, float m1OuterDiameter, float aHex, int startSegmentNumber, int stopSegmentNumber, float M2Piston[], float M2TipTilt[], int M2TipTilt_size_1, float M2TipTiltTelescope[], int M2TipTiltTelescope_size_1, float CentroidResidual[], float M1MeanOffsetsCorrectedForM2X[], int M1MeanOffsetsCorrectedForM2X_size_1, float M1MeanOffsetsCorrectedForM2Y[], int M1MeanOffsetsCorrectedForM2Y_size_1 );
	static { System.loadLibrary("peas"); }
	// TODO: We need to write the public method that calls the private and unpacks output arrays

	public Object[] jcalculateM2M1Analytical(RetVal retVal, float rawFsCentroidOffsetsX[], float rawFsCentroidOffsetsY[], int validSubimages[], int subimagesForM2Calculation[], float xLensletLocations[][], float yLensletLocations[][], float bfd, float f1, float f_final, float m2_tt_correction_factor, float m1OuterDiameter, float aHex, int startSegmentNumber, int stopSegmentNumber, float M2TipTilt[], float M2TipTiltTelescope[], float M1MeanOffsetsCorrectedForM2X[], float M1MeanOffsetsCorrectedForM2Y[] ) {
		// Output variable definitions
		float M2Piston_outArray[] = new float[1];
		float CentroidResidual_outArray[] = new float[1];
		// Deal with Array Lengths
		int rawFsCentroidOffsetsX_len1 = rawFsCentroidOffsetsX.length;
		int rawFsCentroidOffsetsY_len1 = rawFsCentroidOffsetsY.length;
		int validSubimages_len1 = validSubimages.length;
		int subimagesForM2Calculation_len1 = subimagesForM2Calculation.length;
		int xLensletLocations_len1 = xLensletLocations.length;
		int xLensletLocations_len2 = xLensletLocations[0].length;
		float[] xLensletLocations_collapse = new float[xLensletLocations_len1 * xLensletLocations_len2];
		int yLensletLocations_len1 = yLensletLocations.length;
		int yLensletLocations_len2 = yLensletLocations[0].length;
		float[] yLensletLocations_collapse = new float[yLensletLocations_len1 * yLensletLocations_len2];
		int M2TipTilt_len1 = M2TipTilt.length;
		int M2TipTiltTelescope_len1 = M2TipTiltTelescope.length;
		int M1MeanOffsetsCorrectedForM2X_len1 = M1MeanOffsetsCorrectedForM2X.length;
		int M1MeanOffsetsCorrectedForM2Y_len1 = M1MeanOffsetsCorrectedForM2Y.length;
		// collapse array to one dimension
		for (int i=0; i<xLensletLocations_len1; i++) { 
			for (int j=0; j<xLensletLocations_len2; j++) { 
				xLensletLocations_collapse[i*xLensletLocations_len2 + j] = xLensletLocations[i][j]; 
			} 
		} 
		// collapse array to one dimension
		for (int i=0; i<yLensletLocations_len1; i++) { 
			for (int j=0; j<yLensletLocations_len2; j++) { 
				yLensletLocations_collapse[i*yLensletLocations_len2 + j] = yLensletLocations[i][j]; 
			} 
		} 
		// Call native method
		calculateM2M1Analytical(retVal, rawFsCentroidOffsetsX,rawFsCentroidOffsetsX_len1,rawFsCentroidOffsetsY,rawFsCentroidOffsetsY_len1,validSubimages,validSubimages_len1,subimagesForM2Calculation,subimagesForM2Calculation_len1,xLensletLocations_collapse,xLensletLocations_len1,xLensletLocations_len2,yLensletLocations_collapse,yLensletLocations_len1,yLensletLocations_len2,bfd,f1,f_final,m2_tt_correction_factor,m1OuterDiameter,aHex,startSegmentNumber,stopSegmentNumber,M2Piston_outArray,M2TipTilt,M2TipTilt_len1,M2TipTiltTelescope,M2TipTiltTelescope_len1,CentroidResidual_outArray,M1MeanOffsetsCorrectedForM2X,M1MeanOffsetsCorrectedForM2X_len1,M1MeanOffsetsCorrectedForM2Y,M1MeanOffsetsCorrectedForM2Y_len1);
		// expand array to two dimensions
		for (int i=0; i<xLensletLocations_len1; i++) { 
			for (int j=0; j<xLensletLocations_len2; j++) { 
				xLensletLocations[i][j] = xLensletLocations_collapse[i*xLensletLocations_len2 + j];
			} 
		} 
		// expand array to two dimensions
		for (int i=0; i<yLensletLocations_len1; i++) { 
			for (int j=0; j<yLensletLocations_len2; j++) { 
				yLensletLocations[i][j] = yLensletLocations_collapse[i*yLensletLocations_len2 + j];
			} 
		} 
		// Assign output variables
		Object[] out = new Object[2];
		out[0] = M2Piston_outArray[0];
		out[1] = CentroidResidual_outArray[0];
		return out;
	}
}