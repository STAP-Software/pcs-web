package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Model for a subimage: a centroid, subimage intensity, peak intensity and a find cent status flag.
 * Used in {@link org.tmt.aps.peas.frame.ui.FrameController}, {@link org.tmt.aps.peas.procedure.ui.ProcedureController} and 
 * {@link org.tmt.aps.peas.procedure.executor.GetFrameCentroidsExecutor} for hand-marking of centroids.
 * Used in {@link FindCentResult}, {@link FindCentroidsResult} when auto identifying centroids
 * @author smichaels
 * @see org.tmt.aps.peas.computation.model.FindCentResult
 * @see org.tmt.aps.peas.computation.model.FindCentroidsResult
 *  
 */
public class Subimage {

	FloatPoint centroid;
	
	float subimageIntensity;
	float peakIntensity;
	float rawPeakIntensity;
	int findCentStatus;
	
	public Subimage(FloatPoint centroid, float subimageIntensity, float peakIntensity, float rawPeakIntensity, int findCentStatus) {
		this.centroid = centroid;
		this.subimageIntensity = subimageIntensity;
		this.peakIntensity = peakIntensity;
		this.rawPeakIntensity = rawPeakIntensity;
		this.findCentStatus = findCentStatus;
	}
	
	public FloatPoint getCentroid() {
		return centroid;
	}
	public void setCentroid(FloatPoint centroid) {
		this.centroid = centroid;
	}
	public float getSubimageIntensity() {
		return subimageIntensity;
	}
	public void setSubimageIntensity(float subimageIntensity) {
		this.subimageIntensity = subimageIntensity;
	}
	public float getPeakIntensity() {
		return peakIntensity;
	}
	public void setPeakIntensity(float peakIntensity) {
		this.peakIntensity = peakIntensity;
	}

	public float getRawPeakIntensity() {
		return rawPeakIntensity;
	}

	public void setRawPeakIntensity(float rawPeakIntensity) {
		this.rawPeakIntensity = rawPeakIntensity;
	}

	public int getFindCentStatus() {
		return findCentStatus;
	}

	public void setFindCentStatus(int findCentStatus) {
		this.findCentStatus = findCentStatus;
	}
	
	public boolean isGoodCentroid() {
		return findCentStatus == 0;
	}
	
}
