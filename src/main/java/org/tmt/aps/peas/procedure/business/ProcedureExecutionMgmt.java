/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Stateless
public class ProcedureExecutionMgmt {

	Logger logger = Logger.getLogger(this.getClass());

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

				
				// load up png file again because associateCcdFrame reloads ccd frame fresh
				// FIXME: we should not have to do this.
				String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

				CcdFrame loadedFitsFile = null;

				logger.debug("filename = " + filename);
				try {

					loadedFitsFile = frameMgmt.loadFitsFrame(filename);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// if a png file for display exists, read it in. Otherwise create it.
				byte[] falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
				procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

			}

			statusLogger.saveLog(procedure.getProcedureId());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
