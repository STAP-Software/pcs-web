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
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.model.SufsIterationOutput;
import org.tmt.aps.peas.procedure.model.SufsProcedureOutput;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
@Startup
public class SufsExecutor {

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
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private ReadyCameraSubflow readyCameraSubflow;
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
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CenterTelescopeSubflow centerTelescopeSubflow;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private SubimageDefCache subimageDefCache;
	@EJB
	private CreateRefMapExecutor createRefMapExecutor;
	@EJB
	private PupilRegistrationLoopSubflow pupilRegistrationLoopSubflow;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("SufsTiltExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("SufsTiltExecutor::testMethod::");
		return null;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("SUFS Executor::executeProcedure::");

		Future<Exception> dcsTelMoveFuture = null;

		boolean telescopeMoved = false;
		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();
			SufsCoarseOffsetsConfig sufsCoarseOffsetsConfig = procedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig();
			CentroidOffsetsConfig centroidOffsetsConfig = procedure.getProcedureConfigSet().getCentroidOffsetsConfig();

			SufsProcedureOutput procedureOutput = (SufsProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");

			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(),
					procedureConfig.getFilter().getFilterType().getFilterTypeId(), procedureConfig.getSufsGroup());

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD || currentRefMap == null) {

				boolean autoTakeRefMap = false;
				if (currentRefMap == null) {
					autoTakeRefMap = true;
				} else if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_NO) {
					autoTakeRefMap = false;
				} else {

					try {
						// SUFS coarse mirror position pointing to group
						Point coarseMirrorPosition = Point.add(globalConfig.getCoarseMirrorDefault(),
								sufsCoarseOffsetsConfig.getCoarseMirrorOffsetCurrent());
						computationLibrary.autoRefMapCheck(procedure.getProcedureConfigSet().getAutoRefMapConfig(), coarseMirrorPosition,
								globalConfig.getFineMirrorDefault(), physicalModel.getInstrument().getCcd().getTemperature(),
								procedureConfig.getNumberOfTrials(), new Date(), currentRefMap);

					} catch (AutoRefMapCheckException e) {

						statusLogger.log(e.getKey(), e.getArg1(), e.getArg2());

						if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_PROMPT) {
							// prompt user
							autoTakeRefMap = userPromptMgmt.displayYesNoDialog("New Ref Map Needed", e.getText() + "\nTake new Ref Map?");

						} else {
							autoTakeRefMap = true;
						}
					}
				}

				if (autoTakeRefMap) {

					CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
					Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
							ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession, procedure.getTestNumber(), po);

					procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

					procedureExecutionState.setPendingSubProcedure(subProcedure);

					// execute the subprocedure
					createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

					currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
							procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(),
							procedureConfig.getFilter().getFilterType().getFilterTypeId(), procedureConfig.getSufsGroup());

				}
			}

			procedure.setRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());

			logger.debug("calcM2M1Config = " + procedure.getProcedureConfigSet().getCalcM2M1Config());

			/**********************************************/
			/*    Move Telescope to compensate for SUFS   */
			/*        group coarse mirror steering        */
			/**********************************************/

			// TODO: make DCS call asynchronous and wait after readyCamera
			// determine telescope moves given coarse offsets
			FloatPoint telescopeMoveAzEl = computationLibrary.coarseOffsetsToTelMoves(
					sufsCoarseOffsetsConfig.getCoarseMirrorOffsetCurrent(), constantsCache.getTelescopeConstants().getTelPerCoarseMotion());

			// Auto point logic
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				if (procedureConfig.getAutoPointTelescopeSufsGroup() != Constants.AUTO_SUFS_POINT_TEL_NO) {

					boolean autoPointTelescope = false;
					if (procedureConfig.getAutoPointTelescopeSufsGroup() == Constants.AUTO_SUFS_POINT_TEL_PROMPT) {
						// prompt user
						autoPointTelescope = userPromptMgmt.displayYesNoDialog("SUFS Point Telescope",
								"Send telescope commands to point to SUFS group?");

					} else {
						autoPointTelescope = true;
					}

					if (autoPointTelescope) {

						// send commands to DCS
						statusLogger.log("telescope.desired_move", telescopeMoveAzEl.x, telescopeMoveAzEl.y);
						statusLogger.log("telescope.cmd.start");

						// send out the commands
						dcsTelMoveFuture = dcsMgmt.commandTelescopeDeltasAsync(telescopeMoveAzEl.asDoubleArray());
					}
				}
				
			}

			/**********************************************/
			/*                Ready Camera                */
			/**********************************************/
			readyCameraSubflow.execute(procedure);

			/*****************************************************/
			/*       wait for Move Telescope to complete         */
			/*****************************************************/
			long waitPeriodMsTelMove = Utils.waitForComplete(dcsTelMoveFuture);
			if (dcsTelMoveFuture != null) {

				if (dcsTelMoveFuture.get() == null) {
					statusLogger.log("telescope.cmd.end");
					logger.info("commandTelescopeDeltas: success");
					telescopeMoved = true;
					statusLogger.log("dcs.cmd_completed", waitPeriodMsTelMove / 1000.0);
				} else {
					statusLogger.log("telescope.cmd.failed");
					logger.error(MessageGenerator.generateMessage("command.error"), dcsTelMoveFuture.get());
				}
			}

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			int readyCameraTime = 10;
			int trialsTime = 70;

			for (int i = 0; i < procedureConfig.getNumberOfTrials(); i++) {

				int trialTimeDelta = (trialsTime / procedureConfig.getNumberOfTrials()) * i + readyCameraTime;
				procedureExecutionState.setPercentComplete(trialTimeDelta);

				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), i + 1,
						procedureConfig.getNumberOfTrials());

				procedureExecutionState.incrementIteration();

				// setup the iteration output as the output target
				SufsIterationOutput pio = new SufsIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);

				/*****************************************************/
				/*             centerTelescopeCalc subflow           */
				/*****************************************************/
				Future<Exception> dcsFuture = centerTelescopeSubflow.centerTelescope(procedure, currentSession);

				CentroidOffsetsResult centroidOffsetsResult = pio.getCentroidOffsetsResult();

				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();

				SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(
						procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getSufsGroup());

				/*****************************************************/
				/*   Divide up offsets to each segment and recalc    */
				/*****************************************************/

				int[][] sufsGroupSegmentToMask = constantsCache.getSufsConstants().getSufsGroupSegmentToMask();

				SufsSegmentOffsetsResult sufsCentroidOffsets = computationLibrary.calculateSufsCentroidOffsets(findCentroidsResult,
						procedure.getRefBeamMap().getCentroidMap().getFindCentroidsResult(),
						procedure.getProcedureConfigSet().getCentroidOffsetsConfig(), procedureConfig.getPupilMaskType(),
						subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), sufsGroupSegmentToMask,
						centroidOffsetsConfig.getSufsIgnoreSubimageThreshold());

				/*****************************************************/
				/*                calculateCentroidStats             */
				/*****************************************************/

				computationLibrary.calculateSufsCentroidStats(sufsCentroidOffsets, findCentroidsResult.getFindCentStatusList(),
						subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), sufsGroupSegmentToMask);

				/*****************************************************/
				/*        Calculate Zernikes from Offsets            */
				/*****************************************************/

				computationLibrary.calculateSufsZernikes(constantsCache.getPrimaryMirrorSegmentConstants().getSufsSpotCoordinates(),
						centroidOffsetsResult.getCcdCentroidOffsets(), constantsCache.getPrimaryMirrorConstants().getaHex(),
						procedureConfig.getPupilMask().getSecPerPixel(), sufsCentroidOffsets.getValidOffsets(),
						sufsGroupSegmentToMask,
						procedure.getProcedureConfigSet().getGlobalConfig().getSufsZernikeOrderArray(),
						constantsCache.getSufsConstants().getSufsGroupToMirror()[procedureConfig.getSufsGroup() - 1]);

				// TODO: this may eventually be handled in a different structure
				pio.getProcedureIterationDecisionLog().setTelescopeMoved(false);

				/*****************************************************/
				/*               Display Centroid Offsets            */
				/*****************************************************/

				if (procedureConfig.isAutoDisplayCentroidOffsets()) {
					graphicDisplayMgmt.displaySufsCentroidOffsets(pio);
				}

				/*****************************************************/
				/*                 Wait for DCS                      */
				/*****************************************************/
				long dcsWaitPeriodMs = Utils.waitForComplete(dcsFuture);
				if (dcsFuture != null) {
					if (dcsFuture.get() == null) {
						statusLogger.log("dcs.cmd_completed", dcsWaitPeriodMs / 1000.0);
					} else {
						statusLogger.log("telescope.cmd.failed");
						logger.error(MessageGenerator.generateMessage("command.error"), dcsFuture.get());
					}
				}

			} // end of iteration loop

			procedureExecutionState.setPercentComplete(trialsTime + readyCameraTime);

			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			/*****************************************************/
			/*                 Restore Telescope                 */
			/*****************************************************/

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// restore telescope
				if (telescopeMoved) {

					statusLogger.log("telescope.desired_move", -telescopeMoveAzEl.x, -telescopeMoveAzEl.y);
					statusLogger.log("telescope.cmd.start");

					// send out the negative of the previous commands
					dcsTelMoveFuture = dcsMgmt.commandTelescopeDeltasAsync(telescopeMoveAzEl.prod(-1.0).asDoubleArray());

				}

			}

			/****************************************************/
			/*              calc average good spots             */
			/****************************************************/

			// this needs to be an average over all frames
			int[][] findCentStatusIterations = procedureOutput
					.getIterationValuesFor("FindCentroidsResult", "FindCentStatusList", int[].class).toArray(new int[0][0]);

			// calculateAvgFindCentStatus returns 1 for good spots, 0 for any bad
			int[] avgGoodSpotMask = computationLibrary.calculateAvgFindCentStatus(findCentStatusIterations);
			int[][] sufsGroupSegmentToMask = constantsCache.getSufsConstants().getSufsGroupSegmentToMask();

			/*****************************************************/
			/*             calcAvgCentroidOffsets                */
			/*****************************************************/

			SufsSegmentOffsetsResult[] sufsOffsetsIterations = procedureOutput
					.getIterationResultObjectFor("SufsSegmentOffsetsResult", SufsSegmentOffsetsResult.class)
					.toArray(new SufsSegmentOffsetsResult[0]);

			SufsSegmentOffsetsResult sufsSegmentAvgOffsetsResult = computationLibrary.calcAvgSufsCentroidOffsets(sufsOffsetsIterations,
					avgGoodSpotMask, constantsCache.getSufsConstants().getSufsGroupSegmentToMask());

			/*****************************************************/
			/*         calculateCentroidStats - avg SUFS         */
			/*****************************************************/

			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getSufsGroup());

			FindCentroidsResult[] findCentroidsIterations = procedureOutput
					.getIterationResultObjectFor("FindCentroidsResult", FindCentroidsResult.class).toArray(new FindCentroidsResult[0]);

			/*****************************************************/
			/*             calcAvgCentroidStats                  */
			/*****************************************************/
			computationLibrary.calculateSufsAvgCentroidStats(sufsSegmentAvgOffsetsResult, avgGoodSpotMask, subimageDefList.getNspotTypes(),
					sufsGroupSegmentToMask);

			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());

			/*****************************************************/
			/*           Calculate Zernikes Avg and EOM          */
			/*                 calcZernikeStats                  */
			/*****************************************************/

			SufsSegmentZernikeResult[] sufsSegmentZernikeResultIterations = procedureOutput
					.getIterationResultObjectFor("SufsSegmentZernikeResult", SufsSegmentZernikeResult.class)
					.toArray(new SufsSegmentZernikeResult[0]);

			SufsSegmentZernikeStatsResult sufsSegmentZernikeStatsResult = computationLibrary
					.calculateSufsZernikeStats(sufsSegmentZernikeResultIterations);

			/*****************************************************/
			/*        Display Avg SUFS Centroid Offsets          */
			/*****************************************************/

			// Display the average centroid offsets
			if (procedureConfig.isAutoDisplayAvgSufsCentroidOffsets()) {

				graphicDisplayMgmt.displayAvgSufsCentroidOffsets(procedureOutput);
			}

			procedureExecutionState.setPercentComplete(85);

			/*****************************************************/
			/*      wait for Restore Telescope to complete       */
			/*****************************************************/
			waitPeriodMsTelMove = Utils.waitForComplete(dcsTelMoveFuture);
			if (dcsTelMoveFuture != null) {

				if (dcsTelMoveFuture.get() == null) {
					statusLogger.log("telescope.cmd.end");
					logger.info("commandTelescopeDeltas: success");
					statusLogger.log("dcs.cmd_completed", waitPeriodMsTelMove / 1000.0);
				} else {
					statusLogger.log("telescope.cmd.failed");
					logger.error(MessageGenerator.generateMessage("command.error"), dcsTelMoveFuture.get());
				}
			}

			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(99);
				long waitPeriodMs = Utils.waitForComplete(refBeamFuture);
				statusLogger.log("camera.cmd.complete", waitPeriodMs / 1000.0);
			}

			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}

		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
