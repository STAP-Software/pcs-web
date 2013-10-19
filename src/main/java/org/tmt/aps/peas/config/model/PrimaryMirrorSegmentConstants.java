package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;


public class PrimaryMirrorSegmentConstants {

	FloatPoint fineSpotCoordinates[];  
	FloatPoint sufsSpotCoordinates[];
	
	public FloatPoint[] getFineSpotCoordinates() {
		return fineSpotCoordinates;
	}
	
	public void setFineSpotCoordinates(FloatPoint[] fineSpotCoordinates) {
		this.fineSpotCoordinates = fineSpotCoordinates;
	}
	
	public FloatPoint[] getSufsSpotCoordinates() {
		return sufsSpotCoordinates;
	}
	
	public void setSufsSpotCoordinates(FloatPoint[] sufsSpotCoordinates) {
		this.sufsSpotCoordinates = sufsSpotCoordinates;
	} 
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nfineSpotCoordinates = ");
		for (int i=0; i<fineSpotCoordinates.length; i++) {
			buf.append(fineSpotCoordinates[i] + ", ");
		}
		
		buf.append("\nsufsSpotCoordinates = ");
		for (int i=0; i<sufsSpotCoordinates.length; i++) {
			buf.append(sufsSpotCoordinates[i] + ", ");
		}
		
		buf.append("\n");
		return buf.toString();
	}


	
	
}
