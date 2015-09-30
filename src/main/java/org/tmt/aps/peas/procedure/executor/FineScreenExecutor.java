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
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.FineScreenIterationOutput;
import org.tmt.aps.peas.procedure.model.FineScreenProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
@Startup
public class FineScreenExecutor {

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
	private ReadyCamera readyCamera;
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
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CenterTelescopeCalc centerTelescopeCalc;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private SubimageDefCache subimageDefCache;
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

		logger.info("Fine Screen Executor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			FineScreenProcedureOutput procedureOutput = (FineScreenProcedureOutput) procedure.getProcedureOutput();

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
						
						computationLibrary.autoRefMapCheck(procedure.getProcedureConfigSet().getAutoRefMapConfig(), globalConfig.getCoarseMirrorDefault(), 
								globalConfig.getFineMirrorDefault(), physicalModel.getInstrument().getCcd().getTemperature(), 1, new Date(), currentRefMap);

					} catch (AutoRefMapCheckException e) {

						statusLogger.log(e.getKey(), e.getArg1(), e.getArg2());

						if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_PROMPT) {
							// prompt user
							autoTakeRefMap = userPromptMgmt.displayYesNoDialog(e.getText() + "\nTake new Ref Map?");

						} else {
							autoTakeRefMap = true;
						}
					}
				}

				if (autoTakeRefMap) {

					CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
					Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
							ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession, 
							procedure.getTestNumber(), po);

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

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
			logger.debug("calcM2M1Config = " + procedure.getProcedureConfigSet().getCalcM2M1Config());
						
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCamera.execute(procedure);
			
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
			/*
			 *  First we will only have one iteration and one calculation method
			 *  
			 */ 
			         


	//DO I = 1, NUMBER_TRIALS
	//
    //       WRITE(CI, FMT='(I2)') I
    //       WRITE(CNT, FMT='(I2)') NUMBER_TRIALS
    //       TEXT = 'Fine Screen, Loop '//CI//' of '//CNT
	//
    //       CALL FUPDATE_WORK_MESSAGE(TEXT, LEN(TEXT))
           
		for (int i=0; i<2; i++) {
           
			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			CenterTelescopeCalcResult centerTelescopeCalcResult = centerTelescopeCalc.centerTelescope(procedure, currentSession);
			
			CentroidOffsetsResult centroidOffsetsResult= centerTelescopeCalcResult.getCentroidOffsetsResult();
			FloatPoint deltaAzEl = centerTelescopeCalcResult.getDeltaAzEl();


			procedureExecutionState.setPercentComplete(80);

			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/

			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			/*****************************************************/
			/*              fineScreenScaleError                 */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
			
			ScaleError scaleError = computationLibrary.fineScreenScaleError(centroidOffsetsResult.getCcdCentroidOffsets(),
					centerSpots, subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());
			
			// fill the iteration output
			FineScreenIterationOutput pio = new FineScreenIterationOutput();
			procedureOutput.addIteration(pio);

			pio.addCenterTelescopeCalcResult(centerTelescopeCalcResult);

			pio.addCentroidOffsetsResult(centroidOffsetsResult);
			
			pio.addCentroidStatsResult(centroidStatsResult);
			
			pio.addScaleError(scaleError);
			
			pio.setTelescopeMoved(false);

			// fill the output - many of these are copied from the one iteration
			procedureOutput.addFineScreenIterationOutput(pio);
			
			/*****************************************************/
			/*             Display Centroid Offsets              */
			/*****************************************************/
			graphicDisplayMgmt.displayCentroidOffsets(pio);

			
			
			// TODO: call pupil_registration for fine screen, and center the pupil
			
          
			// TODO: if calc option is Ray Trace:
						
			CalcM2M1Result calcM2M1Result = computationLibrary.calculateM2M1RayTrace(findCentroidsResult, centroidOffsetsResult, 
					subimageDefList.getUseForM2InteriorSpotFlags(),
					procedure.getProcedureConfigSet().getCalcM2M1Config(),
					constantsCache.getPrimaryMirrorConstants().getFineScreenSpotCoords(), 
					subimageDefList.getNspotTypes(),
					constantsCache.getTelescopeConstants());
			
			System.out.println();
			
		}

			
			

			// Go from segment tip/tilt offsets to actuator deltas with pistons set to zero
			/*****************************************************/
			/*                  ttOffsetsToActs                  */
			/*****************************************************/
			
			/**
			 * can not call yet
			 *
			
			List<FloatPoint> actPosList = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getPrimaryActPos());
			// lpz = local piston zeroed on a segment
			
			float[][] lpzActDeltas = computationLibrary.ttOffsetsToActs(actPosList, procedureConfig.getPupilMask().getSecPerPixel(),
					centroidOffsetsResult.getCartesianCentroidOffsets());

			// Decompose the calculated actuators into pure tip/tilt and pure piston.
			// This code is to ensure that the pistons are indeed zero prior to proceding.
			/*****************************************************/
			/*                  decomposeActs                    */
			/*****************************************************/
			
			/**
			 * can not call yet
			 *
			
			DecomposeActsResult decomposeActResult = computationLibrary.decomposeActs(lpzActDeltas);

			// Calculate the optimal pistons associated with the calculated
			// actuators (minimizes the changes to the edges). Note that this
			// routine just determines the optimal pistons; if you want to add
			// these on to the tip/tilt pistons, you need to do it yourself.

			/*****************************************************/
			/*                  optimalPistons                   */
			/*****************************************************/
			
			/**
			 * cannot call yet
			 *
			float[][] controlMatrix = constantsCache.getPrimaryMirrorConstants().getaMatrix();
			float[][] pistonActs = computationLibrary.optimalPistons(controlMatrix, decomposeActResult.getTipTiltActs());

			// calculate RMS of the actuator cmds
			float pistonActsRms = computationLibrary.calcRms(pistonActs);

			
			// combine tip/tilt and piston commands
			/*****************************************************/
			/*               calcDesiredActCommands              */
			/*****************************************************/
			
			/**
			 * cannot call yet 
			 *
			
			float[][] desiredActDeltas = computationLibrary.addMatricies(decomposeActResult.getTipTiltActs(), pistonActs);

			// calculate RMS of the actuator cmds
			float desiredActDeltasRms = computationLibrary.calcRms(desiredActDeltas);

			
			// set iteration and procedure outputs
			pio.setTipTiltActuatorDeltas(decomposeActResult.getTipTiltActs());
			pio.setPistonActuatorDeltas(pistonActs);
			pio.setPistonActuatorDeltasRms(pistonActsRms);

			pio.setM1ActuatorCmds(desiredActDeltas);
			pio.setM1ActuatorCmdsRms(desiredActDeltasRms);

			procedureOutput.setM1ActuatorCmds(pio.getM1ActuatorCmds());
			procedureOutput.setM1ActuatorCmdsRms(pio.getM1ActuatorCmdsRms());
			procedureOutput.setTipTiltActuatorDeltas(pio.getTipTiltActuatorDeltas());
			procedureOutput.setPistonActuatorDeltas(pio.getPistonActuatorDeltas());
			procedureOutput.setPistonActuatorDeltasRms(pio.getPistonActuatorDeltasRms());

			

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
					
					// take and store a snapshot
					int snapNum = acsMgmt.commandTakeSnap();
					procedureOutput.setM1SnapNumberAfter(snapNum);
					
				} catch (Exception e) {
					statusLogger.log("pt.m1_act_cmd_failed");
					logger.error(MessageGenerator.generateMessage("command.error"), e);
				}

			}
			procedureOutput.setM1CmdsSent(commandsSent);

			*/

			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
		        Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete");
			}

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
