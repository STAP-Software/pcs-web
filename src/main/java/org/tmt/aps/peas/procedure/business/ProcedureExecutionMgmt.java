package org.tmt.aps.peas.procedure.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Stateless
public class ProcedureExecutionMgmt {

	@EJB
	private SessionMgmt sessionMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private ProcedureExecutionState procedureExecutionState;

	public void performProcedureStartup(Procedure procedure) {

		procedure.setExecutionStartTime(new Date());
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_EXECUTING);

		procedureExecutionState.setExecutionStatus(true);
		procedureExecutionState.setPercentComplete(0);

		statusLogger.initLog();

	}

	public void handleProcedureException(Procedure procedure) {

		procedure.setProcedureState(Procedure.PROCEDURE_STATE_ABORTED);

	}

	public void performProcedureCompletion(Procedure procedure, Session currentSession) {

		try {
			procedure.setExecutionEndTime(new Date());
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_COMPLETED);

			// this persists the procedure
			sessionMgmt.updateCurrentSession(currentSession);

			// persist all the frames
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
				procedureCcdFrame.setProcedure(procedure); // need the assigned procedure id
				frameMgmt.associateCcdFrame(procedureCcdFrame);
			}

			statusLogger.saveLog(procedure.getProcedureId());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
