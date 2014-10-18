/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
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
	private FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private ProcedureExecutionState procedureExecutionState;
	@EJB
	private ProcedureOutputMgmt procedureOutputMgmt;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private GlobalConfigMgmt globalConfigMgmt;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;

	public void performProcedureStartup(Procedure procedure) {

		procedure.setExecutionStartTime(new Date());
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_EXECUTING);

		procedureExecutionState.setExecutionStatus(true);
		procedureExecutionState.setPercentComplete(0);

		statusLogger.initLog();

		frameDisplayMgmt.init();

	}

	public void handleProcedureException(Procedure procedure, Throwable exception) {

		Throwable procedureException = exception;

		try {
			throw exception;

		} catch (UnsatisfiedLinkError e) {
			procedureException = new Exception("Fortran libraries not accessible due to hot deployment.  To fix, restart JBoss.");

		} catch (EJBTransactionRolledbackException e) {

			if (e.getCause() instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException e1 = (UndeclaredThrowableException) e.getCausedByException();
				Throwable e2 = e1.getCause();
				procedureException = e2;
			}

		} catch (Throwable e) {
			procedureException = e;
		}

		exception.printStackTrace();
		statusLogger.log("procedure.exception", procedureException.getMessage());

		procedure.setProcedureState(Procedure.PROCEDURE_STATE_ABORTED);
		procedureExecutionState.setExecutionStatus(false);
		procedureExecutionState.setProcedureException(procedureException);

	}

	public void performProcedureCompletion(Procedure procedure, Session currentSession) {

		try {
			procedure.setExecutionEndTime(new Date());
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_COMPLETED);

			// this persists the procedure
			sessionMgmt.updateCurrentSession(currentSession);

			// save the current coarse mirror state in global config
			Point coarsePosition = physicalModel.getInstrument().getCamera().getCoarseTiltMirror().getCurrentPosition();
			procedure.getGlobalConfig().setCoarseMirrorX(coarsePosition.x);
			procedure.getGlobalConfig().setCoarseMirrorY(coarsePosition.y);
			globalConfigMgmt.saveDefaultConfig(procedure.getGlobalConfig());

			// persist all the frames
			if (procedure.getProcedureCcdFrameList() != null) {
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

					// save the associated centroid map
					if (procedureCcdFrame.getCentroidMap() != null) {
						centroidMapMgmt.saveCentroidMap(procedureCcdFrame.getCentroidMap());
					}
				}
			}

			statusLogger.saveLog(procedure.getProcedureId());

			// associate ref beam map
			if (procedure.getRefBeamMap() != null) {
				centroidMapMgmt.associateRefBeamMap(procedure.getRefBeamMap(), procedure);
			}
			
			// persist the procedure output
			procedureOutputMgmt.createProcedureOutput(procedure.getProcedureOutput(), procedure.getProcedureId());
			// set up for immediate viewing
			procedure.setProcedureOutput(procedureOutputMgmt.findProcedureOutput(procedure.getProcedureId()));

			procedureExecutionState.setExecutionStatus(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
