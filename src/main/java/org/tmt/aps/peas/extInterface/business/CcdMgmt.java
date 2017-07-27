/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
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
		
		getGain(); // populates the physicalModel
		
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
		
		getGain(); // populates the physicalModel

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

	
	@Asynchronous
	public Future<Integer> setGain(int gainNumber) throws Exception {
			
		Gain gain = new GainImpl(gainNumber);
		extInfFactory.getCcdCommand().setGain(gain);
		
		physicalModel.getInstrument().getCcd().setGain(gainNumber);
	
		return new AsyncResult<Integer>(gainNumber);
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
		
		if (channel == 0) {
			physicalModel.getInstrument().getCcd().getCcdGain().setGainOffsetChannel0(offset);
		} else if (channel == 1) {
			physicalModel.getInstrument().getCcd().getCcdGain().setGainOffsetChannel1(offset);
		}
	}

	@Asynchronous
	public Future<Integer> setOffset(int[] offset) throws Exception {
		extInfFactory.getCcdCommand().setOffset(offset);
		
		physicalModel.getInstrument().getCcd().getCcdGain().setGainOffsetChannel0(offset[0]);
		physicalModel.getInstrument().getCcd().getCcdGain().setGainOffsetChannel1(offset[1]);
		
		return new AsyncResult<Integer>(1);
	}
	
	@Asynchronous
	public Future<Integer> setTemp(double temp) throws Exception {
		
		extInfFactory.getCcdCommand().setTemp(temp);
		
		physicalModel.getInstrument().getCcd().setTemperatureSetting(temp);
		
		return new AsyncResult<Integer>(1);
	}
	
	public double[] getTemperatures() throws Exception {
		return extInfFactory.getCcdCommand().getTemperatures();
	}

	public double getExposureTime() throws Exception {
		return extInfFactory.getCcdCommand().getExposureTime();
	}


}
