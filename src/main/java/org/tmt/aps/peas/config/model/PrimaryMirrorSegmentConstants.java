package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Constants data class containing primary mirror segment constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class PrimaryMirrorSegmentConstants {

	
	FloatPoint sufsSpotCoordinates[];
	float peripheralSpotPerp[];
	float peripheralSpotParallel[];
	float peripheralSpotTheta[];
	
	
	public FloatPoint[] getSufsSpotCoordinates() {
		return sufsSpotCoordinates;
	}
	
	public void setSufsSpotCoordinates(FloatPoint[] sufsSpotCoordinates) {
		this.sufsSpotCoordinates = sufsSpotCoordinates;
	} 

	public float[] getPeripheralSpotPerp() {
		return peripheralSpotPerp;
	}

	public void setPeripheralSpotPerp(float[] peripheralSpotPerp) {
		this.peripheralSpotPerp = peripheralSpotPerp;
	}

	public float[] getPeripheralSpotParallel() {
		return peripheralSpotParallel;
	}

	public void setPeripheralSpotParallel(float[] peripheralSpotParallel) {
		this.peripheralSpotParallel = peripheralSpotParallel;
	}

	public float[] getPeripheralSpotTheta() {
		return peripheralSpotTheta;
	}

	public void setPeripheralSpotTheta(float[] peripheralSpotTheta) {
		this.peripheralSpotTheta = peripheralSpotTheta;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("\nsufsSpotCoordinates = ");
		for (int i=0; i<sufsSpotCoordinates.length; i++) {
			buf.append(sufsSpotCoordinates[i] + ", ");
		}

		buf.append("\nperipheralSpotPerp = ");
		for (int i=0; i<peripheralSpotPerp.length; i++) {
			buf.append(peripheralSpotPerp[i] + ", ");
		}
		
		buf.append("\nperipheralSpotParallel = ");
		for (int i=0; i<peripheralSpotParallel.length; i++) {
			buf.append(peripheralSpotParallel[i] + ", ");
		}
		
		buf.append("\nperipheralSpotThetap = ");
		for (int i=0; i<peripheralSpotTheta.length; i++) {
			buf.append(peripheralSpotTheta[i] + ", ");
		}
		
		buf.append("\n");
		return buf.toString();
	}


	
	
}
