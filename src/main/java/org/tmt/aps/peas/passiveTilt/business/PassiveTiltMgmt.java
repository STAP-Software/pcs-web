package org.tmt.aps.peas.passiveTilt.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.tmt.aps.peas.procedure.model.Procedure;

@Stateless
public class PassiveTiltMgmt {

	@EJB
	private PassiveTiltExecutor passiveTiltExecutor;
	
	public void executeProcedure(Procedure procedure) {
		System.out.println("doExecuteProcedure::about to call passiveTiltMgmt");
		passiveTiltExecutor.executeProcedure(procedure);
	}
}
