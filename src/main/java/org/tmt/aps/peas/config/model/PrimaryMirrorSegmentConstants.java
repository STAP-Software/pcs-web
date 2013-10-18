package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;


public class PrimaryMirrorSegmentConstants {

	FloatPoint fine[];  //FIXME: maybe we need more descriptive names here
	FloatPoint sufs[];  //FIXME: maybe we need more descriptive names here
	
	
	public FloatPoint[] getFine() {
		return fine;
	}
	
	public void setFine(FloatPoint[] fine) {
		this.fine = fine;
	}
	
	public FloatPoint[] getSufs() {
		return sufs;
	}
	
	public void setSufs(FloatPoint[] sufs) {
		this.sufs = sufs;
	}
	
	
}
