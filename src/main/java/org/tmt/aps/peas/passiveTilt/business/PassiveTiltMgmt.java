package org.tmt.aps.peas.passiveTilt.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;

@Stateless
public class PassiveTiltMgmt {

	@EJB
	private PassiveTiltExecutor passiveTiltExecutor;
	
	// not sure if this is even necessary
	
	public void executeProcedure(Procedure procedure, Session session) {
		System.out.println("doExecuteProcedure::about to call passiveTiltMgmt");
		passiveTiltExecutor.executeProcedure(procedure, session);
	}
}
