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
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.NarrowBandPhasingIterationOutput;
import org.tmt.aps.peas.procedure.model.NarrowBandPhasingProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

/**
 * Executor for the broadband Phasing procedure
 * @author smichaels
 */
@Singleton
@Startup
public class NarrowBandPhasingExecutor {

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

			NarrowBandPhasingProcedureOutput procedureOutput = (NarrowBandPhasingProcedureOutput) procedure.getProcedureOutput();

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

			procedure.setRefBeamMap(currentRefMap);

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
        			
			
			//****************************************//
			//   Start logic for Narrow Band Phasing  //
			//****************************************//
			
			// Begin the filter loop:

			// TESTONLY
			for (int iFilter=0; iFilter<2; iFilter++) {
				Filter currentFilter = new Filter();
				
			// requirement: a set of predefined lists + advanced options to create a new one
			//for (int iFilter=0; iFilter<procedureConfig.getNarrowBandPhasingFilterList().size(); iFilter++) {
				
			//	Filter currentFilter = procedureConfig.getNarrowBandFilterForIteration(iFilter);
			//	Filter currentRefBeam = procedureConfig.getNarrowBandRefBeamForIteration(iFilter);
				
			//	int trialTimeDelta = (trialsTime/procedureConfig.getNarrowBandPhasingFilterList().size())*iFilter + readyCameraTime;
			//	procedureExecutionState.setPercentComplete(trialTimeDelta);
				
			//	statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), iFilter+1, procedureConfig.getPhasingSteps());
				
				procedureExecutionState.incrementIteration();
				
				//***********************************************//
				//       Set the Filter and Reference Beam       //
				//***********************************************//
				
				// TBD - we need a way to get the filter and reference beam

				//***********************************************//
				//   Make the Phasing Templates for this filter  //
				//***********************************************//

				MakeTemplateResult makeTemplateResult = computationLibrary.makeTemplate(
						constantsCache.getPhasingConstants().getPhasingSubimageFftSize(), 
						constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
						procedure.getProcedureConfigSet().getFindCentConfigInterior(),
						procedureConfig.getPupilMask(), currentFilter);
				

		           // Write out the template in a format suitable for display.
		           // sm - not sure if we need this
		           // makeTableau(ret_val_makeTableau, template_c)

		           
						
		
				// setup the iteration output as the output target
				NarrowBandPhasingIterationOutput pio = new NarrowBandPhasingIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);
				
					
				/**********************************************/
				/*        PupilRegistration Subflow           */
				/**********************************************/
				pupilRegistrationLoopSubflow.pupilRegistrationLoop(procedure, currentSession);
										
				
				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
				CcdFrame ccdFrame = procedure.getLatestProcedureCcdFrame().getCcdFrame();

				//***********************************************//
				//                 nbAnalyzeFrame                //
				//***********************************************//
	            
	           // frame Analysis
	           computationLibrary.nbAnalyzeFrame(ccdFrame.getCorrectedFrame(), findCentroidsResult.getCentroidList(), 
	        		   findCentroidsResult.getFoundSubimageFlags(), 
	        		   constantsCache.getPrimaryMirrorConstants().getEdgeAngle(), 
	        		   makeTemplateResult.getTemplateArray());


				// Begin Phase Analysis:  Combine the results from multiple exposures.
		           
		        // TODO: what is the logic we need to use to set row_flag_analyze for a particular filter set of exposures
		        // does this depend on results from other filters?
				// TODO: the output iteration target is now a bit uncertain.. an embedded loop creates problems for which iteration
				// this is part of
				

				//***********************************************//
				//            nbAnalyzeStepSequence              //
				//***********************************************//
		           
		        // Determine the phases
				
				// nbTable, corrTable, stepTable, indexTable and xlamda0 are all passed in for a particular filter, and are derived from
				// information (arrays?) of one dimension larger
				// EACH OF THE FOLLOWING MUST BE RESOLVED:
				float[][][] nbTable = new float[0][0][0];
				float[][][] corrTable = new float[0][0][0];
				float xlambda0[] = new float[0];
				int[] rowFlagIn = new int[0]; 
				int[] edgeColor = new int[0]; 
				int templateCount = 0;
				
				
				computationLibrary.nbAnalyzeStepSequence(nbTable[iFilter], corrTable[iFilter], xlambda0[iFilter], rowFlagIn, edgeColor, 
						templateCount, procedureConfig.getNumberOfTrials());

