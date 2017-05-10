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
import org.tmt.aps.peas.computation.model.NbAnalyzeStepSequenceResult;
import org.tmt.aps.peas.computation.model.StartupComputationsResult;
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
import org.tmt.aps.peas.instrument.model.CcdGain;
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

			// requirement: a set of predefined lists + advanced options to create a new one
			IterationListConfig iterationList = procedure.getProcedureConfigSet().getIterationListConfig();

			
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());
			int edgeCount = constantsCache.getPrimaryMirrorConstants().getEdgeAngle().length;

			int trialsTime = 70;
			
			/***********************************************/
			/*             Startup Computations            */
			/***********************************************/
			StartupComputationsResult startupComputationsResult = computationLibrary.startupComputations(
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getArcsecPerMeter(),
					physicalModel.getInstrument().getCcd().getCcdType().getPixelSize());

			
			for (int index=0; index<iterationList.getIterationValueList().getSize(); index++) {
				
				IterationValue iterationValue = iterationList.getIterationValueList().getIterationValue(index);
				
				Filter currentFilter = (Filter)iterationValue.getIterableEntity("Filter");
				ReferenceBeam currentRefBeam = (ReferenceBeam)iterationValue.getIterableEntity("ReferenceBeam");
				// set up procedureConfig each loop so that the create ref map auto subprocedures know how to get this info
				// TODO: the following statements need to be somewhere else ultimately
				procedureConfig.setFilter(currentFilter);
				procedureConfig.setFilterType(currentFilter.getFilterType());
				procedureConfig.setReferenceBeam(currentRefBeam);
				procedureExecutionMgmt.setupFindCentDefaults(procedure);
				
				
				RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
						procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
						currentFilter.getFilterType().getFilterTypeId(), -1);
				
				
				
				
	
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
								procedure.getTestNumber(), procedure.isOperational(), po);
	
						procedureExecutionMgmt.performProcedureStartup(subProcedure, null);
	
						procedureExecutionState.setPendingSubProcedure(subProcedure);
	
						// execute the subprocedure
						createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);
	
						currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
								.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
								currentFilter.getFilterType().getFilterTypeId(), -1);
	
					}
				}
	
				procedure.addRefBeamMap(currentRefMap);
			

				logger.debug("light source 1 = " + procedureConfig.getLightSource());
				
							
				/**********************************************/
				/*                 Ready Camera               */
				/**********************************************/			
				readyCameraSubflow.execute(procedure);
				
				procedureExecutionState.setCurrentOutputTarget(procedureOutput);
				
				logger.debug("light source 2 = " + procedureConfig.getLightSource());
	
				
				//****************************************//
				//   Start logic for Narrow Band Phasing  //
				//****************************************//
				
				// Begin the filter loop:
					
			    statusLogger.log("nph.loop_starting");

		
				
			    statusLogger.log("nph.current_filter", currentFilter.getFilterName());

				
				// set up integration time for this iteration
				if (procedureConfig.isLightSourceLed()) {
					float intTime = ((IntegrationTime)iterationValue.getIterableEntity("LedIntegrationTime")).getIntegrationTime();
					procedureConfig.setIntegrationTime(intTime);
					CcdGain currentCcdGain = (CcdGain)iterationValue.getIterableEntity("LedGain");
					procedureConfig.setCcdGainNumber(currentCcdGain.getGainNumber());

				} else {
					
					float intTime = ((IntegrationTime)iterationValue.getIterableEntity("StarIntegrationTime")).getIntegrationTime();
					procedureConfig.setIntegrationTime(intTime);
					CcdGain currentCcdGain = (CcdGain)iterationValue.getIterableEntity("StarGain");
					procedureConfig.setCcdGainNumber(currentCcdGain.getGainNumber());

				}

				
				int trialTimeDelta = (trialsTime/iterationList.getIterationValueList().getSize())*index;
				procedureExecutionState.setPercentComplete(trialTimeDelta);
				
				// setup the iteration output as the output target
				NarrowBandPhasingIterationOutput pio = new NarrowBandPhasingIterationOutput();
				procedureExecutionState.setCurrentOutputTarget(pio);
				procedureOutput.addIteration(pio);

				
				statusLogger.log("procedure.iteration", procedure.getProcedureType().getProcedureTypeName(), index+1, iterationList.getIterationValueList().getSize());
				
				procedureExecutionState.incrementIteration();
				
				//***********************************************//
				//       Set the Filter and Reference Beam       //
				//***********************************************//
				
				/*
				if (procedureConfig.isFrameFromCcd()) {
				
					statusLogger.log("camera.cmd.filter_wheel", currentFilter.getWheelPosition());
					Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());
	
					statusLogger.log("camera.cmd.ref_beam", currentRefBeam.getRefBeamNum());
					Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(currentRefBeam.getRefBeamNum());
	
					// wait for all commands to complete
					long waitPeriodMs = Utils.waitForComplete(filterCommandFuture, refBeamFuture);
					statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
					
				}
				*/

			    statusLogger.log("nph.calc_templates");

				
				//***********************************************//
				//   Make the Phasing Templates for this filter  //
				//***********************************************//

				MakeTemplateResult makeTemplateResult = computationLibrary.makeTemplate(
						constantsCache.getPhasingConstants().getPhasingSubimageFftSize(), 
						constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
						procedure.getProcedureConfigSet().getFindCentConfigInterior(),
						procedureConfig.getPupilMask(), currentFilter, 
						startupComputationsResult.getArcsecPerPixel());
				

				/**********************************************/
				/*        PupilRegistration Subflow           */
				/**********************************************/
				pupilRegistrationLoopSubflow.pupilRegistrationLoop(procedure, currentSession);
										
				FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
				CcdFrame ccdFrame = procedure.getLatestProcedureCcdFrame().getCcdFrame();

				//***********************************************//
				//                 nbAnalyzeFrame                //
				//***********************************************//
				
			    statusLogger.log("nph.analyze_frame");

				
	            NbAnalyzeFrameResult nbAnalyzeFrameResult = computationLibrary.nbAnalyzeFrame(ccdFrame.getCorrectedFrame(), findCentroidsResult.getCentroidList(), 
	        		   findCentroidsResult.getFoundSubimageFlags(), 
	        		   constantsCache.getPrimaryMirrorConstants().getEdgeAngle(), 
	        		   makeTemplateResult.getTemplateArray(),
	        		   constantsCache.getTelescopeConstants().getNumberOfSegments());
				

				//***********************************************//
				//            nbAnalyzeStepSequence              //
				//***********************************************//
		           
	            // analyze spots for nbPhasing 
				int[] nphMissingSpotsFlags = subimageDefList.getNphMissingSpotFlags(); 
							
			    statusLogger.log("nph.analyze_step_sequence");

				NbAnalyzeStepSequenceResult nbAnalyzeStepSequenceResult = computationLibrary.nbAnalyzeStepSequence(
						nbAnalyzeFrameResult.getBestCorrelationIndex(), nbAnalyzeFrameResult.getCoherenceOut(), 
						currentFilter.getWavelength() * Constants.NM_TO_MICRONS, 
						nphMissingSpotsFlags, 
						findCentroidsResult.getFindCentStatusList(), 
						constantsCache.getPrimaryMirrorConstants().getEdgeColor(), 
						constantsCache.getPhasingConstants().getPhasingTemplateCount(), 
						constantsCache.getTelescopeConstants().getNumberOfSegments(), 
						constantsCache.getPhasingConstants().getNbSingleFilterCoherenceThreshold());

				
			    /**********************************************/
				/*          CalculatePhasingStats             */
				/**********************************************/		
			    computationLibrary.calculatePhasingStats(
			    		nbAnalyzeStepSequenceResult.getRowFlagOut(), 
			    		nbAnalyzeStepSequenceResult.getRowFlagOut(), 
			    		nbAnalyzeStepSequenceResult.getStepTable(),
			    		new float[edgeCount]);

			    
			    /**********************************************/
				/*      Display Measured Edge Heights         */
				/**********************************************/

			    if (procedureConfig.isAutoDisplaySingleFilterEdgeHeights()) {
					graphicDisplayMgmt.displaySingleFilterEdgeHeights(pio, index);
				}

				

			}      // end Filter Loop


			// setup the procedure output as the output target
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			int filterCount = iterationList.getIterationValueList().getSize();
			
			
			//***********************************************//
			//             nbAnalyzeFilterSequence           //
			//***********************************************//

	        // Begin Filter Analysis:  Combine the results from multiple filters.

			float[][] stepTable = procedureOutput.getIterationValuesFor("NbAnalyzeStepSequenceResult", "StepTable", float[].class).toArray(new float[0][0]);
			
			int[][] rowFlagOutFilters = procedureOutput.getIterationValuesFor("NbAnalyzeStepSequenceResult", "RowFlagOut", int[].class).toArray(new int[0][0]);
			
			float[][] coherenceOutFilters = procedureOutput.getIterationValuesFor("NbAnalyzeFrameResult", "CoherenceOut", float[].class).toArray(new float[0][0]);
			float[][][] corrTable = new float[filterCount][1][edgeCount];
			
			for (int i=0; i<coherenceOutFilters.length; i++) {
				
				for (int j=0; j<edgeCount; j++) {
					
					corrTable[i][0][j] = coherenceOutFilters[i][j];	
				}
			}
			
			float filterWavelengthMicrons[] = new float[filterCount]; 

			String filterNames[] = new String[filterCount];
			
			for (int index=0; index<iterationList.getIterationValueList().getSize(); index++) {
								
				IterationValue iterationValue = iterationList.getIterationValueList().getIterationValue(index);
				
				Filter currentFilter = (Filter)iterationValue.getIterableEntity("Filter");

				filterWavelengthMicrons[index] = currentFilter.getWavelength() * Constants.NM_TO_MICRONS;
				
				filterNames[index] = currentFilter.getFilterName();
			}
			
			statusLogger.log("nph.analyze_filter_sequence");
			
			NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequenceResult = computationLibrary.nbAnalyzeFilterSequence(rowFlagOutFilters, stepTable, filterWavelengthMicrons, 
					procedure.getProcedureConfigSet().getNbFilterSeqConfig().getEdgeHeightSearchRange(),
					constantsCache.getPhasingConstants().getEdgeHeightSearchInterval());
	        
			//***********************************************//
			//                   nbActuators                 //
			//***********************************************//
	
	        // Calculate the actuators:

			// no incomplete mirror
			int[] colFlag = new int[constantsCache.getTelescopeConstants().getNumberOfSegments()];
			Arrays.fill(colFlag, 1);
			
			statusLogger.log("nph.calc_actuators");

			
			NbActuatorsResult nbActuatorsResult = computationLibrary.nbActuators(nbAnalyzeFilterSequenceResult.getNbStep(), 
					nbAnalyzeFilterSequenceResult.getRowFlagOut(), colFlag, 
		    		constantsCache.getPrimaryMirrorConstants().getSavePlusPiston(),
		    		constantsCache.getPrimaryMirrorConstants().getSaveMinusPiston(),
		    		constantsCache.getTelescopeConstants().getNumberOfSegments());

			
		    if (nbActuatorsResult.getConstrainedSegmentCount() != constantsCache.getTelescopeConstants().getNumberOfSegments()) {
		    	
			    userPromptMgmt.displayInfoDialog("Constrained Segment Warning", MessageGenerator.generateMessage("phasing.constrained_warning", 
			    		nbActuatorsResult.getConstrainedSegmentCount(), constantsCache.getTelescopeConstants().getNumberOfSegments()));
		    	
		    }


		    /**********************************************/
			/*                FixPistons                  */
			/**********************************************/		
		    FixPistonsResult fixPistonsResult = computationLibrary.fixPistons(
		    		constantsCache.getPrimaryMirrorConstants().getPrimaryActPos(), 
		    		nbActuatorsResult.getActCalc(),
		    		globalConfig.getMirrorListInt());

		    /**********************************************/
			/*          CalculatePhasingStats             */
			/**********************************************/		
		    computationLibrary.calculatePhasingStats(
		    		nbAnalyzeFilterSequenceResult.getRowFlagOut(), 
		    		nbAnalyzeFilterSequenceResult.getRowFlagOut(), 
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
			procedureExecutionState.setPercentComplete(trialsTime);
		      

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

		    
			procedureExecutionState.setPercentComplete(90);
						

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
	
				// send out the commands
				statusLogger.log("pt.m1_act_cmd_started");
				
				procedureExecutionMgmt.commandActuatorDeltas(procedureOutput.getCalcDesiredActCommandsResult().getDesiredActDeltas());
					
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
			
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		statusLogger.log("procedure.saving");
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}
	


}
