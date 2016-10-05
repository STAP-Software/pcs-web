/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.BbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.ColorStepResult;
import org.tmt.aps.peas.computation.model.ColorStepToActuatorsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
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
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.CoarsePhasingIterationOutput;
import org.tmt.aps.peas.procedure.model.CoarsePhasingProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * Executor for the broadband Phasing procedure
 * @author smichaels
 */
@Singleton
@Startup
public class CoarsePhasingExecutor {

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
		logger.debug("PhasingExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PhasingExecutor::testMethod::");
		return null;
	}

	/**
	 * Executor method: this method is the broad band Phasing flow
	 */
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("Phasing Executor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			CoarsePhasingProcedureOutput procedureOutput = (CoarsePhasingProcedureOutput) procedure.getProcedureOutput();

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
								globalConfig.getFineMirrorDefault(), physicalModel.getInstrument().getCcd().getTemperature(), procedureConfig.getNumberOfTrials(), new Date(), currentRefMap);

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

			procedure.addRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
						
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCameraSubflow.execute(procedure);
			
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			statusLogger.log("procedure.using_curr_frame");
			
			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			int readyCameraTime = 10;
			int trialsTime = 70;
        			

			// calculate templates on the fly
			MakeTemplateResult makeTemplateResult = computationLibrary.makeTemplate(
					constantsCache.getPhasingConstants().getPhasingSubimageFftSize(), 
					constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
					procedure.getProcedureConfigSet().getFindCentConfigInterior(),
					procedureConfig.getPupilMask(), procedureConfig.getFilter());
					
			/**********************************************/
			/*           Set up colorsteps                */
			/**********************************************/
			ColorStepResult colorStepResult = computationLibrary.colorStep(procedureConfig.getPhasingSteps(), procedureConfig.getPhasingStepSize());
						
			
			// take and store a snapshot
			int snapNumBefore = acsMgmt.commandTakeSnap();
			procedureOutput.getProcedureDecisionLog().setM1SnapNumberBefore(snapNumBefore);


 			/**********************************************/
			/// send colorstep 1 to ACS prior to loop
			/**********************************************/
		
							
			for (int i=0; i<procedureConfig.getPhasingSteps(); i++) {
				
				int trialTimeDelta = (trialsTime/procedureConfig.getPhasingSteps())*i + readyCameraTime;
				procedureExecutionState.setPercentComplete(trialTimeDelta);
				
				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), i+1, procedureConfig.getPhasingSteps());
				
				procedureExecutionState.incrementIteration();
	
				// setup the iteration output as the output target
				CoarsePhasingIterationOutput pio = new CoarsePhasingIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);
				
				/**********************************************/
				/* Send next ACS colorstep commands           */
				/**********************************************/

				ColorStepToActuatorsResult colorStepToActuatorsResult = computationLibrary.colorStepToActuators(
						colorStepResult.getColorSteps()[i],
						constantsCache.getPrimaryMirrorConstants().getnColor());
				
				statusLogger.log("acs.colorstep_cmds");
				
				long deltaMs = procedureExecutionMgmt.commandActuatorDeltas(colorStepToActuatorsResult.getM1ActuatorDeltas());
				
				statusLogger.log("acs.cmd_completed", deltaMs/1000.0);
				
					
				/**********************************************/
				/*        PupilRegistration Subflow           */
				/**********************************************/
				pupilRegistrationLoopSubflow.pupilRegistrationLoop(procedure, currentSession);
										
				
				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
				
				/**********************************************/
				/// BbAnalyzeFrame
				/**********************************************/		
			    BbAnalyzeFrameResult bbAnalyzeFrameResult = computationLibrary.bbAnalyzeFrame(
			    		procedure.getLatestProcedureCcdFrame().getCcdFrame().getCorrectedFrame(),
			    		findCentroidsResult, 
			    		constantsCache.getPrimaryMirrorConstants().getEdgeAngle(),
			    		makeTemplateResult.getTemplateArray(), constantsCache.getTelescopeConstants().getNumberOfSegments());
			    
				
			} // end of iteration loop
	
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

		    // send last colorstep to M1 
			ColorStepToActuatorsResult colorStepToActuatorsResult = computationLibrary.colorStepToActuators(
					colorStepResult.getColorSteps()[procedureConfig.getPhasingSteps()],
					constantsCache.getPrimaryMirrorConstants().getnColor());

			
			statusLogger.log("acs.colorstep_cmds");
			
			long deltaMs = procedureExecutionMgmt.commandActuatorDeltas(colorStepToActuatorsResult.getM1ActuatorDeltas());
			statusLogger.log("acs.cmd_completed", deltaMs/1000.0);

			
			procedureExecutionState.setPercentComplete(trialsTime + readyCameraTime);


			/****************************************************/
			/*    calc union good spots over all steps          */
			/****************************************************/
			
			// this needs to be an average over all frames
			int[][] findCentStatusIterations = procedureOutput.getIterationValuesFor("FindCentroidsResult", "FindCentStatusList", int[].class).toArray(new int[0][0]);
			
			int[] goodSpots = computationLibrary.calculateAvgFindCentStatus(findCentStatusIterations);

			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			float[][] coherenceArraySet = procedureOutput.getIterationValuesFor("BbAnalyzeFrameResult", "CoherenceArray", float[].class).toArray(new float[0][0]);

		                                                                                
			/**********************************************/
			/*            BbAnalyzeSequence               */
			/**********************************************/
		    BbAnalyzeSequenceResult bbAnalyzeSequenceResult = computationLibrary.bbAnalyzeSequence(
		    		constantsCache.getPrimaryMirrorConstants().getEdgeAngle(),
		    		constantsCache.getPrimaryMirrorConstants().getEdgeColor(),
		    		coherenceArraySet, 
		    		procedureConfig.getPhasingStepSize(), 
		    		constantsCache.getTelescopeConstants().getNumberOfSegments(), 
		    		constantsCache.getPrimaryMirrorConstants().getSavePlusPiston(),
		    		constantsCache.getPrimaryMirrorConstants().getSaveMinusPiston(),
		    		constantsCache.getPhasingConstants().getRingModeCorrectionFactor(),
		    		procedureConfig.getFilter(),
 					constantsCache.getPhasingConstants().getBbPhasingFracInterval(),
 					constantsCache.getPhasingConstants().getRingMode(),
					procedureConfig.getPhasingSteps(), 
		    		subimageDefList.useForAnalysis(), goodSpots);
		
		    
		    if (bbAnalyzeSequenceResult.getConstrainedSegmentCount() != constantsCache.getTelescopeConstants().getNumberOfSegments()) {
		    	
			    userPromptMgmt.displayInfoDialog("Constrained Segment Warning", MessageGenerator.generateMessage("phasing.constrained_warning", 
			    		bbAnalyzeSequenceResult.getConstrainedSegmentCount(), constantsCache.getTelescopeConstants().getNumberOfSegments()));
		    	
		    }
		    
		    statusLogger.log("procedure.cph_calc_piston");
		
		    
		    /**********************************************/
			/*                FixPistons                  */
			/**********************************************/		
		    FixPistonsResult fixPistonsResult = computationLibrary.fixPistons(
		    		constantsCache.getPrimaryMirrorConstants().getPrimaryActPos(), 
		    		bbAnalyzeSequenceResult.getActCalc());

		    /**********************************************/
			/*          CalculatePhasingStats             */
			/**********************************************/		
		    computationLibrary.calculatePhasingStats(
		    		bbAnalyzeSequenceResult.getRowFlagIn(), 
		    		bbAnalyzeSequenceResult.getRowFlagOut(), 
		    		bbAnalyzeSequenceResult.getStepCorr(),
		    		bbAnalyzeSequenceResult.getResid());
		      
		    /**********************************************/
			/*       CalculateDesiredActCommands          */
			/**********************************************/		
		    computationLibrary.fixPistonsToDesiredActs(fixPistonsResult);

		    /**********************************************/
			/*      Display Measured Edge Heights         */
			/**********************************************/		
		    if (procedureConfig.isAutoDisplayEdgeHeights()) {
				graphicDisplayMgmt.displayEdgeHeights(procedureOutput);
			}

		    /**********************************************/
			/*      Display Residual Edge Heights         */
			/**********************************************/		
		    if (procedureConfig.isAutoDisplayResiduals()) {
				graphicDisplayMgmt.displayEdgeResiduals(procedureOutput);
			}

		    /**********************************************/
			/*          Display Piston Deltas             */
			/**********************************************/		
			if (procedureConfig.isAutoDisplayActuatorDeltas()) {
				graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);
			}

		    statusLogger.log("procedure.cph.algorithm_complete");

		    
			procedureExecutionState.setPercentComplete(98);
						

		    statusLogger.log("calc.phasing_summary",
					procedureOutput.getFixPistonsResult().getActRms(), 
					procedureOutput.getPhasingStatsResult().getResidualEdgeErrorRss(), 
					procedureOutput.getPhasingStatsResult().getGoodEdgeCount());

			
			// prepare to command primary
			boolean sendM1Command = procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_YES;
			if (procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_PROMPT) {
								
				// Display to user and ask if they want to command				
				String phasingSummaryText = MessageGenerator.generateMessage("calc.phasing_summary",
						procedureOutput.getFixPistonsResult().getActRms(), 
						procedureOutput.getPhasingStatsResult().getResidualEdgeErrorRss(), 
						procedureOutput.getPhasingStatsResult().getGoodEdgeCount());
				
				sendM1Command = userPromptMgmt.displayYesNoDialog("Primary Mirror Command", phasingSummaryText  + "\n\n\nCommand Primary Mirror?");
			}
			
	
			// command ACS
			boolean commandsSent = false;
			if (sendM1Command) {
	
				// send out the commands
				statusLogger.log("pt.m1_act_cmd_started");
				
				deltaMs = procedureExecutionMgmt.commandActuatorDeltas(procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltas());

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
		
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(99);
				long waitPeriodMs =  Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
			}
	
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());
	
			procedureExecutionState.setPercentComplete(100);
				
			
		} catch (Throwable e) {
			
			// just to make sure
			procedureExecutionState.setAbortRequested(false);
			
			try {
				/// attempt to put ACS state back to where it was when we began.
				
				if (procedure.getCoarsePhasingProcedureOutput().getM1SnapNumberBefore() != -1) {
					
					acsMgmt.commandLoadSnap(procedure.getCoarsePhasingProcedureOutput().getM1SnapNumberBefore());
				
					statusLogger.log("procedure.cph.abort_recovered");
					
					// put up a warning dialog with the non-recovered text (and supress abort button)
				    userPromptMgmt.displayInfoDialog("Successful Mirror Restoration", MessageGenerator.generateMessage("procedure.cph.abort_recovered"), true);
			    
				} 

			} catch (Exception e1) {
				
				e1.printStackTrace();
				
			    statusLogger.log("procedure.cph.abort_not_recovered");

			    // put up a warning dialog with the non-recovered text
			    userPromptMgmt.displayInfoDialog("WARNING", MessageGenerator.generateMessage("procedure.cph.abort_not_recovered"));

			} 
			
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		statusLogger.log("procedure.saving");
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}
	
	


}
