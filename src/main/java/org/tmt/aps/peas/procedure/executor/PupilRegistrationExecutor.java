/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltIterationOutput;
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
	private ImageProcessor imageProcessor;
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
	private CenterTelescopeFlow centerTelescopeFlow;
	@EJB
	private ReadyCamera readyCamera;
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

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PupilRegistrationExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PupilRegistrationProcedureOutput procedureOutput = (PupilRegistrationProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");			
			
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (currentRefMap == null) {

				// pupil registration only cares if we have a ref beam map for the filter/pupil mask/instrument, not if it is recent, etc
				// this is because pupil registration is interested in intensities, not offsets
				
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
			
			procedureExecutionState.setPercentComplete(10);

			
			procedure.setRefBeamMap(currentRefMap);

			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCamera.execute(procedure);

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			procedureExecutionState.setPercentComplete(20);

			
			// Set up the only iteration as the current output target
			// TODO: hide these functions
			PupilRegistrationIterationOutput pio = new PupilRegistrationIterationOutput();
			procedureExecutionState.setCurrentOutputTarget(pio);
			procedureOutput.addIteration(pio);

			
			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			centerTelescopeFlow.centerTelescope(procedure, currentSession);

			procedureExecutionState.setPercentComplete(30);

			CentroidOffsetsResult centroidOffsetsResult= pio.getCentroidOffsetsResult();
	
			procedureExecutionState.setPercentComplete(40);
			
			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/
			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			procedureExecutionState.setPercentComplete(50);

			/*****************************************************/
			/*              passiveTiltScaleError                */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
						
			if (procedureConfig.getPupilMaskType().isPupilMaskTypeFs()) {
			
				computationLibrary.fineScreenScaleErrorResult(centroidOffsetsResult.getCcdCentroidOffsets(),
					centerSpots, subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			}
			
			// TODO: handle with framework
			pio.setTelescopeMoved(false);

			procedureExecutionState.setPercentComplete(70);
			
			/*****************************************************/
			/*            calcPupilRegErrorDefaults              */
			/*****************************************************/
					
			PupilRegErrorResult pupilRegErrorResult = computationLibrary.calculatePupilRegError(
				procedure.getProcedureConfigSet().getPupilRegErrorConfig(), 
				procedure.getLatestProcedureCcdFrame().getCentroidMap(), 
				procedureConfig.getPupilMaskType().getNumSpots(), 
				constantsCache.getPrimaryMirrorSegmentConstants().getPeripheralSpotPerp(),
				constantsCache.getPrimaryMirrorSegmentConstants().getPeripheralSpotParallel(),
				constantsCache.getPrimaryMirrorSegmentConstants().getPeripheralSpotTheta(),
				constantsCache.getPrimaryMirrorConstants().getaHex(), 
				procedureConfig.getPupilMask().getSpotDiamPeripheral(), 
				subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());
				
			
				// log values 
				statusLogger.log("calc.pupil_reg_error", new Float(pupilRegErrorResult.getRegErrorX() * 1000.0f), new Float(pupilRegErrorResult.getRegErrorY() * 1000.0f), 
						new Float(pupilRegErrorResult.getRegErrorPhi() / Constants.DEG2RAD));
	
				procedureExecutionState.setPercentComplete(80);

				
			/*****************************************************/
			/*           determine fine/coarse PR Commands       */
			/*****************************************************/
			
			// logic that given automode preferences and (potentially) user input to determine if commands are to be sent, 
			// what the commands are and which mechanisms to move.
			
			// prompt user with registration error values
			FloatPoint regErrorMm = new FloatPoint(pupilRegErrorResult.getRegErrorX() * 1000.0f, pupilRegErrorResult.getRegErrorY() * 1000.0f);
				
			String text = MessageGenerator.generateMessage("calc.pupil_reg_error", regErrorMm.x, regErrorMm.y);
				
			boolean centerPupil = false;
			if (procedureConfig.getAutoCenterPupil() == Constants.AUTO_CENTER_PUPIL_PROMPT) {
				// ask the user
				
				// include the PR error result in the dialog
				centerPupil = userPromptMgmt.displayYesNoDialog(text + "\n\nSend commands to correct pupil registration errors?");
				
			} else if (procedureConfig.getAutoCenterPupil() == Constants.AUTO_CENTER_PUPIL_YES) {
				
				// only move if error more than centerPupilThresh
				float centerPupilThresh = procedure.getProcedureConfigSet().getPupilRegErrorConfig().getCenterPupilThresh();
				if (Math.abs(regErrorMm.x) > centerPupilThresh || Math.abs(regErrorMm.y) > centerPupilThresh) {
				
					centerPupil = procedureConfig.getAutoCenterPupil() == Constants.AUTO_CENTER_PUPIL_YES;
				}
			} 
			
			int desiredCenterPupilMech = 0;
			if (centerPupil) {
				if (procedureConfig.getAutoCenterPupilMechanism() == Constants.AUTO_CENTER_PUPIL_MECH_PROMPT) {
					
					String[] choices = {"Fine", "Coarse", "AutoDetermine"};
					int[] values = {Constants.AUTO_CENTER_PUPIL_MECH_FINE, Constants.AUTO_CENTER_PUPIL_MECH_COARSE, Constants.AUTO_CENTER_PUPIL_MECH_AUTO};
					
					desiredCenterPupilMech = userPromptMgmt.displayGenericMultiChoiceDialog(text + "\n\nChoose mechanism to center pupil:", 
							choices, values);
								
				} else {
					desiredCenterPupilMech = procedureConfig.getAutoCenterPupilMechanism();
				}					
			}

			procedureExecutionState.setPercentComplete(85);

			CoarseTiltMirror coarseMirror = physicalModel.getInstrument().getCamera().getCoarseTiltMirror();
			FineTiltMirror fineMirror = physicalModel.getInstrument().getCamera().getFineTiltMirror();

			// need to know current positions which is available in the coarse and fine mirror objects
			CalcPrCommandsResult calcPrCommandsResult = computationLibrary.calcPrCommands(centerPupil, desiredCenterPupilMech, pupilRegErrorResult, 
					procedure.getProcedureConfigSet().getPupilRegErrorConfig(), fineMirror, coarseMirror);

			
			// fill the procedure output
			procedureOutput.addPupilRegistrationIterationOutput(pio);

			
			procedureExecutionState.setPercentComplete(90);
			
			/*****************************************************/
			/*         move fine, coarse, both, or none          */
			/*****************************************************/
			
			if (calcPrCommandsResult.isOffloaded()) {
				statusLogger.log("calc.pupil_reg.cmd_offloaded");
			}
			
			Future<Point> coarseMirrorCommandFuture = null;
			Future<Point> fineMirrorCommandFuture = null;
			
			if (calcPrCommandsResult.hasCoarseMirrorCommands()) {
				// always command the coarse mirror to setup values at the start of all procedures
				coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(calcPrCommandsResult.getCoarseMirrorCommands());
				
				statusLogger.log("camera.cmd.coarse_mirror_deltas", calcPrCommandsResult.getCoarseMirrorDeltas().x, calcPrCommandsResult.getCoarseMirrorDeltas().y);
				statusLogger.log("camera.cmd.coarse_mirror", calcPrCommandsResult.getCoarseMirrorCommands().x, calcPrCommandsResult.getCoarseMirrorCommands().y);

			} 
			if (calcPrCommandsResult.hasFineMirrorCommands()) {
				fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(calcPrCommandsResult.getFineMirrorCommands());
				statusLogger.log("camera.cmd.fine_mirror_deltas", calcPrCommandsResult.getFineMirrorDeltas().x, calcPrCommandsResult.getFineMirrorDeltas().y);
				statusLogger.log("camera.cmd.fine_mirror", calcPrCommandsResult.getFineMirrorCommands().x, calcPrCommandsResult.getFineMirrorCommands().y);
			}
			
			Utils.waitForComplete(coarseMirrorCommandFuture, fineMirrorCommandFuture);
			
			if (calcPrCommandsResult.hasCoarseMirrorCommands() || calcPrCommandsResult.hasFineMirrorCommands()) {
				statusLogger.log("camera.cmd.complete");
			}
			
								
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);


			
		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}

		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
