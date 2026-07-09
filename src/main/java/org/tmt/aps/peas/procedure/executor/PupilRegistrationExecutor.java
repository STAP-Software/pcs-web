/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
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
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.model.PupilRegistrationIterationOutput;
import org.tmt.aps.peas.procedure.model.PupilRegistrationProcedureOutput;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

/**
 * Executor for the Pupil Registration procedure
 * @author smichaels
 */
@Singleton
@Startup
public class PupilRegistrationExecutor {

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
	private CenterTelescopeSubflow centerTelescopeSubflow;
	@EJB
	private ReadyCameraSubflow readyCameraSubflow;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private SubimageDefCache subimageDefCache;
	@EJB
	private CreateRefMapExecutor createRefMapExecutor;
	@EJB
	private PupilRegistrationSubflow pupilRegistrationSubflow;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("PupilRegistrationExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PupilRegistrationExecutor::testMethod::");
		return null;
	}

	/**
	 * Executor method: this method is the Pupil Registration flow
	 */
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PupilRegistrationExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			@SuppressWarnings("unused")
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PupilRegistrationProcedureOutput procedureOutput = (PupilRegistrationProcedureOutput) procedure.getProcedureOutput();
			// V3.0 - save it in procedureExecutionState to avoid transient Procedure fields not persisting outside transactions
			procedureExecutionState.setCurrentProcedureOutput(procedureOutput);

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (currentRefMap == null) {

				// pupil registration only cares if we have a ref beam map for the filter/pupil mask/instrument, not if it is recent, etc
				// this is because pupil registration is interested in intensities, not offsets
				
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
			
			procedureExecutionState.setPercentComplete(10);

			
			procedureExecutionState.setCurrentRefMap(currentRefMap);

			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCameraSubflow.execute(procedure);
			
			procedureExecutionState.setCurrentOutputTarget(procedureOutput);

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			procedureExecutionState.setPercentComplete(20);

			/***********************************************/
			/*             Startup Computations            */
			/***********************************************/
			@SuppressWarnings("unused")
			StartupComputationsResult startupComputationsResult = computationLibrary.startupComputations(
					procedureConfig.getPupilMask().getArcsecPerMeter(),
					physicalModel.getInstrument().getCcd().getCcdType().getPixelSize());

			
			// Set up the only iteration as the current output target
			PupilRegistrationIterationOutput pio = new PupilRegistrationIterationOutput();
			procedureExecutionState.setCurrentOutputTarget(pio);
			procedureOutput.addIteration(pio);

			
			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			Future<Exception> dcsFuture = centerTelescopeSubflow.centerTelescope(procedure, currentSession);

			procedureExecutionState.setPercentComplete(30);

			CentroidOffsetsResult centroidOffsetsResult= pio.getCentroidOffsetsResult();
	
			procedureExecutionState.setPercentComplete(40);
			
			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/
			ProcedureCcdFrame procedureCcdFrame = frameMgmt.findLatestProcedureCcdFrame(procedure);
			FindCentroidsResult findCentroidsResult = procedureCcdFrame.getCentroidMap().getFindCentroidsResult();
			Integer sufsGroup = procedure.getProcedureType().isSufs() ? procedureConfig.getSufsGroup() : null;

			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), sufsGroup);

			
			@SuppressWarnings("unused")
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			procedureExecutionState.setPercentComplete(50);

			/*****************************************************/
			/*                                                   */
			/*****************************************************/
			
			//need to get centerSpots 
			@SuppressWarnings("unused")
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
						
			if (procedureConfig.getPupilMaskType().isPupilMaskTypeFs()) {
			
				// spots that can be used (found without errors and should be used for analysis)
				@SuppressWarnings("unused")
				int[] good_spots = 	computationLibrary.goodCentroidsFound(subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			}
			
			pio.getProcedureIterationDecisionLog().setTelescopeMoved(false);

			procedureExecutionState.setPercentComplete(70);
			
			
			/**********************************************/
			/*        PupilRegistration Subflow           */
			/**********************************************/			
			pupilRegistrationSubflow.execute(procedure, findCentroidsResult);

			
			/**********************************************/
			/*        Wait for DCS to complete            */
			/**********************************************/			
			long waitPeriodMs = Utils.waitForComplete(dcsFuture);
			if (dcsFuture != null) {
				if (dcsFuture.get() == null) {
					statusLogger.log("dcs.cmd_completed", waitPeriodMs/1000.0);
				} else {
					statusLogger.log("telescope.cmd.failed");
					logger.error(MessageGenerator.generateMessage("command.error"), dcsFuture.get());
				}		
			}
			
			// fill the procedure output
			procedureOutput.addPupilRegistrationIterationOutput(pio);
			
			// close shutter
			// FIXME: remove this call when all shutter usage is deprecated
			//cameraMgmt.commandCcdShutterState(CameraCommand.CLOSED);

				
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);


			
		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}

		statusLogger.log("procedure.saving");		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
