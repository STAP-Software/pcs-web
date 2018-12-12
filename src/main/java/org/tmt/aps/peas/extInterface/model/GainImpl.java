package org.tmt.aps.peas.extInterface.model;

import org.tmt.aps.peas.extinf.Gain;

public class GainImpl extends Gain {

	double electronsPerAdu;
	
	public GainImpl(int gain) {
		super(gain);
	}
	
	@Override
	public double getElectronsPerAdu() {
		// TODO Auto-generated method stub
		return electronsPerAdu;
	}
	
	public void setElectronsPerAdu(double electronsPerAdu) {
		this.electronsPerAdu = electronsPerAdu;
	}

	@Override
	public int getGain() {
		// TODO Auto-generated method stub
		return super.getGain();
	}

}
