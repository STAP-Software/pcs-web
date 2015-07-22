/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.Arrays;
import java.util.Date;
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
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.model.PupilRegistrationIterationOutput;
import org.tmt.aps.peas.procedure.model.PupilRegistrationProcedureOutput;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class PupilRegistrationExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private AcsMgmt acsMgmt;
	@EJB
	private DcsMgmt dcsMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private ImageProcessor imageProcessor;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
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
	//@EJB
	//private PupilRegistrator pupilRegistrator;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private CreateRefMapExecutor createRefMapExecutor;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("PupilRegistrationExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PupilRegistrationExecutor::testMethod::");
		return null;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PupilRegistrationExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PupilRegistrationProcedureOutput procedureOutput = (PupilRegistrationProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");


			/** 
			 * TODO: this check would go in the procedure controller prior to calling
			 * 
			 * so if you choose a mask, then you get a default filter only changeable in advanced options
			 * 
			 * this goes away:
			 * 
		       IF ((FILT_POS.NE.FILT_POS_611).AND.(MASK_POS.NE.POS_160_12).AND.
		    		     +       (MASK_POS.NE.POS_UPH)) THEN
		    		           IF(.NOT.FYNERROR_DIALOG(
		    		     +       'Not using recommended 611 nm filter',
		    		     +       LEN('Not using recommended 611 nm filter'),
		    		     +       'Continue', LEN('Continue'),
		    		     +		 'Abort Test', LEN('Abort Test'), NO, M_DIALOG)) GOTO 900
		    		        ENDIF

			 */
			
			
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (currentRefMap == null) {

				CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
				Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
						ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession.getSessionId(), 
						procedure.getTestNumber(), po);

				procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

				procedureExecutionState.setPendingSubProcedure(subProcedure);

				// execute the subprocedure
				createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

				currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
						.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
						.getFilterTypeId(), -1);

			}
			
			
			procedure.setRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// always command the coarse mirror to setup values at the start of all procedures
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getProcedureConfigSet()
						.getGlobalConfig().getCoarseMirrorDefault());
				
				// TODO: implement
				//Future<Point> fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(procedure.getProcedureConfigSet()
				//		.getGlobalConfig().getFineMirrorDefault());

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
					statusLogger.log("camera.cmd.two_pos_device", "extend");
					twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.RETRACTED);
				}

				// wait for all commands to complete
				// TODO: complete this
				//Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture,
				//		coarseMirrorCommandFuture, fineMirrorCommandFuture);
				statusLogger.log("camera.cmd.complete");

			}

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
			ProcedureCcdFrame procedureCcdFrame = null;
			CentroidOffsetsResult centroidOffsetsResult = null;

			FloatPoint lastMove = null;
			FloatPoint deltaAzEl;
			
			while (true) {

				logger.debug("light source 3 = " + procedureConfig.getLightSource());

				procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);

				statusLogger.log("calc.centroid_resid");

				/*****************************************************/
				/*             calculateCentroidOffsets              */
				/*****************************************************/

				
				centroidOffsetsResult = computationLibrary.calculateCentroidOffsets(procedureCcdFrame.getCentroidMap().getFindCentroidsResult().getCentroidList(),
						procedure.getRefBeamMap().getCentroidMap().getFindCentroidsResult().getCentroidList(), procedure.getProcedureConfigSet()
								.getCentroidOffsetsConfig(), procedureConfig.getPupilMaskType());

				// go from centroidOffsetsResult.imageTranslation to deltaAz,El
				deltaAzEl = computationLibrary.pixLocationToDeltaArcSeconds(centroidOffsetsResult.getImageTranslation(), 
						new FloatPoint(0,0), procedureConfig.getPupilMask().getSecPerPixel());

				
				// test deltaAzEl against thresholds for telescope move
				AutoCenterTelConfig autoCenterTelConfig = procedure.getProcedureConfigSet().getAutoCenterTelConfig();
				AutoCenterTelCheckResult aResult = computationLibrary.autoCenterTelescopeCheck(autoCenterTelConfig, deltaAzEl, lastMove);
				// log what result was found
				statusLogger.log(aResult.getReasonKey(), aResult.getReasonArgs());

				if (aResult.getRecenterTelescope().isNo() || procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_NO) {
					break; // leave the loop if nothing to do
				}

				if (aResult.getRecenterTelescope().isYes() && procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_YES) {

					// perform telescope move
					lastMove = deltaAzEl;
					statusLogger.log("telescope.cmd.start");
					dcsMgmt.commandTelescopeDeltas(deltaAzEl.asDoubleArray());
					statusLogger.log("telescope.cmd.end");
				}

				
				
				// prompt user if required by settings or required due to abnormal result
				boolean userReply = false;
				if (aResult.getRecenterTelescope().isPrompt()) {
					
					// ask user if they want to center the telescope
					userReply = userPromptMgmt.displayYesNoDialog(MessageGenerator.generateMessage(aResult.getReasonKey(),
							aResult.getReasonArgs()) + "\nMove Telescope?");
					
					if (userReply) {
						// perform telescope move
						lastMove = deltaAzEl;
						statusLogger.log("telescope.cmd.start");
						dcsMgmt.commandTelescopeDeltas(deltaAzEl.asDoubleArray());
						statusLogger.log("telescope.cmd.end");						
					} else {
						break;
					}
					
				} else if (procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_PROMPT) {
					// ask user if they want to center the telescope
					userReply = userPromptMgmt.displayYesNoDialog(MessageGenerator.generateMessage(aResult.getReasonKey(),
							aResult.getReasonArgs()) + "\nMove Telescope?");
					
					if (userReply) {
						// perform telescope move
						lastMove = deltaAzEl;
						statusLogger.log("telescope.cmd.start");
						dcsMgmt.commandTelescopeDeltas(deltaAzEl.asDoubleArray());
						statusLogger.log("telescope.cmd.end");						
					} else {
						break; // if user doesn't want to move telescope, no point in re-taking frame
					}
					
				}

				if (aResult.getRetakeFrame().isNo()) {
					break;
				}

				if (aResult.getRetakeFrame().isPrompt()) {

					// ask user if they want to re-take the frame
					int reply = userPromptMgmt.displayFlowControlTriFlowDialog("Frame needs to be retaken.  Press: 'Retry' to re-take frame, 'Continue' to continue procedure with this frame, 'Abort' to abort test now.");
				
					if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
						
						// TODO: put in logic here (throw user abort exception?
						
					} else if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
						break; // continue on
					}
				}
	
				// go back and re-take frame

			}
			
			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets());

			/*****************************************************/
			/*              passiveTiltScaleError                */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
			
			ScaleError scaleError = computationLibrary.passiveTiltScaleError(centroidOffsetsResult.getCcdCentroidOffsets(),
					centerSpots);


			procedureExecutionState.setPercentComplete(80);

			// TODO: should auto center telescope calls be extracted to its own sub-procedure??
			
			/*****************************************************/
			/*            calcPupilRegErrorDefaults              */
			/*****************************************************/
			
			// TODO: calcPupilRegErrorDefaults needs to be defined in DB
			// fractionalIntensityCalcMethod (mask dependent)
			// nStart - subimage number to start on
			// 
			// peripheralDiam == spotDiam - in pupilMask table
			// segmentSideLen should be in the constants tables aHexM
			// 'constantsDefiningGeometryOfSpots' should be added to constants tables
			
			// PupilRegErrorResult result = computationLibrary.calculatePupilRegError(calcPupilRegErrorDefaults, centroidMap??, mask.numSubimgagesTotal, constantsDefiningGeometyOfSpots, segmentSideLen , pupilMask.spotDiam);
			
			// PupilRefErrorResult:
			// x registration error (m)
			// y registration error (m)
			// phi rotation error (r)
			// x registration error using approx calc
			// y registration error using approx calc
			// phi registration error using approx calc
			// scale error

			
			/*****************************************************/
			/*           determine fine/coarse PR Commands       */
			/*****************************************************/
			
			// TODO: logic that given automode preferences and (potentially) user input to determine if commands are to be sent, what the commands are and which mechanisms to move.
			// this will use calcuations:
			// calcFineTiltPRCommands()
			// calcCoarseTiltPRCommands()
			// and may require 'threshold' values: 
			// 1. general meters on sky = which mechanism to move
			// 2. fine tilt absolute position limit/threshold to determine if offload is required.
			
			
			// prompt user with registration error values (if automode allows)
			
			
			// DeterminePrCommandsResult prCommandResult = determinePrCommands();
			
			
			/*****************************************************/
			/*         move fine, coarse, both, or none          */
			/*****************************************************/
			
			/*
			if (prCommandResult.isCoarseCommands()) {
				// always command the coarse mirror to setup values at the start of all procedures
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(<some Point value>);
			} . . .
			Future<Point> fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(<some Point value>);

			Utils.waitForComplete(coarseMirrorCommandFuture, fineMirrorCommandFuture);
			*/
			
			
			// fill the iteration output: TODO all pupilRegErrorResultFields
			// TODO: what was moved and how much
			PupilRegistrationIterationOutput pio = new PupilRegistrationIterationOutput();
			procedureOutput.addIteration(pio);

			pio.setIteration(0);
			pio.setDeltaAzEl(deltaAzEl);

			pio.setCcdCentroidOffsets(centroidOffsetsResult.getCcdCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setCartesianCentroidOffsets(centroidOffsetsResult.getCartesianCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setScaleError(scaleError.getScaleError());

			pio.setMaxSpotNum(centroidStatsResult.getMaxSpotNum());
			pio.setMaxOffset(centroidStatsResult.getMaxOffset());
			pio.setRmsOffset(centroidStatsResult.getRmsOffset());

			pio.setEnclosedEnergy50(centroidStatsResult.getEnclosedEnergy50());
			pio.setEnclosedEnergy80(centroidStatsResult.getEnclosedEnergy80());

			pio.setScaleError(scaleError.getScaleError());
			pio.setSlopeError(scaleError.getSlopeError());

			pio.setTelescopeMoved(false);

			// fill the output - many of these are copied from the one iteration
			procedureOutput.setCcdCentroidOffsets(pio.getCcdCentroidOffsets());
			procedureOutput.setCartesianCentroidOffsets(pio.getCartesianCentroidOffsets());

			procedureOutput.setScaleError(pio.getScaleError());

			procedureOutput.setMaxSpotNum(pio.getMaxSpotNum());
			procedureOutput.setMaxOffset(pio.getMaxOffset());
			procedureOutput.setRmsOffset(pio.getRmsOffset());

			procedureOutput.setEnclosedEnergy50(pio.getEnclosedEnergy50());
			procedureOutput.setEnclosedEnergy80(pio.getEnclosedEnergy80());

			procedureOutput.setScaleError(pio.getScaleError());
			procedureOutput.setSlopeError(pio.getSlopeError());

			procedureOutput.setRotationFromRefBeam(centroidOffsetsResult.getImageRotation());
			procedureOutput.setScaleChangeFromRefBeam(centroidOffsetsResult.getImageScale());
			procedureOutput.setTranslationFromRefBeam(centroidOffsetsResult.getImageTranslation());

			
			
			// VERIFY: probably dont need this?
			graphicDisplayMgmt.displayCentroidOffsets(procedureOutput);


			// VERIFY: what do we display and/or command here...


			
			
			
			
			
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);


			
		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		/*
		 * getProcStats();
		 */

		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
