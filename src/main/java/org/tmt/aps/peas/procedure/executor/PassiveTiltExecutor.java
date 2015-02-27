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
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltIterationOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class PassiveTiltExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private AcsMgmt acsMgmt;
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
		logger.debug("PassiveTiltExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PassiveTiltExecutor::testMethod::");
		return null;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PassiveTiltExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PassiveTiltProcedureOutput procedureOutput = (PassiveTiltProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");

			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD || currentRefMap == null) {

				boolean autoTakeRefMap = false;
				if (currentRefMap == null) {
					autoTakeRefMap = true;
				} else if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_NO) {
					autoTakeRefMap = false;
				} else {

					try {

						computationLibrary.autoRefMapCheck(procedure.getProcedureConfigSet().getAutoRefMapConfig(), physicalModel
								.getInstrument().getCamera().getCoarseTiltMirror().getCurrentPosition(), physicalModel.getInstrument()
								.getCcd().getTemperature(), 1, new Date(), currentRefMap);

					} catch (AutoRefMapCheckException e) {

						if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_PROMPT) {
							// prompt user
							autoTakeRefMap = userPromptMgmt.displayYesNoDialog(e.getMessage() + "\nTake new Ref Map?");

						} else {
							autoTakeRefMap = true;
						}
					}
				}

				if (autoTakeRefMap) {

					CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
					Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
							ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession.getSessionId(), po);

					procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

					procedureExecutionState.setPendingSubProcedure(subProcedure);

					// execute the subprocedure
					createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

					currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
							.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

				}
			}
			
			procedure.setRefBeamMap(currentRefMap);

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// always command the coarse mirror to setup values at the start of all procedures
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getProcedureConfigSet()
						.getGlobalConfig().getCoarseMirrorDefault());

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
				Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture,
						coarseMirrorCommandFuture);
				statusLogger.log("camera.cmd.complete");

			}

			

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			ProcedureCcdFrame procedureCcdFrame = null;
			CentroidOffsetsResult centroidOffsetsResult = null;

			while (true) {

				procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);

				try {

					statusLogger.log("calc.centroid_resid");

					// TODO: centroid offsets throws an exception if telescope pointing out of tolerance
					// TODO: centroid offset calc
					/*****************************************************/
					/* calculateCentroidOffsets */
					/*****************************************************/

					centroidOffsetsResult = computationLibrary.calculateCentroidOffsets(procedureCcdFrame.getCentroidMap().getValues(),
							procedure.getRefBeamMap().getCentroidMap().getValues(), procedure.getProcedureConfigSet()
									.getCentroidOffsetsConfig());

					statusLogger.log("calc.rigid_body_rot", 0.284E-03);

					statusLogger.log("telescope.desired_move", 0.04, 0.14);

					int trialPct = (int) (((100) / procedureConfig.getNumberOfTrials()) * 0.95);

					procedureExecutionState.setPercentComplete(trialPct);

					break; // leave the loop if no exception

				} catch (Exception e) {

					// ask user if they want to re-take the frame
					int reply = userPromptMgmt.displayFlowControlTriFlowDialog("" + e.getMessage());

					if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
						// TODO: put in logic here (throw user abort exception?
					} else if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
						break; // continue on
					}

					// go back and re-take frame
				}

			}

			/*****************************************************/
			/* calculateCentroidStats */
			/*****************************************************/
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCentroidOffsets());

			/*****************************************************/
			/* passiveTiltScaleError */
			/*****************************************************/
			ScaleError scaleError = computationLibrary.passiveTiltScaleError(centroidOffsetsResult.getCentroidOffsets(),
					procedure.getRefDefMap());

			// fill the iteration output
			PassiveTiltIterationOutput pio = new PassiveTiltIterationOutput();
			procedureOutput.addIteration(pio);

			pio.setIteration(0);
			pio.setDeltaAzEl(new FloatPoint(1.1f, 2.3f));

			pio.setCentroidOffsets(centroidOffsetsResult.getCentroidOffsets().toArray(new FloatPoint[0]));
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
			procedureOutput.setCentroidOffsets(pio.getCentroidOffsets());

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

			// Display the average centroid offsets - this is probably not needed since we only do one trial
			graphicDisplayMgmt.displayCentroidOffsets(procedureOutput);

			// Go from segment tip/tilt offsets to actuator deltas with pistons set to zero
			/*****************************************************/
			/* ttOffsetsToActs */
			/*****************************************************/
			List<FloatPoint> actPosList = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getPrimaryActPos());
			// lpz = local piston zeroed on a segment
			float[][] lpzActDeltas = computationLibrary.ttOffsetsToActs(actPosList, centroidOffsetsResult.getImageScale(),
					centroidOffsetsResult.getCentroidOffsets());

			// Decompose the calculated actuators into pure tip/tilt and pure piston.
			// This code is to ensure that the pistons are indeed zero prior to proceding.
			/*****************************************************/
			/* decomposeActs */
			/*****************************************************/
			DecomposeActsResult decomposeActResult = computationLibrary.decomposeActs(lpzActDeltas);

			// Calculate the optimal pistons associated with the calculated
			// actuators (minimizes the changes to the edges). Note that this
			// routine just determines the optimal pistons; if you want to add
			// these on to the tip/tilt pistons, you need to do it yourself.

			/*****************************************************/
			/* optimalPistons */
			/*****************************************************/
			float[][] controlMatrix = constantsCache.getPrimaryMirrorConstants().getaMatrix();
			float[][] pistonActs = computationLibrary.optimalPistons(controlMatrix, decomposeActResult.getTipTiltActs());

			// combine tip/tilt and piston commands
			/*****************************************************/
			/* calcDesiredActCommands */
			/*****************************************************/
			float[][] desiredActDeltas = computationLibrary.addMatricies(decomposeActResult.getTipTiltActs(), pistonActs);

			// calculate RMS of the actuator cmds
			float desiredActDeltasRms = computationLibrary.calcRms(desiredActDeltas);

			// set iteration and procedure outputs
			pio.setTipTiltActuatorDeltas(decomposeActResult.getTipTiltActs());
			pio.setPistonActuatorDeltas(decomposeActResult.getPistonActs());

			pio.setM1ActuatorCmds(desiredActDeltas);
			pio.setM1ActuatorCmdsRms(desiredActDeltasRms);

			procedureOutput.setM1ActuatorCmds(pio.getM1ActuatorCmds());
			procedureOutput.setM1ActuatorCmdsRms(pio.getM1ActuatorCmdsRms());
			procedureOutput.setTipTiltActuatorDeltas(pio.getTipTiltActuatorDeltas());
			procedureOutput.setPistonActuatorDeltas(pio.getPistonActuatorDeltas());

			// display the pistonDeltas
			graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);

			// display RMS piston deltas to user in dialog
			String text = MessageGenerator.generateMessage("pt.m1_act_cmds_rms", desiredActDeltasRms);
			boolean commandAcs = userPromptMgmt.displayYesNoDialog(text + "\nCommand Primary Mirror?");

			// command ACS
			boolean commandsSent = false;
			if (commandAcs) {

				try {
					// send out the commands
					acsMgmt.commandActuatorDeltas(desiredActDeltas);

					statusLogger.log("pt.m1_act_cmd_success");
					logger.info("doSendActDeltaCommands: success");
					commandsSent = true;
				} catch (Exception e) {
					statusLogger.log("pt.m1_act_cmd_failed");
					e.printStackTrace();
				}

			}
			procedureOutput.setM1CmdsSent(commandsSent);

			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - no need to wait for response
				cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
			}

			statusLogger.log("procedure.end", procedure.getProcedureType().getProcedureTypeName());

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
