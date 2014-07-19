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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
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
public class CreateRefMapExecutor {

	Logger logger = Logger.getLogger(this.getClass());


	@EJB
	private CameraMgmt cameraMgmt;
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

		logger.info("CreateRefMapExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfig();
			
			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			procedureExecutionMgmt.performProcedureStartup(procedure);
			
			
			// TODO: frame simulation mode sets iterations = 1 (why?) - this should also be part of form validation

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init"); 


			wait(1);
			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// TODO: implement
				// autoPointTelescope();

				Future<Integer> pupilMaskCommandFuture = cameraMgmt.commandPupilMask(procedureConfig.getPupilMask().getWheelPosition());

				Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());

				// TODO: implement
				// autoRefmapCheck();

				Future<Point> coarseFuture = cameraMgmt.commandCoarseTiltMirror(new Point(0,0));
				Future<Point> fineFuture = cameraMgmt.commandFineTiltMirror(new Point(0,0));
				
				//cameraMgmt.readyCamera();

				//cameraMgmt.selectRefBeam(); // check if this is a command or something else

				// FIXME
				// cameraMgmt.cameraCommand("45E"); //extend two position mirror, for ref map is this retract??
				
				// wait for 
				while (!pupilMaskCommandFuture.isDone() || !filterCommandFuture.isDone()) {
					// wait and try again
					Thread.sleep(500);
				}
				while (!coarseFuture.isDone() || !fineFuture.isDone()) {
					// wait and try again
					Thread.sleep(500);
				}
			}
	
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());
		
			for (int i = 0; i < procedureConfig.getNumberOfTrials(); i++) {

				ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig.getFrameSource(), i, i);
				CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
				
				// TODO: this is where we display the frame
				// tell the async controller to update the frame
				frameDisplayMgmt.displayFrame();
				
				wait(4670);

				statusLogger.log("fandi.start");
				
				List<Point> subimageList = null;
				
				float centroids[][] = new float [36][2];
				computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), centroids);
				
				// TODO: this is where we display the marked frame
				frameDisplayMgmt.displayMarkedFrame();

				wait(2000);
				
				statusLogger.log("fandi.search_count", 1);
				statusLogger.log("fandi.frame_scale", 0.9530617);
				statusLogger.log("fandi.rotation", 0.3370904);
				statusLogger.log("fandi.frame_scale", 0.9840439);
				statusLogger.log("fandi.center_calc");
				
				statusLogger.log("fandi.end.success");

				graphicDisplayMgmt.displaySubimageCentroids(subimageList);

				// TODO: if not frame_ok (global variable), then ask user if they want to retake frame
				// this construct will probably work
				if (false) {
					i--;
					continue;
				}
				// TODO:

				wait(967);
								
				int trialPct = (int) ((((i+1)*100)/procedureConfig.getNumberOfTrials()) * 0.95);
				
				procedureExecutionState.setPercentComplete(trialPct);
			}

			// FIXME: what is this really? we need to abstract this
			// cameraMgmt.cameraCommand("RBF"); //

			/*
			float a = 1.0f;
			float b = 2.2f;
			
			float c = computationLibrary.actuatorLengths(a, b);
			*/
			
			//userPromptMgmt.displayYesNoDialog("Can you see this text?");
		//	statusLogger.log("computationLibrary: a,b,c = " + a + " " + b + " " + c);
						
			wait(134);
			statusLogger.log("procedure.end",  procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setExecutionStatus(false);
			procedureExecutionState.setPercentComplete(100);

		} catch (Exception e) {
			
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
