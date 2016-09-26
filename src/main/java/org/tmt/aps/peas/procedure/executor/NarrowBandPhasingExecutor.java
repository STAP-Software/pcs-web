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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.computation.model.NbActuatorsResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFilterSequenceResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.IntegrationTime;
import org.tmt.aps.peas.config.model.IterationListConfig;
import org.tmt.aps.peas.config.model.IterationValue;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
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
        			
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			//****************************************//
			//   Start logic for Narrow Band Phasing  //
			//****************************************//
			
			// Begin the filter loop:

				
			// requirement: a set of predefined lists + advanced options to create a new one
			IterationListConfig iterationList = procedure.getProcedureConfigSet().getIterationListConfig();
			
			for (int index=0; index<iterationList.getIterationValueList().getSize(); index++) {
				
				IterationValue iterationValue = iterationList.getIterationValueList().getIterationValue(index);
				
				Filter currentFilter = (Filter)iterationValue.getIterableEntity("Filter");
				ReferenceBeam currentRefBeam = (ReferenceBeam)iterationValue.getIterableEntity("ReferenceBeam");
				
				// set up integration time for this iteration
				if (procedureConfig.isLightSourceLed()) {
					float intTime = ((IntegrationTime)iterationValue.getIterableEntity("LedIntegrationTime")).getIntegrationTime();
					procedureConfig.setIntegrationTime(intTime);
				} else {
					
					float intTime = ((IntegrationTime)iterationValue.getIterableEntity("StarIntegrationTime")).getIntegrationTime();
					procedureConfig.setIntegrationTime(intTime);
				}

				int trialTimeDelta = (trialsTime/iterationList.getIterationValueList().getSize())*index + readyCameraTime;
				procedureExecutionState.setPercentComplete(trialTimeDelta);
				
				// setup the iteration output as the output target
				NarrowBandPhasingIterationOutput pio = new NarrowBandPhasingIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);

				
				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), index+1, procedureConfig.getPhasingSteps());
				
				procedureExecutionState.incrementIteration();
				
				//***********************************************//
				//       Set the Filter and Reference Beam       //
				//***********************************************//
				
				statusLogger.log("camera.cmd.filter_wheel", currentFilter.getWheelPosition());
				Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());

				statusLogger.log("camera.cmd.ref_beam", currentRefBeam.getRefBeamNum());
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(currentRefBeam.getRefBeamNum());

				// wait for all commands to complete
				long waitPeriodMs = Utils.waitForComplete(filterCommandFuture, refBeamFuture);
				statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);


				//***********************************************//
				//   Make the Phasing Templates for this filter  //
				//***********************************************//

				MakeTemplateResult makeTemplateResult = computationLibrary.makeTemplate(
						constantsCache.getPhasingConstants().getPhasingSubimageFftSize(currentFilter.getFilterType()), 
						constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
						procedure.getProcedureConfigSet().getFindCentConfigInterior(),
						procedureConfig.getPupilMask(), currentFilter);
				

		           // Write out the template in a format suitable for display.
		           // sm - not sure if we need this
		           // makeTableau(ret_val_makeTableau, template_c)

		           
						
		
				
					
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
				
	           NbAnalyzeFrameResult nbAnalyzeFrameResult = computationLibrary.nbAnalyzeFrame(ccdFrame.getCorrectedFrame(), findCentroidsResult.getCentroidList(), 
	        		   findCentroidsResult.getFoundSubimageFlags(), 
	        		   constantsCache.getPrimaryMirrorConstants().getEdgeAngle(), 
	        		   makeTemplateResult.getTemplateArray(),
	        		   constantsCache.getTelescopeConstants().getNumberOfSegments());
				

				// Begin Phase Analysis:  Combine the results from multiple exposures.
		           
		        // TODO: what is the logic we need to use to set row_flag_analyze for a particular filter set of exposures
		        // does this depend on results from other filters?
				// TODO: the output iteration target is now a bit uncertain.. an embedded loop creates problems for which iteration
				// this is part of
				

				//***********************************************//
				//            nbAnalyzeStepSequence              //
				//***********************************************//
		           
		        // Determine the phases
	          
	            // analyze spots for nbPhasing 
				int[] nphMissingSpotsFlags = subimageDefList.getNphMissingSpotFlags(); 
							
				computationLibrary.nbAnalyzeStepSequence(nbAnalyzeFrameResult.getBestCorrelationIndex(), nbAnalyzeFrameResult.getCoherenceOut(), currentFilter.getWavelength(), nphMissingSpotsFlags, 
						findCentroidsResult.getFoundSubimageFlags(), constantsCache.getPrimaryMirrorConstants().getEdgeColor(), 
						constantsCache.getPhasingConstants().getPhasingTemplateCount(), constantsCache.getTelescopeConstants().getNumberOfSegments());


			}      // end Filter Loop


			// setup the iteration output as the output target

			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			int edgeCount = constantsCache.getPrimaryMirrorConstants().getEdgeAngle().length;
			int filterCount = iterationList.getIterationValueList().getSize();
			
			// calculate the Intersection of all row_flag_out for each filter and put that in row_flag_in for the next computation
			int[][] rowFlagOutFilters = procedureOutput.getIterationValuesFor("NbAnalyzeStepSequenceResult", "RowFlagOut", int[].class).toArray(new int[0][0]);
			
			int[] rowFlagIn = new int[edgeCount];
			Arrays.fill(rowFlagIn, 1);
			
			for (int[] rowFlagOutFilter : rowFlagOutFilters) {
				for (int i=0; i<edgeCount; i++) {
					rowFlagIn[i] = rowFlagIn[i] * rowFlagOutFilter[i];
				}
			}
			
			//***********************************************//
			//             nbAnalyzeFilterSequence           //
			//***********************************************//

	        // Begin Filter Analysis:  Combine the results from multiple filters.

			float[][] stepTable = procedureOutput.getIterationValuesFor("NbAnalyzeStepSequenceResult", "StepTable", float[].class).toArray(new float[0][0]);
			
			float[][] coherenceOutFilters = procedureOutput.getIterationValuesFor("NbAnalyzeFrameResult", "CoherenceOut", float[].class).toArray(new float[0][0]);
			float[][][] corrTable = new float[filterCount][1][edgeCount];
			
			for (int i=0; i<coherenceOutFilters.length; i++) {
				
				for (int j=0; j<edgeCount; j++) {
					
					corrTable[i][0][j] = coherenceOutFilters[i][j];	
				}
			}
			
			float xlambda[] = new float[filterCount]; 

			
			for (int index=0; index<iterationList.getIterationValueList().getSize(); index++) {
								
				IterationValue iterationValue = iterationList.getIterationValueList().getIterationValue(index);
				
				Filter currentFilter = (Filter)iterationValue.getIterableEntity(Filter.class.getName());

				xlambda[index] = currentFilter.getWavelength();
				
			}
			
			NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequenceResult = computationLibrary.nbAnalyzeFilterSequence(rowFlagIn, stepTable, corrTable, xlambda, 
					constantsCache.getPhasingConstants().getEdgeHeightSearchRange(filterCount), 
					constantsCache.getPhasingConstants().getEdgeHeightSearchInterval());
	        
			//***********************************************//
			//                   nbActuators                 //
			//***********************************************//
	
	        // Calculate the actuators:

			// no incomplete mirror
			int[] colFlag = new int[constantsCache.getTelescopeConstants().getNumberOfSegments()];
			Arrays.fill(colFlag, 1);
			
			
			NbActuatorsResult nbActuatorsResult = computationLibrary.nbActuators(nbAnalyzeFilterSequenceResult.getNbStep(), rowFlagIn, colFlag, 
		    		constantsCache.getPrimaryMirrorConstants().getSavePlusPiston(),
		    		constantsCache.getPrimaryMirrorConstants().getSaveMinusPiston(),
		    		constantsCache.getTelescopeConstants().getNumberOfSegments());


		    /**********************************************/
			/*                FixPistons                  */
			/**********************************************/		
		    FixPistonsResult fixPistonsResult = computationLibrary.fixPistons(
		    		constantsCache.getPrimaryMirrorConstants().getPrimaryActPos(), 
		    		nbActuatorsResult.getActCalc());

		    /**********************************************/
			/*          CalculatePhasingStats             */
			/**********************************************/		
		    computationLibrary.calculatePhasingStats(
		    		rowFlagIn, 
		    		rowFlagIn, 
		    		nbAnalyzeFilterSequenceResult.getNbStep(),
		    		nbActuatorsResult.getResid());
		      
		    /**********************************************/
			/*       CalculateDesiredActCommands          */
			/**********************************************/		
		    computationLibrary.fixPistonsToDesiredActs(fixPistonsResult);
		    
		    
	        // TODO: is this data to be displayed and where

	        // print *,' No. constrained segments      = ', constrainedSegmentCount
	        // print *,' Number of good edges          = ', edge_count
	        // print *,' RMS edge residual             = ', edge_res_rms
	        // print *,' Max. edge residual            = ', edge_res_max
	        // print *,' Plane removed rms             = ', act_noplane_rms

			
	        // TODO: need to fix this
			procedureExecutionState.setPercentComplete(trialsTime + readyCameraTime);
		      

		    /**********************************************/
			/*      Display Measured Edge Heights         */
			/**********************************************/	
		    // TODO: do we keep this - yes
		    if (procedureConfig.isAutoDisplayEdgeHeights()) {
				graphicDisplayMgmt.displayEdgeHeights(procedureOutput);
			}

		    /**********************************************/
			/*      Display Residual Edge Heights         */
			/**********************************************/
		    // TODO: do we keep this - yes
		    if (procedureConfig.isAutoDisplayResiduals()) {
				graphicDisplayMgmt.displayEdgeResiduals(procedureOutput);
			}

		    /**********************************************/
			/*          Display Piston Deltas             */
			/**********************************************/	
		    // TODO: do we keep this - yes
			if (procedureConfig.isAutoDisplayActuatorDeltas()) {
				graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);
			}

		    statusLogger.log("procedure.cph.algorithm_complete");

		    
			procedureExecutionState.setPercentComplete(98);
						

			// TODO: is this set of output correct for nph?
			// FIXME: edge residual RSS is not here, we have the RMS instead
		    statusLogger.log("calc.phasing_summary",
					procedureOutput.getFixPistonsResult().getActRms(), 
					procedureOutput.getPhasingStatsResult().getResidualEdgeErrorRss(), 
					procedureOutput.getPhasingStatsResult().getGoodEdgeCount());

			
			// prepare to command primary
			boolean sendM1Command = procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_YES;
			if (procedureConfig.getAutoSendActuatorCmds() == Constants.AUTO_SEND_ACT_DELTAS_PROMPT) {
								
				// Display to user and ask if they want to command	
				// TODO: fix this to be the outputs and sources we want for nph
				// FIXME: edge residual RSS is not here, we have the RMS instead
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
