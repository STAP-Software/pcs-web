package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Constants data class containing mask constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class MaskConstants {
		
	FloatPoint[] passiveTiltTheoreticalLocations;
	FloatPoint[] fineScreenTheoreticalLocations;
	FloatPoint[] phasingTheoreticalLocations;
	FloatPoint[] sufsTheoreticalLocations;
	
	float maskRotationDifferenceThreshold;

	
	public FloatPoint[] getPassiveTiltTheoreticalLocations() {
		return passiveTiltTheoreticalLocations;
	}

	public void setPassiveTiltTheoreticalLocations(FloatPoint[] passiveTiltTheoreticalLocations) {
		this.passiveTiltTheoreticalLocations = passiveTiltTheoreticalLocations;
	}


	public FloatPoint[] getFineScreenTheoreticalLocations() {
		return fineScreenTheoreticalLocations;
	}


	public void setFineScreenTheoreticalLocations(FloatPoint[] fineScreenTheoreticalLocations) {
		this.fineScreenTheoreticalLocations = fineScreenTheoreticalLocations;
	}


	public FloatPoint[] getPhasingTheoreticalLocations() {
		return phasingTheoreticalLocations;
	}


	public void setPhasingTheoreticalLocations(FloatPoint[] phasingTheoreticalLocations) {
		this.phasingTheoreticalLocations = phasingTheoreticalLocations;
	}


	public FloatPoint[] getSufsTheoreticalLocations() {
		return sufsTheoreticalLocations;
	}


	public void setSufsTheoreticalLocations(FloatPoint[] sufsTheoreticalLocations) {
		this.sufsTheoreticalLocations = sufsTheoreticalLocations;
	}

	public float getMaskRotationDifferenceThreshold() {
		return maskRotationDifferenceThreshold;
	}

	public void setMaskRotationDifferenceThreshold(float maskRotationDifferenceThreshold) {
		this.maskRotationDifferenceThreshold = maskRotationDifferenceThreshold;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nPassiveTiltTheoreticalLocations = ");
		for (FloatPoint location : passiveTiltTheoreticalLocations) {
			buf.append(location + ", ");
		}
		buf.append("\n");
		
		buf.append("\nFineScreenTheoreticalLocations = ");
		for (FloatPoint location : fineScreenTheoreticalLocations) {
			buf.append(location + ", ");
		}
		buf.append("\n");
		
		buf.append("\nPhasingTheoreticalLocations = ");
		for (FloatPoint location : phasingTheoreticalLocations) {
			buf.append(location + ", ");
		}
		buf.append("\n");
		

		buf.append("\nsufsTheoreticalLocations = ");
		buf.append("\nsize = " + sufsTheoreticalLocations.length + "\n");
		for (FloatPoint location : sufsTheoreticalLocations) {
			buf.append(location + ", ");
		}
		buf.append("\n");

		
		
		return buf.toString();
	}

	
	
}
