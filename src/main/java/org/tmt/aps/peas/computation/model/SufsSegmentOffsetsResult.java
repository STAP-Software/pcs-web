package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

public class SufsSegmentOffsetsResult {

	FloatPoint[] imageTranslation;
	float[] imageScale;
	float[] imageRotation;
	private FloatPoint[][] ccdCentroidOffsets;
	private FloatPoint[][] cartesianCentroidOffsets;
	
	int[][] spotJumped;
	int[][] validOffsets;
	
	public SufsSegmentOffsetsResult(CentroidOffsetsResult[] segmentCentroidOffsetsResults, int[][] spotJumped, int[][] validOffsets) {
		
		int offsetCount = segmentCentroidOffsetsResults[0].getCartesianCentroidOffsets().length;
		imageTranslation = new FloatPoint[segmentCentroidOffsetsResults.length];
		imageScale = new float[segmentCentroidOffsetsResults.length];
		imageRotation = new float[segmentCentroidOffsetsResults.length];
		ccdCentroidOffsets = new FloatPoint[segmentCentroidOffsetsResults.length][offsetCount];
		cartesianCentroidOffsets = new FloatPoint[segmentCentroidOffsetsResults.length][offsetCount];
		
		for (int i=0; i<segmentCentroidOffsetsResults.length; i++) {
			imageTranslation[i] = segmentCentroidOffsetsResults[i].getImageTranslation();
			imageScale[i] = segmentCentroidOffsetsResults[i].getImageScale();
			imageRotation[i] = segmentCentroidOffsetsResults[i].getImageRotation();
			ccdCentroidOffsets[i] = segmentCentroidOffsetsResults[i].getCcdCentroidOffsets();
			cartesianCentroidOffsets[i] = segmentCentroidOffsetsResults[i].getCartesianCentroidOffsets();
		}
		
		this.spotJumped = spotJumped;
		this.validOffsets = validOffsets;
	}

	public SufsSegmentOffsetsResult() {
		
	}
	
	public FloatPoint[] getImageTranslation() {
		return imageTranslation;
	}

	public void setImageTranslation(FloatPoint[] imageTranslation) {
		this.imageTranslation = imageTranslation;
	}

	public float[] getImageScale() {
		return imageScale;
	}

	public void setImageScale(float[] imageScale) {
		this.imageScale = imageScale;
	}

	public float[] getImageRotation() {
		return imageRotation;
	}

	public void setImageRotation(float[] imageRotation) {
		this.imageRotation = imageRotation;
	}

	public FloatPoint[][] getCcdCentroidOffsets() {
		return ccdCentroidOffsets;
	}

	public void setCcdCentroidOffsets(FloatPoint[][] ccdCentroidOffsets) {
		this.ccdCentroidOffsets = ccdCentroidOffsets;
	}

	public FloatPoint[][] getCartesianCentroidOffsets() {
		return cartesianCentroidOffsets;
	}

	public void setCartesianCentroidOffsets(FloatPoint[][] cartesianCentroidOffsets) {
		this.cartesianCentroidOffsets = cartesianCentroidOffsets;
	}

	public int[][] getSpotJumped() {
		return spotJumped;
	}

	public void setSpotJumped(int[][] spotJumped) {
		this.spotJumped = spotJumped;
	}

	public int[][] getValidOffsets() {
		return validOffsets;
	}

	public void setValidOffsets(int[][] validOffsets) {
		this.validOffsets = validOffsets;
	}

	public CentroidOffsetsResult extractCentroidOffsetsResult(int segmentNumber) {
		return new CentroidOffsetsResult(imageTranslation[segmentNumber], imageScale[segmentNumber], imageRotation[segmentNumber], 
				ccdCentroidOffsets[segmentNumber], cartesianCentroidOffsets[segmentNumber], validOffsets[segmentNumber]);
	}
	
	public List<SufsSegmentSpot> spotsJumpedString() {
		
		List<SufsSegmentSpot> result = new ArrayList<SufsSegmentSpot>();
		for (int segmentNumber=0; segmentNumber<6; segmentNumber++) {
			int[] jumpedSpots = getJumpedSpots(segmentNumber);
			for (int i=0; i<jumpedSpots.length; i++) {
				SufsSegmentSpot jumpedSpot = new SufsSegmentSpot(segmentNumber+1, i+1);
				result.add(jumpedSpot);
			}
		}
		return result;
	}

	public int[] getJumpedSpots(int segmentNumber) {
		List<Integer> jumpList = new ArrayList<Integer>();
		for (int i=0; i<spotJumped[segmentNumber].length; i++) {
			if (spotJumped[segmentNumber][i] == 1) {
				jumpList.add(i);
			}
		}
		int[] jumpListArray = new int[jumpList.size()];
		for (int i=0; i< jumpListArray.length; i++) {
			jumpListArray[i] = jumpList.get(i);
		}
		return jumpListArray;
	}
	
	public class SufsSegmentSpot {
		int segmentGroupNumber;
		int spotNumber;
		public SufsSegmentSpot(int segmentGroupNumber, int spotNumber) {
			this.segmentGroupNumber = segmentGroupNumber;
			this.spotNumber = spotNumber;
		}
		public int getSegmentGroupNumber() {
			return segmentGroupNumber;
		}
		public void setSegmentGroupNumber(int segmentGroupNumber) {
			this.segmentGroupNumber = segmentGroupNumber;
		}
		public int getSpotNumber() {
			return spotNumber;
		}
		public void setSpotNumber(int spotNumber) {
			this.spotNumber = spotNumber;
		}
		
	}
	
	
}
