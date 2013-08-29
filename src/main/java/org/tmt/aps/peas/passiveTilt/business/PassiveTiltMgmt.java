/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.passiveTilt.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;

@Stateless
public class PassiveTiltMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private PassiveTiltExecutor passiveTiltExecutor;
	
	// not sure if this is even necessary
	
	public void executeProcedure(Procedure procedure, Session session) {
		logger.debug("doExecuteProcedure::about to call passiveTiltMgmt");
		passiveTiltExecutor.executeProcedure(procedure, session);
	}
}
