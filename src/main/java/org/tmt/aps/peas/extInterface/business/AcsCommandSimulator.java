package org.tmt.aps.peas.extInterface.business;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class AcsCommandSimulator implements AcsCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	public AcsCommandSimulator() {

	}


	public void setActuDeltas(double p[]) 
			throws CommunicationException, TimeoutException, 
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "setActuDeltas::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "setActuDeltas::SIMULATOR"));

	}

	public double getMirrTemp() 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "getMirrTemp::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "getMirrTemp::SIMULATOR"));
		return 0.2;
	}

	public boolean isRunning() 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "isRunning::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "isRunning::SIMULATOR"));
		return true;
	}
	
	public double getRMSActuMove() 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "getRMSActuMove::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "getRMSActuMove::SIMULATOR"));
		return 2.2;
	}

	public int takeSnap() 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "takeSnap::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "takeSnap::SIMULATOR"));
		return 42;
	}
	
	public void loadSnap(int snap) 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "loadSnap::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "loadSnap::SIMULATOR"));
		
	}


}
