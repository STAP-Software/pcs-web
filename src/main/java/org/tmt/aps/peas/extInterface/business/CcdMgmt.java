/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extInterface.model.GainImpl;
import org.tmt.aps.peas.extinf.Gain;
import org.tmt.aps.peas.instrument.business.PhysicalModel;

/**
 * EJB Session bean for the PCS CCD command interface. This EJB is the single entry point to the PCS CCD interface called from executors and
 * diagnostic user interfaces. All calls are delegated to the {@link ExtInfFactory} which will delegate to either the actual RPC client
 * interface or a simulator.
 * 
 * @author smichaels
 */
@Stateless
public class CcdMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ExtInfFactory extInfFactory;
	@EJB
	PhysicalModel physicalModel;

	// All Camera Commands should be defined here

	public int[][] getImage(double t) throws Exception {

		short[][] result =  extInfFactory.getCcdCommand().getImage(t);
		int[][] intResult = new int[result.length][result[0].length];
		for (int i=0; i<result.length; i++) {
			for (int j=0; j<result[0].length; j++) {
				intResult[i][j] = result[i][j];
			}
		}
		return intResult;
	}

	public int[][] getOverscannedImage(double t) throws Exception {
		
		short[][] result =   extInfFactory.getCcdCommand().getOverscannedImage(t);
		int[][] intResult = new int[result.length][result[0].length];
		for (int i=0; i<result.length; i++) {
			for (int j=0; j<result[0].length; j++) {
				intResult[i][j] = result[i][j];
			}
		}
		return intResult;
	}

	public int[] getImageSize() throws Exception {
		return extInfFactory.getCcdCommand().getImageSize();
	}

	public int[] getOverscannedImageSize() throws Exception {
		return extInfFactory.getCcdCommand().getOverscannedImageSize();
	}

	public Gain getGain() throws Exception {
		return extInfFactory.getCcdCommand().getGain();
	}

	public void setGain(int gainNumber) throws Exception {
		
		Gain gain = new GainImpl(gainNumber);
		extInfFactory.getCcdCommand().setGain(gain);
	}

	public int[] triggerOffsetCalibration() throws Exception {
		return extInfFactory.getCcdCommand().triggerOffsetCalibration();
	}

	public int getOffset(int channel) throws Exception {
		return extInfFactory.getCcdCommand().getOffset(channel);
	}

	public int[] getOffset() throws Exception {
		return extInfFactory.getCcdCommand().getOffset();
	}

	public void setOffset(int channel, int offset) throws Exception {
		extInfFactory.getCcdCommand().setOffset(channel, offset);
	}

	public void setOffset(int[] offset) throws Exception {
		extInfFactory.getCcdCommand().setOffset(offset);
	}

}
