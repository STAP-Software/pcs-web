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
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
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
import org.tmt.aps.peas.instrument.model.PupilMaskType;
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
			readyCameraSubflow.execute(procedure);
			
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
         
			for (int i=0; i<procedureConfig.getNumberOfTrials(); i++) {
				
				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), i+1, procedureConfig.getNumberOfTrials());
				
				procedureExecutionState.incrementIteration();
	
				// setup the iteration output as the output target
				FineScreenIterationOutput pio = new FineScreenIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);
	
				/*****************************************************/
				/*          centerTelescope                          */
				/*****************************************************/	
				centerTelescopeSubflow.centerTelescope(procedure, currentSession);
	
				procedureExecutionState.setPercentComplete(20);
	
				/*****************************************************/
				/*              calculateCentroidStats               */
				/*****************************************************/
	
				CentroidOffsetsResult centroidOffsetsResult= pio.getCentroidOffsetsResult();
				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
				SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());
				
				computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
						subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());
	
				/*****************************************************/
				/*              fineScreenScaleError                 */
				/*****************************************************/
				
				List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
				
				computationLibrary.fineScreenScaleErrorResult(centroidOffsetsResult.getCcdCentroidOffsets(),
						centerSpots, subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());
				
				// TODO: this may eventually be handled in a different structure
				pio.getProcedureIterationDecisionLog().setTelescopeMoved(false);
	
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
						constantsCache.getTelescopeConstants(), procedureConfig.getPupilMask().getSecPerPixel());
					
								
				// calc centroid stats for passive tilt
				
				SubimageDefList subimageDefListPt = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);				
				computationLibrary.calculatePseudoCentroidStats(calcM2M1Result.getM1OffsetsCorrectedForM2Pixels(), subimageDefListPt.getNspotTypes());

				// calc scale error for passive tilt
				computationLibrary.passiveTiltScaleErrorResult(calcM2M1Result.getM1OffsetsCorrectedForM2Pixels(), centerSpots);
	
				// Go from segment tip/tilt offsets to actuator deltas with pistons set to zero
				/*****************************************************/
				/*                  ttOffsetsToActs                  */
				/*****************************************************/
				
				List<FloatPoint> actPosList = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getPrimaryActPos());
				// lpz = local piston zeroed on a segment
				// TODO: the result here should be a TtOffsetsToActsResult object
				float[][] lpzActDeltas = computationLibrary.ttOffsetsToActs(actPosList, procedureConfig.getPupilMask().getSecPerPixel(),
						calcM2M1Result.getM1OffsetsCorrectedForM2Pixels());
		
				// Decompose the calculated actuators into pure tip/tilt and pure piston.
				// This code is to ensure that the pistons are indeed zero prior to proceding.
				/*****************************************************/
				/*                  decomposeActs                    */
				/*****************************************************/
				
				DecomposeActsResult decomposeActResult = computationLibrary.decomposeActs(lpzActDeltas);
				
				/*****************************************************/
				/*                  optimalPistons                   */
				/*****************************************************/
				
				float[][] controlMatrix = constantsCache.getPrimaryMirrorConstants().getaMatrix();				
				computationLibrary.calcDesiredActCommands(controlMatrix, decomposeActResult.getTipTiltActs());			

			}

			
			
			/*
	
			// display the pistonDeltas
			if (procedure.getProcedureConfigSet().getGlobalConfig().isAutoDisplayActuatorDeltas()) {
				graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);
			}
	
			// display RMS piston deltas to user in dialog
			String text = MessageGenerator.generateMessage("pt.m1_act_cmds_rms", calcDesiredActCommandsResult.getDesiredActDeltasRms());
			boolean commandAcs = userPromptMgmt.displayYesNoDialog(text + "\nCommand Primary Mirror?");
	
			// command ACS
			boolean commandsSent = false;
			if (commandAcs) {
	
				try {
					// send out the commands
					acsMgmt.commandActuatorDeltas(calcDesiredActCommandsResult.getDesiredActDeltas());
	
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
			
			
			// TODO: eventually replace this with an framework solution
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