		        // TODO: account for this logic
		        // Combine ROW_FLAG_OUT from multiple filters:
		        // good_edge_flag(:) = good_edge_flag(:) * row_flag_out(:)

				
				
			}      // end Filter Loop


			//***********************************************//
			//             nbAnalyzeFilterSequence           //
			//***********************************************//

	        // Begin Filter Analysis:  Combine the results from multiple filters.

			// EACH OF THE FOLLOWING MUST BE RESOLVED:
			float[][] stepTable = new float[0][0];
			float[][][] corrTable = new float[0][0][0];
			float xlambda[] = new float[0]; // why not xlambda0?
			int[] rowFlagIn = new int[0]; 
			float range = 0.0f;
			float rInt = 0.0f;

			computationLibrary.nbAnalyzeFilterSequence(rowFlagIn, stepTable, corrTable, xlambda, range, rInt);
	        
			//***********************************************//
			//                   nbActuators                 //
			//***********************************************//

	        // Calculate the actuators:

			// EACH OF THE FOLLOWING MUST BE RESOLVED:
			float[] nbStep = new float[0]; 
			int[] rowFlag = new int[0];
			int[] colFlag = new int[0];
			float[][] acsa = new float[0][0];
			
			
			computationLibrary.nbActuators(nbStep, rowFlag, colFlag, constantsCache.getPrimaryMirrorConstants().getPrimaryActPos(), acsa);

	
	        // TODO: is this data to be displayed and where

	        // print *,' No. constrained segments      = ', constrainedSegmentCount
	        // print *,' Number of good edges          = ', edge_count
	        // print *,' RMS edge residual             = ', edge_res_rms
	        // print *,' Max. edge residual            = ', edge_res_max
	        // print *,' Plane removed rms             = ', act_noplane_rms

			
	        // TODO: need to fix this
			procedureExecutionState.setPercentComplete(trialsTime + readyCameraTime);


		    /**********************************************/
			/*          CalculatePhasingStats             */
			/**********************************************/	
	        // TODO: do we need to do something like this?
		    //computationLibrary.calculatePhasingStats(
		    //		bbAnalyzeSequenceResult.getRowFlagIn(), 
		    //		bbAnalyzeSequenceResult.getRowFlagOut(), 
		    //		bbAnalyzeSequenceResult.getStepCorr(),
		    //		bbAnalyzeSequenceResult.getResid());
		      

		    /**********************************************/
			/*      Display Measured Edge Heights         */
			/**********************************************/	
		    // TODO: do we keep this?
		    if (procedureConfig.isAutoDisplayEdgeHeights()) {
				graphicDisplayMgmt.displayEdgeHeights(procedureOutput);
			}

		    /**********************************************/
			/*      Display Residual Edge Heights         */
			/**********************************************/
		    // TODO: do we keep this?
		    if (procedureConfig.isAutoDisplayResiduals()) {
				graphicDisplayMgmt.displayEdgeResiduals(procedureOutput);
			}

		    /**********************************************/
			/*          Display Piston Deltas             */
			/**********************************************/	
		    // TODO: do we keep this?
			if (procedureConfig.isAutoDisplayActuatorDeltas()) {
				graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);
			}

		    statusLogger.log("procedure.cph.algorithm_complete");

		    
			procedureExecutionState.setPercentComplete(98);
						

			// TODO: is this set of output correct for nph?
		    statusLogger.log("calc.phasing_summary",
					procedureOutput.getFixPistonsResult().getActRms(), 
					procedureOutput.getPhasingStatsResult().getResidualEdgeErrorRss(), 
					procedureOutput.getPhasingStatsResult().getGoodEdgeCount());

			
			// prepare to command primary
			boolean sendM1Command = procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_YES;
			if (procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_PROMPT) {
								
				// Display to user and ask if they want to command	
				// TODO: fix this to be the outputs and sources we want for nph
				String phasingSummaryText = MessageGenerator.generateMessage("calc.phasing_summary",
						procedureOutput.getFixPistonsResult().getActRms(), 
						procedureOutput.getPhasingStatsResult().getResidualEdgeErrorRss(), 
						procedureOutput.getPhasingStatsResult().getGoodEdgeCount());
				
				sendM1Command = userPromptMgmt.displayYesNoDialog("Primary Mirror Command", phasingSummaryText  + "\n\n\nCommand Primary Mirror?");
			}
			
	
			// command ACS
			boolean commandsSent = false;
			if (sendM1Command) {
	
				try {
					// send out the commands
					statusLogger.log("pt.m1_act_cmd_started");
					
					acsMgmt.commandActuatorDeltas(procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltas());
						
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
			
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		statusLogger.log("procedure.saving");
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}
	


}
