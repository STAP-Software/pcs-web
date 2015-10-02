package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class FindCentResult {

	private Subimage subimage;
	private FloatPoint guess;

	public FindCentResult() {
		
	}
	
	public FindCentResult(FloatPoint guess, Subimage subimage) {
		this.guess = guess;
		this.subimage = subimage;
	}
	
	// get/set centroid is convenience routine for procedure output
	public FloatPoint getCentroid() {
		return subimage.getCentroid();
	}
	
	public void setCentroid(FloatPoint centroid) {
		subimage = new Subimage(centroid, 0.0f, 0.0f, 0);
	}
	
	public Subimage getSubimage() {
		return subimage;
	}

	public void setSubimage(Subimage subimage) {
		this.subimage = subimage;
	}

	public FloatPoint getGuess() {
		return guess;
	}

	public void setGuess(FloatPoint guess) {
		this.guess = guess;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
		
	}
	
}
