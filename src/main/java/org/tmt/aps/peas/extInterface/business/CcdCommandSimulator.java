package org.tmt.aps.peas.extInterface.business;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.extInterface.model.GainImpl;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.Gain;
import org.tmt.aps.peas.extinf.TimeoutException;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.CcdGain;

/**
 * PCS CCD command simulator.  Generates dummy values for queries.
 * @author smichaels
 */
public class CcdCommandSimulator implements CcdCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	private Ccd ccd;
	private int[] gainOffset;
	private int imageHeight;
	private int imageWidth;
	private int overscanWidth;
	private int overscanHeight;
	private int gainNumber;
	private int[] offsetCalibration;
	private GainImpl gain;
	
	
	public CcdCommandSimulator(Ccd ccd, int imageHeight, int imageWidth, int overscanWidth, int overscanHeight, int gainNumber, 
			int[] offsetCalibration, int[] gainOffset) {
		this.ccd = ccd;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.overscanWidth = overscanWidth;
		this.overscanHeight = overscanHeight;
		this.gainNumber = gainNumber;
		this.offsetCalibration = offsetCalibration;
		this.gainOffset = gainOffset;
	}

	public short[][] getImage(double t) throws IllegalArgumentException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "getImage::SIMULATOR"));

		int[] imageSize = getImageSize();
		int imageWidth = imageSize[0];
		int imageHeight = imageSize[1];
		
		short[][] frame = new short[imageWidth][imageHeight];
		for (int i=0; i<imageWidth; i++) {
			for (int j=0; j<imageHeight; j++) {
				
				frame[i][j] = (short)(j + i);
			}
		}
		logger.info(MessageGenerator.generateMessage("command.success", "getImage::SIMULATOR"));
		return frame;

	}

	public short[][] getOverscannedImage(double t) throws IllegalArgumentException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "getOverscannedImage::SIMULATOR"));

		int[] imageSize = getOverscannedImageSize();
		int imageWidth = imageSize[0];
		int imageHeight = imageSize[1];
		
		short[][] frame = new short[imageWidth][imageHeight];
		for (int i=0; i<imageWidth; i++) {
			for (int j=0; j<imageHeight; j++) {
				
				frame[i][j] = (short)(j + i);
			}
		}
		logger.info(MessageGenerator.generateMessage("command.success", "getOverscannedImage::SIMULATOR"));
		return frame;
	}


	public int[] getImageSize() {
		
		int[] result = {imageWidth, imageHeight};
		
		return result;
	}


	public int[] getOverscannedImageSize() {
		
		int[] result = {overscanWidth, overscanHeight};
		
		return result;
	}

	public Gain getGain() throws CommandFailureException {
		
		
		CcdGain ccdGain = ccd.getCcdGainList().get(gainNumber);
		
		gain = new GainImpl(ccdGain.getGainNumber());
		gain.setElectronsPerAdu(ccdGain.getGainValue());
		
		
		return gain;
	}

	public void setGain(Gain newGain) throws IllegalArgumentException, CommandFailureException {
		
		gainNumber = newGain.getProgramNumber();
		
		gain = new GainImpl(newGain.getProgramNumber());
		gain.setElectronsPerAdu(newGain.getElectronsPerAdu());
		
	}

	public int[] triggerOffsetCalibration() throws TimeoutException, CommandFailureException {

		return offsetCalibration;
	}

	public int getOffset(int channel) throws IllegalArgumentException, CommandFailureException {
		return gainOffset[channel];
	}


	public int[] getOffset() throws CommandFailureException {		
		return gainOffset;
	}

	public void setOffset(int channel, int offset) throws IllegalArgumentException, CommandFailureException {
		gainOffset[channel] = offset;
		
	}

	public void setOffset(int[] offset) throws IllegalArgumentException, CommandFailureException {
		gainOffset = offset;
	}
	
	
	

}
