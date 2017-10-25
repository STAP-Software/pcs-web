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
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.StartupComputationsResult;
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
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltIterationOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

/**
 * Executor for the Passive Tilt procedure
 * @author smichaels
 */
@Singleton
@Startup
public class PassiveTiltExecutor {

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
	//@EJB
	//private PupilRegistrator pupilRegistrator;
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

	/**
	 * Executor method: this method is the Passive Tilt flow
	 */
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PassiveTiltExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PassiveTiltProcedureOutput procedureOutput = (PassiveTiltProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());

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
							autoTakeRefMap = userPromptMgmt.displayYesNoDialog("Need Ref Map Needed", e.getText() + "\nTake new Ref Map?");

						} else {
							autoTakeRefMap = true;
						}
					}
				}

				if (autoTakeRefMap) {

					CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
					Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
							ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession, 
							procedure.getTestNumber(), procedure.isOperational(), po);

					procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

					procedureExecutionState.setPendingSubProcedure(subProcedure);

					// execute the subprocedure
					createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

					currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
							.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);


				}
			}

			procedure.addRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			procedureExecutionState.setPercentComplete(10);
			readyCameraSubflow.execute(procedure);
			procedureExecutionState.setPercentComplete(20);
			
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
			/***********************************************/
			/*             Startup Computations            */
			/***********************************************/
			StartupComputationsResult startupComputationsResult = computationLibrary.startupComputations(
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getArcsecPerMeter(),
					physicalModel.getInstrument().getCcd().getCcdType().getPixelSize());

			
			// Set up the only iteration as the current output target
			PassiveTiltIterationOutput pio = new PassiveTiltIterationOutput();
			procedureExecutionState.setCurrentOutputTarget(pio);
			procedureOutput.addIteration(pio);

			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			Future<Exception> dcsFuture = centerTelescopeSubflow.centerTelescope(procedure, currentSession);
			procedureExecutionState.setPercentComplete(50);
			
			CentroidOffsetsResult centroidOffsetsResult= pio.getCentroidOffsetsResult();
			
			procedureExecutionState.setPercentComplete(70);

			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/

			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			/*****************************************************/
			/*              passiveTiltScaleError                */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
			
			pio.getProcedureIterationDecisionLog().setTelescopeMoved(false);

			
			// Display the average centroid offsets - this is probably not needed since we only do one trial
			if (procedureConfig.isAutoDisplayCentroidOffsets()) {
				graphicDisplayMgmt.displayCentroidOffsets(pio);
			}

			// Go from segment tip/tilt offsets to actuator deltas with pistons set to zero
			/*****************************************************/
			/*                  ttOffsetsToActs                  */
			/*****************************************************/
			List<FloatPoint> actPosList = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getPrimaryActPos());
			// lpz = local piston zeroed on a segment
			// TODO: the result here should be a TtOffsetsToActsResult object
			float[][] lpzActDeltas = computationLibrary.ttOffsetsToActs(actPosList, startupComputationsResult.getArcsecPerPixel(),
					centroidOffsetsResult.getCartesianCentroidOffsets(), globalConfig.getMirrorList());

			// Decompose the calculated actuators into pure tip/tilt and pure piston.
			// This code is to ensure that the pistons are indeed zero prior to proceding.
			/*****************************************************/
			/*                  decomposeActs                    */
			/*****************************************************/
			DecomposeActsResult decomposeActResult = computationLibrary.decomposeActs(lpzActDeltas);

			procedureExecutionState.setPercentComplete(80);

			/*****************************************************/
			/*                  optimalPistons                   */
			/*****************************************************/

			CalcDesiredActCommandsResult calcDesiredActCommandsResult = computationLibrary.calcDesiredActCommands(constantsCache.getPrimaryMirrorConstants().getaMatrix(), 
					decomposeActResult.getTipTiltActs(), globalConfig.getMirrorListInt());
			
			// fill the procedure output
			procedureOutput.addPassiveTiltIterationOutput(pio);

			// display the pistonDeltas
			if (procedureConfig.isAutoDisplayActuatorDeltas()) {
				graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);
			}

			// display RMS piston deltas to user in dialog
			String text = MessageGenerator.generateMessage("pt.m1_act_cmds_rms", calcDesiredActCommandsResult.getDesiredActDeltasRms());
			boolean commandAcs = userPromptMgmt.displayYesNoDialog("Primary Mirror Command", text + "\nCommand Primary Mirror?");

			procedureExecutionState.setPercentComplete(90);

			// command ACS
			boolean commandsSent = false;
			if (commandAcs) {

				// send out the commands
				statusLogger.log("pt.m1_act_cmd_started");

				procedureExecutionMgmt.commandActuatorDeltas(calcDesiredActCommandsResult.getDesiredActDeltas());

				statusLogger.log("pt.m1_act_cmd_success");
				logger.info("doSendActDeltaCommands: success");
				commandsSent = true;
					
				try {
					// take and store a snapshot
					int snapNum = acsMgmt.commandTakeSnap();
					procedureOutput.getProcedureDecisionLog().setM1SnapNumberAfter(snapNum);
					
				} catch (Exception e) {
					statusLogger.log("pt.m1_act_cmd_failed");
					logger.error(MessageGenerator.generateMessage("command.error"), e);
				}

			}
			
			procedureOutput.getProcedureDecisionLog().setM1CmdsSent(commandsSent);

			/*****************************************************/
			/*                  Wait for DCS                     */
			/*****************************************************/
			long dcsWaitPeriodMs = Utils.waitForComplete(dcsFuture);
			if (dcsFuture != null) {
				if (dcsFuture.get() == null) {
					statusLogger.log("dcs.cmd_completed", dcsWaitPeriodMs/1000.0);
				} else {
					statusLogger.log("telescope.cmd.failed");
					logger.error(MessageGenerator.generateMessage("command.error"), dcsFuture.get());
				}		
			}
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
				long waitPeriodMs =  Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
			}

			// close shutter
			// FIXME: remove this call when all shutter usage is deprecated
			cameraMgmt.commandCcdShutterState(CameraCommand.CLOSED);

			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);


			
		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		statusLogger.log("procedure.saving");
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
