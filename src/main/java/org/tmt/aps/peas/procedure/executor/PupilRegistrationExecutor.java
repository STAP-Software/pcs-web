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
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.CalcPrCommandsResult;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
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
	private ComputationContext computationContext;
	@EJB
	private CenterTelescopeCalc centerTelescopeCalc;
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

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PupilRegistrationProcedureOutput procedureOutput = (PupilRegistrationProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");			
			
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (currentRefMap == null) {

				CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
				Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
						ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession.getSessionId(), 
						procedure.getTestNumber(), po);

				procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

				procedureExecutionState.setPendingSubProcedure(subProcedure);

				// execute the subprocedure
				createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

				currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
						.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
						.getFilterTypeId(), -1);

			}
			
			
			procedure.setRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCamera.execute(procedure);

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			CenterTelescopeCalcResult centerTelescopeCalcResult = centerTelescopeCalc.centerTelescope(procedure, currentSession);
			
			CentroidOffsetsResult centroidOffsetsResult= centerTelescopeCalcResult.getCentroidOffsetsResult();
			FloatPoint deltaAzEl = centerTelescopeCalcResult.getDeltaAzEl();
	
			
			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/
			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			/*****************************************************/
			/*              passiveTiltScaleError                */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
			
			ScaleError scaleError = computationLibrary.fineScreenScaleError(centroidOffsetsResult.getCcdCentroidOffsets(),
					centerSpots, subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			
			PupilRegistrationIterationOutput pio = new PupilRegistrationIterationOutput();
			procedureOutput.addIteration(pio);
			pio.setIteration(0);
			pio.setDeltaAzEl(deltaAzEl);

			pio.setCcdCentroidOffsets(centroidOffsetsResult.getCcdCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setCartesianCentroidOffsets(centroidOffsetsResult.getCartesianCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setScaleError(scaleError.getScaleError());

			pio.setMaxSpotNum(centroidStatsResult.getMaxSpotNum());
			pio.setMaxOffset(centroidStatsResult.getMaxOffset());
			pio.setRmsOffset(centroidStatsResult.getRmsOffset());

			pio.setEnclosedEnergy50(centroidStatsResult.getEnclosedEnergy50());
			pio.setEnclosedEnergy80(centroidStatsResult.getEnclosedEnergy80());

			pio.setScaleError(scaleError.getScaleError());
			pio.setSlopeError(scaleError.getSlopeError());

			pio.setTelescopeMoved(false);

			// fill the output - many of these are copied from the one iteration
			procedureOutput.setCcdCentroidOffsets(pio.getCcdCentroidOffsets());
			procedureOutput.setCartesianCentroidOffsets(pio.getCartesianCentroidOffsets());

			procedureOutput.setScaleError(pio.getScaleError());

			procedureOutput.setMaxSpotNum(pio.getMaxSpotNum());
			procedureOutput.setMaxOffset(pio.getMaxOffset());
			procedureOutput.setRmsOffset(pio.getRmsOffset());

			procedureOutput.setEnclosedEnergy50(pio.getEnclosedEnergy50());
			procedureOutput.setEnclosedEnergy80(pio.getEnclosedEnergy80());

			procedureOutput.setScaleError(pio.getScaleError());
			procedureOutput.setSlopeError(pio.getSlopeError());

			procedureOutput.setRotationFromRefBeam(centroidOffsetsResult.getImageRotation());
			procedureOutput.setScaleChangeFromRefBeam(centroidOffsetsResult.getImageScale());
			procedureOutput.setTranslationFromRefBeam(centroidOffsetsResult.getImageTranslation());
			
			

			procedureExecutionState.setPercentComplete(80);
			
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
				procedureConfig.getPupilMask().getSpotDiameter(), 
				subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());
				
			
				// log values 
				statusLogger.log("calc.pupil_reg_error", new Float(pupilRegErrorResult.getRegErrorX() * 1000.0f), new Float(pupilRegErrorResult.getRegErrorY() * 1000.0f), 
						new Float(pupilRegErrorResult.getRegErrorPhi() / Constants.DEG2RAD));
				
				
				BeanUtils.copyProperties(pio, pupilRegErrorResult);
				BeanUtils.copyProperties(procedureOutput, pupilRegErrorResult);
			
						
			/*****************************************************/
			/*           determine fine/coarse PR Commands       */
			/*****************************************************/
			
			// logic that given automode preferences and (potentially) user input to determine if commands are to be sent, 
			// what the commands are and which mechanisms to move.
			
			// prompt user with registration error values
			
			boolean centerPupil = false;
			if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_COMMAND_TILT_PLATE_PROMPT) {
				// ask the user
				
				// TODO: include the PR error result in the dialog
				centerPupil = userPromptMgmt.displayYesNoDialog("\nCommand Tilt Plates to correct pupil registration errors?");
				
			} else {
				centerPupil = procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_COMMAND_TILT_PLATE_YES;
			}
			
			int desiredCenterPupilMech = 0;
			if (procedureConfig.getAutoCenterPupilMechanism() == Constants.AUTO_CENTER_PUPIL_MECH_PROMPT) {
				
				String[] choices = {"Fine", "Coarse", "AutoDetermine"};
				int[] values = {Constants.AUTO_CENTER_PUPIL_MECH_FINE, Constants.AUTO_CENTER_PUPIL_MECH_COARSE, Constants.AUTO_CENTER_PUPIL_MECH_AUTO};
				desiredCenterPupilMech = userPromptMgmt.displayGenericMultiChoiceDialog("\nChoose mechanism to center pupil:", choices, values);
				
			} else {
				desiredCenterPupilMech = procedureConfig.getAutoCenterPupilMechanism();
			}
			
			// TODO: these should be configuration somewhere
			float largeMoveThreshold = 0.0f; 
			float fineTiltPositionOffloadLimit = 0.0f;
						
			Point initialCoarsePosition = physicalModel.getInstrument().getCamera().getCoarseTiltMirror().getCurrentPosition();
			Point initialFinePosition = physicalModel.getInstrument().getCamera().getFineTiltMirror().getCurrentPosition();

			
			
			CalcPrCommandsResult calcPrCommandsResult = computationLibrary.calcPrCommands(centerPupil, desiredCenterPupilMech, pupilRegErrorResult, 
			initialFinePosition, initialCoarsePosition, largeMoveThreshold, fineTiltPositionOffloadLimit);
			
			
			// this will use calcuations:
			// calcFineTiltPRCommands()
			// calcCoarseTiltPRCommands()

			
			/*****************************************************/
			/*         move fine, coarse, both, or none          */
			/*****************************************************/
			
			Future<Point> coarseMirrorCommandFuture = null;
			Future<Point> fineMirrorCommandFuture = null;
			
			if (calcPrCommandsResult.hasCoarseCommands()) {
				// always command the coarse mirror to setup values at the start of all procedures
				coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(calcPrCommandsResult.getCoarseCommands());
				statusLogger.log("camera.cmd.coarse_mirror", calcPrCommandsResult.getCoarseCommands().x, calcPrCommandsResult.getCoarseCommands().y);

			} 
			if (calcPrCommandsResult.hasFineCommands()) {
				fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(calcPrCommandsResult.getFineCommands());
				statusLogger.log("camera.cmd.fine_mirror", calcPrCommandsResult.getFineCommands().x, calcPrCommandsResult.getFineCommands().y);
			}
			
			Utils.waitForComplete(coarseMirrorCommandFuture, fineMirrorCommandFuture);
			
			if (calcPrCommandsResult.hasCoarseCommands() || calcPrCommandsResult.hasFineCommands()) {
				statusLogger.log("camera.cmd.complete");
			}
			
			
			// fill the iteration output: TODO all pupilRegErrorResultFields
			// TODO: what was moved and how much


			
					
			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);


			
		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}

		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

}
