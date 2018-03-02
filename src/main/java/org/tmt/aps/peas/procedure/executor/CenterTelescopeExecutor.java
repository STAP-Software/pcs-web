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
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.StartupComputationsResult;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CameraPoller;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CenterTelescopeProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

/**
 * Executor for the Center Telescope procedure
 * @author smichaels
 */
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
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private CameraPoller cameraPoller;
	@EJB
	private ReadyCameraSubflow readyCameraSubflow;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	
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

	/**
	 * Executor method: this method is the Center Telescope flow
	 */
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("CenterTelescopeExecutor::executeProcedure::" );

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			
			CenterTelescopeProcedureOutput procedureOutput = (CenterTelescopeProcedureOutput)procedure.getProcedureOutput();
			
			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			procedureExecutionState.setPercentComplete(5);
			
			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());

			//cameraPoller.setDoPoll(false);
			//Thread.sleep(5000);
			//cameraMgmt.resetCamera();
			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCameraSubflow.execute(procedure);

			
			procedureExecutionState.setPercentComplete(20);
			
			// setup procedure output logging
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);			
			
			/***********************************************/
			/*             Startup Computations            */
			/***********************************************/
			StartupComputationsResult startupComputationsResult = computationLibrary.startupComputations(
					procedureConfig.getPupilMask().getArcsecPerMeter(),
					physicalModel.getInstrument().getCcd().getCcdType().getPixelSize());
			
			statusLogger.log("frame.get");

			ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, 
					procedure.getProcedureConfigSet().getFrameCorrectionConfig(),
					procedure.getProcedureType(), 
					procedure.getProcedureNumber(), 
					0, 0, procedureConfig.getIntegrationTime(), 
					physicalModel.getInstrument().getCcd().getAllHotPixelRects(), 
					procedureConfig.isRemoveBadPixels());
			
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
			
			statusLogger.log("frame.mark_guess", guess);
			
			// call find cent with the guess
			FindCentResult findCentResult = computationLibrary.findCent(ccdFrame.getCorrectedFrame(), guess, procedure.getProcedureConfigSet().getFindCentConfigInterior(), Constants.SPOT_TYPE_INTERIOR);
			FloatPoint centroid = findCentResult.getSubimage().getCentroid();
			
			
			procedureExecutionState.setPercentComplete(80);
			
			// display with recalculated centroid
			frameDisplayMgmt.setMarking(centroid);
			frameDisplayMgmt.displayMarkedFrame();
			statusLogger.log("frame.mark_centroid", centroid);
			PupilMask mask = procedureConfig.getPupilMask();
			logger.debug("mask = " + mask);
						
			// get most recent ref map - we want the center of the star to be coincident with the center of the ref maps
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					PupilMaskType.PUPIL_MASK_TYPE_ID_508, FilterType.FILTER_TYPE_ID_611, -1);

			FloatPoint refMapTranslation = new FloatPoint(currentRefMap.getCentroidMap().getTranslationX(), currentRefMap.getCentroidMap().getTranslationY());
			
			// get Az, El deltas.  The desired location is the center of the ref map
			FloatPoint desiredPixLocation = new FloatPoint(ccdFrame.getAxes1()/2.0f + refMapTranslation.x, ccdFrame.getAxes2()/2.0f + refMapTranslation.y);
			CenterTelescopeCalcResult centerTelescopeCalcResult = computationLibrary.centerTelescopeCalc(centroid, desiredPixLocation, startupComputationsResult.getArcsecPerPixel());
			
			procedureExecutionState.setPercentComplete(90);
			
			// display result and ask if we should move telescope
			statusLogger.log("telescope.desired_move", centerTelescopeCalcResult.getDeltaAzEl());
			
			String text = MessageGenerator.generateMessage("telescope.desired_move", centerTelescopeCalcResult.getDeltaAzEl());
			boolean telescopeMoved = userPromptMgmt.displayYesNoDialog("Command Telescope?", text + "\nCommand Telescope?");
			procedureOutput.getProcedureDecisionLog().setTelescopeMoved(telescopeMoved);
			
			// depending on what user answers, either command telescope or quit
			if (telescopeMoved) {
				statusLogger.log("telescope.cmd.start");
				dcsMgmt.commandTelescopeDeltas(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
				statusLogger.log("telescope.cmd.end");
			}
			
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
		        long waitPeriodMs = Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
			}

			// close shutter
			// FIXME: remove this call when all shutter usage is deprecated
			cameraMgmt.commandCcdShutterState(CameraCommand.CLOSED);

			procedureExecutionState.setPercentComplete(95);
			
			statusLogger.log("procedure.success",  procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		

		statusLogger.log("procedure.saving");
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);

	}


	
}
