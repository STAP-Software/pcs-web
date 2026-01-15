package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

/**
 * ACS command simulator.  Generates dummy values for queries.
 * @author smichaels
 */
public class AcsCommandSimulator implements AcsCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	public AcsCommandSimulator() {

	}


	public void setActuDeltas(double p[]) 
			throws CommunicationException, TimeoutException, 
			CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "setActuDeltas::SIMULATOR"));
		
		Utils.waitFor(1100);
		
		logger.info(MessageGenerator.generateMessage("command.success", "setActuDeltas::SIMULATOR"));

		//throw new TimeoutException(50, "ACS Timed OUT");
	}

	public double getMirrTemp() 
			throws CommunicationException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "getMirrTemp::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "getMirrTemp::SIMULATOR"));
		return -12.20402050607;
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
		return 2.204050607;
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


	@Override
	public double getSensorRange() throws CommunicationException, CommandFailureException, RemoteException {
		logger.info(MessageGenerator.generateMessage("command.start", "getSensorRange::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "getSensorRange::SIMULATOR"));
		return 999.0;
	}


}
