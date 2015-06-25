package org.tmt.aps.peas.extInterface.business;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.MessageGenerator;
import org.tmt.aps.peas.extinf.TimeoutException;

public class CcdCommandSimulator implements CcdCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	public int[][] getImage() throws CommunicationException, TimeoutException, CommandFailureException {
		
		logger.info(MessageGenerator.generateMessage("command.start", "getImage::SIMULATOR"));

		int[][] frame = new int[1024][1024];
		for (int i=0; i<1024; i++) {
			for (int j=0; j<1024; j++) {
				
				frame[i][j] = j + i;
			}
		}
		logger.info(MessageGenerator.generateMessage("command.success", "getImage::SIMULATOR"));
		return frame;
	}

	@Override
	public void fastWipe() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "fastWipe::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "fastWipe::SIMULATOR"));

	}

	@Override
	public void wipeOn() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "wipeOn::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "wipeOn::SIMULATOR"));

	}

}
