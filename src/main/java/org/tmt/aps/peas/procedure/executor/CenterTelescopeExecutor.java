/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
@Startup
public class CenterTelescopeExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private DcsMgmt dcsMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private ImageProcessor imageProcessor;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private ProcedureExecutionMgmt procedureExecutionMgmt;
	@EJB
	private ProcedureExecutionState procedureExecutionState;
	@EJB
	private ComputationContext computationContext;
	@EJB
	private PupilRegistrator pupilRegistrator;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("CreateRefMapExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("CreateRefMapExecutor::testMethod::");
		return null;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("CenterTelescopeExecutor::executeProcedure::" );

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			procedureExecutionMgmt.performProcedureStartup(procedure);
					
			statusLogger.log("Entering Center Telescope Procedure ");

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {
			
				// command to mask selected
				Future<Integer> pupilMaskCommandFuture = cameraMgmt.commandPupilMask(procedureConfig.getPupilMask().getWheelPosition());
				// command to filter selected
				Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());

				// TODO: call readyCamera

				// TODO: we need a light source advanced option: star vs led - default to led for ref beam tests, star for all other ones

				// TODO: command leds if light source is led - the led chosen depends on the filter selected. This used to be filt_pos
				// but that is wrong and should be stored with each filter which led to use.
			
				// TODO: wait for all futures to complete
			
			} 
			
			statusLogger.log("Getting Corrected Frame");

			ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig.getFrameSource(), 0, 0);
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

			// this is where we display the frame; tell the async controller to update the frame
			// put up some display that tells user to click on the star
			frameDisplayMgmt.displayFrame("Please mark the spot on the frame,\nthen press Apply Marking.");
						
			frameDisplayMgmt.setPendingMarkAction(true);
			// wait for user to mark frame
			statusLogger.log("Waiting for user to mark frame");
			while (frameDisplayMgmt.getPendingMarkAction()) {
				Thread.sleep(500);
			}

			// get marking data from the frame display
			FloatPoint guess = frameDisplayMgmt.getMarkList().get(0);
			statusLogger.log("marked guess: " + guess);
			
			PupilMask mask = procedureConfig.getPupilMask();
			logger.debug("mask = " + mask);
			
			// TODO: this should be in the library
			FloatPoint deltaAzEl = CenterTelescopeCalc.centerTelescopeCalc(guess, mask.getSecPerPixel());
			
			// display result and ask if we should move telescope
			String text = "The telescope needs to be moved \n" + deltaAzEl.x + " arc sec. in AZ \n" + deltaAzEl.y + " arc sec. in EL \n";
			statusLogger.log(text);
			
			boolean cmdTelescope = userPromptMgmt.displayYesNoDialog(text + "\nCommand Telescope?");
			
			// depending on what user answers, either command telescope or quit
			if (cmdTelescope) {
				statusLogger.log("Commanding telescope move");
				dcsMgmt.commandTelescopeDeltas(deltaAzEl.asDoubleArray());
				statusLogger.log("Telescope move completed");
			}
			

			int trialPct = (int) ((((0) * 100) / 1) * 0.95);

			procedureExecutionState.setPercentComplete(trialPct);


			statusLogger.log("Center Telescope Procedure Completed");
			statusLogger.log("Exiting Center Telescope Procedure");

			procedureExecutionState.setExecutionStatus(false);
			procedureExecutionState.setPercentComplete(100);

		} catch (Exception e) {
			e.printStackTrace();
			procedureExecutionMgmt.handleProcedureException(procedure);

		}
		/*
		 * getProcStats();
		 */

		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

	private void wait(int ms) {
		// here we wait until the pending display is cleared
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {

		}

	}

}
