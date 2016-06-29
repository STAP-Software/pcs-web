package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;

/**
 * Computation data result class for <b>findAndIdentify</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#findAndIdentify(float[][], int, org.tmt.aps.peas.config.model.FIConfig, org.tmt.aps.peas.refBeamMap.model.RefBeamMap, List, int[], boolean)
 */
public class FIResult {

	float xiRst[]; 
	float yiRst[];
	float xPeak[];
	float yPeak[];
	int nDetect[]; 
	
	float fourierQuality;
	float rotation;
	float scale;
	FloatPoint translation;
	 
	int n0123[];
	
	float ccdBoxesAll[][];
	float ccdBoxesSha[][];
	float ccdBoxesNum[][];
	
	int numFilledBoxes;
	float fracFilledBoxes;
	int nSolution;
	
	boolean handMarked = false;

	/**
	 * Null constructor
	 */
	public FIResult() {};
	
	/**
	 * Constructor that initializes arrays to zero values
	 * @param numSpots number of spots 
	 * @param frame the raw ccdFrame values
	 */
	public FIResult(int numSpots, float[][] frame) {
		xiRst = new float[numSpots]; 
		yiRst = new float[numSpots];
		xPeak = new float[numSpots];
		yPeak = new float[numSpots];
		nDetect = new int[numSpots]; 
		n0123 = new int[4];
		ccdBoxesAll = new float[frame.length][frame[0].length];
		ccdBoxesSha = new float[frame.length][frame[0].length];
		ccdBoxesNum = new float[frame.length][frame[0].length];
	}
	/**
	 * Constructor that creates an FIResult from a list of handMarks.  Sets the handMarked flag to true.
	 * This constructor is called from GetFrameCentroidsExecutor private handMark method, which is the alternate way to 
	 * build a FIResult when FindAndIdentify fails.
	 * @param handMarks the list of coordinates to construct FIResult with
	 * @param frame used for array initialization sizing only
	 * @see org.tmt.aps.peas.procedure.executor.GetFrameCentroidsExecutor
	 */
	public FIResult(List<FloatPoint> handMarks, float[][] frame) {
		int numSpots = handMarks.size();
		xiRst = new float[numSpots]; 
		yiRst = new float[numSpots];
		xPeak = new float[numSpots];
		yPeak = new float[numSpots];
		nDetect = new int[numSpots]; 
		n0123 = new int[4];
		ccdBoxesAll = new float[frame.length][frame[0].length];
		ccdBoxesSha = new float[frame.length][frame[0].length];
		ccdBoxesNum = new float[frame.length][frame[0].length];
		
		// set all nDetect to one and fill x and y peak with the handmarking
		xPeak = FloatPointListEncoder.extractXArray(handMarks);
		yPeak = FloatPointListEncoder.extractYArray(handMarks);
		for (int i=0; i<nDetect.length; i++) nDetect[i] = 1;
		
		translation = new FloatPoint(0.0f, 0.0f);
		
		handMarked = true;
	}

	public float[] getXiRst() {
		return xiRst;
	}

	public void setXiRst(float[] xiRst) {
		this.xiRst = xiRst;
	}

	public float[] getYiRst() {
		return yiRst;
	}

	public void setYiRst(float[] yiRst) {
		this.yiRst = yiRst;
	}

	public float[] getxPeak() {
		return xPeak;
	}

	public void setxPeak(float[] xPeak) {
		this.xPeak = xPeak;
	}

	public float[] getyPeak() {
		return yPeak;
	}

	public void setyPeak(float[] yPeak) {
		this.yPeak = yPeak;
	}

	public int[] getnDetect() {
		return nDetect;
	}

	public void setnDetect(int[] nDetect) {
		this.nDetect = nDetect;
	}

	public float getFourierQuality() {
		return fourierQuality;
	}

	public void setFourierQuality(float fourierQuality) {
		this.fourierQuality = fourierQuality;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public FloatPoint getTranslation() {
		return translation;
	}

	public void setTranslation(FloatPoint translation) {
		this.translation = translation;
	}

	public int[] getN0123() {
		return n0123;
	}

	public void setN0123(int[] n0123) {
		this.n0123 = n0123;
	}

	public float[][] getCcdBoxesAll() {
		return ccdBoxesAll;
	}

	public void setCcdBoxesAll(float[][] ccdBoxesAll) {
		this.ccdBoxesAll = ccdBoxesAll;
	}

	public float[][] getCcdBoxesSha() {
		return ccdBoxesSha;
	}

	public void setCcdBoxesSha(float[][] ccdBoxesSha) {
		this.ccdBoxesSha = ccdBoxesSha;
	}

	public float[][] getCcdBoxesNum() {
		return ccdBoxesNum;
	}

	public void setCcdBoxesNum(float[][] ccdBoxesNum) {
		this.ccdBoxesNum = ccdBoxesNum;
	}

	public int getNumFilledBoxes() {
		return numFilledBoxes;
	}

	public void setNumFilledBoxes(int numFilledBoxes) {
		this.numFilledBoxes = numFilledBoxes;
	}

	public float getFracFilledBoxes() {
		return fracFilledBoxes;
	}

	public void setFracFilledBoxes(float fracFilledBoxes) {
		this.fracFilledBoxes = fracFilledBoxes;
	}

	public int getnSolution() {
		return nSolution;
	}

	public void setnSolution(int nSolution) {
		this.nSolution = nSolution;
	}

	public List<FloatPoint> getPeakLocationList() {
		return FloatPointListEncoder.constructFromXandY(getxPeak(), getyPeak());
	}
	
	public FloatPoint[] getPeakLocationArray() {
		return (FloatPoint[])getPeakLocationList().toArray(new FloatPoint[0]);
	}
	
	public List<FloatPoint> getRstLocationList() {
		return FloatPointListEncoder.constructFromXandY(getXiRst(), getYiRst());
	}
	
	public FloatPoint[] getRstLocationArray() {
		return (FloatPoint[])getRstLocationList().toArray(new FloatPoint[0]);
	}
	
	public boolean isHandMarked() {
		return handMarked;
	}

	public void setHandMarked(boolean handMarked) {
		this.handMarked = handMarked;
	}

	public boolean allDetectionsSinglePeaks() {
		for (int peak : nDetect) {
			if (peak != 1) {
				return false;
			}
		}
		return true;
	}
}
