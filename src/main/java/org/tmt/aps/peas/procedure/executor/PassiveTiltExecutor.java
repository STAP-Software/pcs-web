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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
@Startup
public class PassiveTiltExecutor {

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
	@EJB
	PhysicalModel physicalModel;

	private List<String> logMessages;
	
	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("PassiveTiltMgmt::PostConstruct::");
	}
	
	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PassiveTiltMgmt::testMethod::");
		return null;
	}
	
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PassiveTiltExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			
			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			procedureExecutionMgmt.performProcedureStartup(procedure);
			
			
			// TODO: frame simulation mode sets iterations = 1 (why?) - this should also be part of form validation

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init"); 

			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// TODO: implement
				// autoPointTelescope();

				// TODO: implement
				// autoRefmapCheck();
				
				// always command the coarse mirror to setup values at the start of all procedures
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getGlobalConfig().getCoarseMirrorDefault());

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
		        	ReferenceBeam refBeam = physicalModel.getInstrument().getCamera().getReferenceBeamByWavelength(procedureConfig.getFilter().getWavelength());
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
		        	statusLogger.log("camera.cmd.two_pos_device", "extend");
		        	twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.RETRACTED);
		        }
			
				// wait for all commands to complete
		        Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture, coarseMirrorCommandFuture);
	        	statusLogger.log("camera.cmd.complete");
				
			}
	
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());
		
			
			// TODO: zero totals centroid array (for summing centroids for all trials)
			
			for (int i = 0; i < procedureConfig.getNumberOfTrials(); i++) {

				try {
				
				ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(), procedure.getProcedureNumber(), 
					0, 0, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd().getAllHotPixelRects(), procedure.getGlobalConfig().isRemoveBadPixels());
				CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
				
				// tell the async controller to update the frame
				frameDisplayMgmt.displayFrame();
				
				statusLogger.log("fandi.start");
				List<Point> subimageList = null;
				
				int numSpots = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots();
				FloatPoint[] centroids;
				
				FIConfig fiConfig = procedure.getProcedureConfigSet().getFiConfig();
				FIResult fiResult = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots, fiConfig, null, procedure.getRefBeamMap());
				
				// display the marked frame
				frameDisplayMgmt.displayMarkedFrame();

				// TODO: what is this?
				statusLogger.log("fandi.search_count", 1);
				statusLogger.log("fandi.frame_scale", 0.9530617);
				statusLogger.log("fandi.rotation", 0.3370904);
				statusLogger.log("fandi.frame_scale", 0.9840439);
				statusLogger.log("fandi.center_calc");
				
				statusLogger.log("fandi.end.success");

				graphicDisplayMgmt.displaySubimageCentroids(subimageList);

				statusLogger.log("calc.centroid_resid");
			
				// TODO: centroid offsets throws an exception if telescope pointing out of tolerance
				List<Point> referenceSubimageList = null;
				
				List<Point> centroidOffsets = imageProcessor.calculateCentroidOffsets(subimageList, referenceSubimageList);

				// TODO: we need to save the centroid offsets, image rotation, translation and scale for each trial for later calculation

				graphicDisplayMgmt.displayCentroidOffsets(centroidOffsets);
				
				statusLogger.log("calc.rigid_body_rot", 0.284E-03);
				
				statusLogger.log("telescope.desired_move", 0.04, 0.14);
				
				int trialPct = (int) ((((i+1)*100)/procedureConfig.getNumberOfTrials()) * 0.95);
				
				procedureExecutionState.setPercentComplete(trialPct);
				
				} catch (Exception e) {
					
					// TODO: ask user if they want to re-take the frame
					boolean reply = userPromptMgmt.displayYesNoDialog("Can you see this text?");
					
					if (reply) {
						// go back and re-take frame
						i--;
						continue;
					}
				}
			}

			// TODO: calc average offsets and calc avg translation, rotation and scale from average offsets
			
			imageProcessor.calculateCentroidStats();

			// TODO: we should persist image rotation, scale, rrmsTotal, focusError, enclosedEnergy and enclosed50Energy
			// TODO: and also: ZPROCLOG_DATA_ACS_FOCUS = ZPROCLOG_DATA_PRIMARY_ACT_FM_RMS/41.1
			// TODO: and these: procedure number and all frame heading records
			
			// TODO: also whatever this does
			// CALL GET_PROC_STATS(ZPASSIVE_FRAME_SOURCE)
			
			// TODO: display the average centroid offsets
			//graphicDisplayMgmt.displayCentroidOffsets(????);
			
			// TODO: implement actuator lengths
			// computationLibrary.actuatorLengths(a, b);
			
			// TODO: somewhere here we need to command the mirrors
			
	        if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
	        	// turn off reference beams
	        	// TODO: make a Future and wait.
	        	// TODO: futures should have a command 'wait' function that we apply to a set of futures
	        	cameraMgmt.commandReferenceBeamState(0);
	        }	
	        
	        // TODO: wait for futures
			
			statusLogger.log("procedure.end",  procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setExecutionStatus(false);
			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		/*
		 * getProcStats();
		 */
		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

	/*

// Let's be nice and turn off the lights

    IF (GET_SYMBOL('STUB_DEMO')) OK = CAMERA_COMMAND( 'RBF')
C
C Put centroid offsets into its own array again
C

	 * 
	 * 
	 */
	
	

	
	
	private void wait(int ms) {
		// here we wait until the pending display is cleared
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {

		}

	}

}
