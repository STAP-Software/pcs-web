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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CameraPoller;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CenterTelescopeProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
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
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private CameraPoller cameraPoller;
	
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

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			
			CenterTelescopeProcedureOutput procedureOutput = (CenterTelescopeProcedureOutput)procedure.getProcedureOutput();
			
			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			procedureExecutionState.setPercentComplete(5);
			
			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());

			//cameraPoller.setDoPoll(false);
			//Thread.sleep(5000);
			//cameraMgmt.resetCamera();
			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {
			
				// always command the coarse mirror to setup values at the start of all procedures
				statusLogger.log("camera.cmd.coarse_mirror", procedure.getProcedureConfigSet().getGlobalConfig().getCoarseMirrorDefault());
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getProcedureConfigSet().getGlobalConfig().getCoarseMirrorDefault());
				
				Future<Integer> twoPosCommandFuture = null;
				Future<Integer> refBeamFuture = null;
				// command to mask selected
				statusLogger.log("camera.cmd.pupil_wheel", procedureConfig.getPupilMask().getWheelPosition());
				Future<Integer> pupilMaskCommandFuture = cameraMgmt.commandPupilMask(procedureConfig.getPupilMask().getWheelPosition());
				// command to filter selected
				statusLogger.log("camera.cmd.filter_wheel", procedureConfig.getFilter().getWheelPosition());
				Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());

		        if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
		        	// select ref beam based on filter wavelength
		        	//ReferenceBeam refBeam = physicalModel.getInstrument().getCamera().getReferenceBeamByWavelength(procedureConfig.getFilter().getWavelength());
		        	
		        	// use ref beam selected in advanced options
		        	ReferenceBeam refBeam = procedureConfig.getReferenceBeam();
		        	
					statusLogger.log("camera.cmd.ref_beam", refBeam.getRefBeamNum());
		        	refBeamFuture = cameraMgmt.commandReferenceBeamState(refBeam.getRefBeamNum()); 

					// extend two pos mirror
		        	statusLogger.log("camera.cmd.two_pos_device", "extend");
		        	twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.EXTENDED);
		        } else {
		        	// turn off reference beams
					statusLogger.log("camera.cmd.ref_beam", 0);
		        	refBeamFuture = cameraMgmt.commandReferenceBeamState(0); 

					// retract two pos mirror
		        	statusLogger.log("camera.cmd.two_pos_device", "retract");
		        	twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.RETRACTED);
		        }
				
				procedureExecutionState.setPercentComplete(10);
			
				// wait for all commands to complete
		        Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture, coarseMirrorCommandFuture);
	        	statusLogger.log("camera.cmd.complete");

			} 
			
			procedureExecutionState.setPercentComplete(20);
			
			statusLogger.log("frame.get");

			ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(), 
					procedure.getProcedureNumber(), 
					0, 0, procedureConfig.getIntegrationTime(), 
					physicalModel.getInstrument().getCcd().getAllHotPixelRects(), 
					procedure.getProcedureConfigSet().getGlobalConfig().isRemoveBadPixels());
			
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
			
			//cameraPoller.setDoPoll(true);

			procedureExecutionState.setPercentComplete(40);
			
			// this is where we display the frame; tell the async controller to update the frame
			// put up some display that tells user to click on the star
			frameDisplayMgmt.displayFrame(MessageGenerator.generateMessage("instructions.ct"));
						
			frameDisplayMgmt.setPendingMarkAction(true);
			// wait for user to mark frame
			statusLogger.log("frame.mark_waiting");
			while (frameDisplayMgmt.getPendingMarkAction()) {
				Thread.sleep(500);
			}

			procedureExecutionState.setPercentComplete(60);
			
			// get marking data from the frame display
			FloatPoint guess = frameDisplayMgmt.getMarkList().get(0);
			procedureOutput.setCentroidGuess(guess);
			statusLogger.log("frame.mark_guess", guess);
			
			// call find cent with the guess
			Subimage subimage = computationLibrary.findCent(ccdFrame.getCorrectedFrame(), guess, procedure.getProcedureConfigSet().getFindCentConfig(), Constants.SPOT_TYPE_INTERIOR);
			FloatPoint centroid = subimage.getCentroid();
			procedureOutput.setCentroid(centroid);
			
			procedureExecutionState.setPercentComplete(80);
			
			// display with recalculated centroid
			frameDisplayMgmt.setMarking(centroid);
			frameDisplayMgmt.displayMarkedFrame();
			statusLogger.log("frame.mark_centroid", centroid);
			PupilMask mask = procedureConfig.getPupilMask();
			logger.debug("mask = " + mask);
						
			// get Az, El deltas
			FloatPoint desiredPixLocation = new FloatPoint(ccdFrame.getAxes1()/2.0f, ccdFrame.getAxes2()/2.0f);
			FloatPoint deltaAzEl = computationLibrary.pixLocationToDeltaArcSeconds(centroid, desiredPixLocation, mask.getSecPerPixel());
			procedureOutput.setDeltaAzEl(deltaAzEl);
			
			procedureExecutionState.setPercentComplete(90);
			
			// display result and ask if we should move telescope
			statusLogger.log("telescope.desired_move", deltaAzEl);
			
			String text = MessageGenerator.generateMessage("telescope.desired_move", deltaAzEl);
			boolean cmdTelescope = userPromptMgmt.displayYesNoDialog(text + "\nCommand Telescope?");
			procedureOutput.setCmdTelescope(cmdTelescope);
			
			// depending on what user answers, either command telescope or quit
			if (cmdTelescope) {
				statusLogger.log("telescope.cmd.start");
				dcsMgmt.commandTelescopeDeltas(deltaAzEl.asDoubleArray());
				statusLogger.log("telescope.cmd.end");
			}
			
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
		        Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete");
			}

			procedureExecutionState.setPercentComplete(95);
			
			statusLogger.log("procedure.success",  procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		/*
		 * getProcStats();
		 */

		//cameraPoller.setDoPoll(true);
		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);

	}


	
}
