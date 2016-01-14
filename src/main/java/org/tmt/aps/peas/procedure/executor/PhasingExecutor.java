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
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
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
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PhasingIterationOutput;
import org.tmt.aps.peas.procedure.model.PhasingProcedureOutput;
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
public class PhasingExecutor {

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

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("Phasing Executor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			PhasingProcedureOutput procedureOutput = (PhasingProcedureOutput) procedure.getProcedureOutput();

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

			procedure.setRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
						
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCameraSubflow.execute(procedure);
			
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			int readyCameraTime = 10;
			int trialsTime = 70;
        			

			// calculate templates on the fly
			MakeTemplateResult makeTemplateResult = computationLibrary.makeTemplate(
					constantsCache.getPhasingConstants().getPhasingSubimageFftSize(), 
					constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
					procedure.getProcedureConfigSet().getFindCentConfigInterior(),
					procedureConfig.getPupilMask(), procedureConfig.getFilter());
					
						
		
			/// MAXSTEP = (ZPHASING_COARSE_STEPS - 1)/2.0
		
			// one filter, one integration time
		
			// User will choose Phasing 30, 300, 1000 and only one filter, int time.
			// default number of steps - all = 11
			// set of default int times depending on phasing test and/or filter.
					
						

 			/**********************************************/
			/// send colorstep 1 to ACS prior to loop
			/**********************************************/
		
							
			for (int i=0; i<procedureConfig.getPhasingSteps(); i++) {
				
				int trialTimeDelta = (trialsTime/procedureConfig.getPhasingSteps())*i + readyCameraTime;
				procedureExecutionState.setPercentComplete(trialTimeDelta);
				
				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), i+1, procedureConfig.getPhasingSteps());
				
				procedureExecutionState.incrementIteration();
	
				// setup the iteration output as the output target
				PhasingIterationOutput pio = new PhasingIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);
				
				/**********************************************/
				/*        wait for ACS to be done			  */
				/**********************************************/
				// TODO
	
				/**********************************************/
				/*        PupilRegistration Subflow           */
				/**********************************************/
				// TODO: may need to change this for performance reasons
				// TODO: if we fail and need to retake frame, then this should be here
				pupilRegistrationLoopSubflow.pupilRegistrationLoop(procedure, currentSession);
				// TODO: if user aborts from pupilreg, restore mirror
				
				/**********************************************/
				/*        send next colorstep to ACS 		  */
				/**********************************************/				
				// TODO: send next colorstep to ACS
						
				
				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
				
				/**********************************************/
				/// BbAnalyzeFrame
				/**********************************************/		
			    BbAnalyzeFrameResult BbAnalyzeFrameResult = computationLibrary.bbAnalyzeFrame(
			    		procedure.getLatestProcedureCcdFrame().getCcdFrame().getCorrectedFrame(),
			    		findCentroidsResult, 
			    		constantsCache.getPrimaryMirrorConstants().getEdgeAngle(),
			    		makeTemplateResult.getTemplateArray(), constantsCache.getTelescopeConstants().getNumberOfSegments());
			    
				
			} // end of iteration loop
			
			
			procedureExecutionState.setPercentComplete(trialsTime + readyCameraTime);

			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			/****************************************************/
			/*    calc union good spots over all steps          */
			/****************************************************/
			
			// this needs to be an average over all frames
			int[][] findCentStatusIterations = procedureOutput.getIterationValuesFor("FindCentroidsResult", "FindCentStatusList", int[].class).toArray(new int[0][0]);
			
			int[] goodSpots = computationLibrary.calculateAvgFindCentStatus(findCentStatusIterations);

			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			float[][] coherenceArraySet = procedureOutput.getIterationValuesFor("BbAnalyzeFrameResult", "CoherenceArray", float[].class).toArray(new float[0][0]);

		                                                                                
			/**********************************************/
			/// BbAnalyzeSequence
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
		
		    statusLogger.log("procedure.cph.algorithm_complete");
		
		
			// TEST ONLY
			if (procedureConfig.isAutoDisplayResiduals()) {
				graphicDisplayMgmt.displayEdgeHeights(procedureOutput);
			}

		    
		    ///CALL DISPLAY_PISTON_ERROR 
		    
		    ///CALL DISPLAY_PISTON_RESID
		    
		    statusLogger.log("procedure.cph_calc_piston");
		
		    ///CALL DISPLAY_PH_PISTON_DELTAS()
		           
			///SHOW_PHASING_STATS_ASK_TO_PHASE(PCALC,STEP_PREDICTED,DUMMY,DUMMY,1,1)
		
		
			
			procedureExecutionState.setPercentComplete(98);

			
			/**********************************************/
			/// wait for ACS final colorstep cmds to complete
			/**********************************************/
			// TODO: wait for ACS final colorstep to complete
			
			// prepare to command primary
			boolean sendM1Command = procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_YES;
			if (procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_PROMPT) {
				
				// Display to user and ask if they want to command				
				//String actDeltaRmsText = MessageGenerator.generateMessage("calc.desiredm1cmds.html",
				//		procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltasRms(), 
				//		calcDesiredActDeltasRmsStdResult.getDesiredActDeltasRmsStd(),
				//		procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltasNoFmRms(),
				//		calcDesiredActDeltasRmsStdResult.getDesiredActDeltasNoFmRmsStd(),
				//		procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltasFmRms(),
				//		calcDesiredActDeltasRmsStdResult.getDesiredActDeltasFmRmsStd());
				
				//sendM1Command = userPromptMgmt.displayYesNoDialog("Primary Mirror Command", actDeltaRmsText  + "\n\n\nCommand Primary Mirror?");
			}

			
			// TODO: make sure logic in PRIMARY_PISTON is captured here
			///PRIMARY_PISTON(PCALC)
			
	
			// command ACS
			boolean commandsSent = false;
			if (sendM1Command) {
	
				try {
					// send out the commands
					//acsMgmt.commandActuatorDeltas(procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltas());
	
					statusLogger.log("pt.m1_act_cmd_success");
					logger.info("doSendActDeltaCommands: success");
					commandsSent = true;
					
					// take and store a snapshot
					int snapNum = acsMgmt.commandTakeSnap();
					procedureOutput.getProcedureDecisionLog().setM1SnapNumberAfter(snapNum);
					
				} catch (Exception e) {
					statusLogger.log("pt.m1_act_cmd_failed");
					logger.error(MessageGenerator.generateMessage("command.error"), e);
				}
	
			}
					
			// TODO: eventually replace this with an framework solution
			procedureOutput.getProcedureDecisionLog().setM1CmdsSent(commandsSent);
		
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(99);
		        Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete");
			}
	
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());
	
			procedureExecutionState.setPercentComplete(100);
				
		} catch (Throwable e) {
			
			
			/// attempt to put ACS state back to where it was when we began.
			/// only do this if we were actually commanding ACS in the first place
			
			/// On success:
			String txt1 = "procedure.cph.abort_recovered";
		
			///CALL FWARN_DIALOG(txt)
		
			/// On failure: 
			                              
			String txt2 = "procedure.cph.abort_not_recovered";
		    statusLogger.log(txt2);
		    ///CALL FWARN_DIALOG(txt)
			
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		/*
		 * getProcStats();
		 */

		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}
	
	
	// TODO: RESTORE_MIRROR is a subroutine we need to have here
	public void restoreMirror() {
		
	}
	
	
	

}
