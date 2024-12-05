package org.tmt.aps.peas.procedure.executor;

import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * Pupil Registration common sub flow methods
 * @author smichaels
 */
@Singleton
@Startup
public class PupilRegistrationSubflow {

	static Logger logger = Logger.getLogger(PupilRegistrationSubflow.class);
	

	@EJB
	private StatusLogger statusLogger;
	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private ProcedureExecutionState procedureExecutionState;
	@EJB
	private SubimageDefCache subimageDefCache;
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	ReadyCameraSubflow readyCameraSubflow;


	/**
	 * This method is the PupilRegistration sub-flow
	 */
	@Abortable
	public boolean execute(Procedure procedure, FindCentroidsResult findCentroidsResult) throws Throwable {
		
		final double PUPIL_ROTATION_XY_THRESH = 15.0/1000.0;  // 15 mm

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
		Integer sufsGroup = procedure.getProcedureType().isSufs() ? procedureConfig.getSufsGroup() : null;

		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), sufsGroup);

		
		/*****************************************************/
		/*            calcPupilRegErrorDefaults              */
		/*****************************************************/
		
		/* 
		 * TODO Add the following variables as the last parameters of calculatePupilRegError
		 *
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();
			globalConfig.getPupilRegistrationOffsetX()
			globalConfig.getPupilRegistrationOffsetY()
		*/	

		
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

			
			// Tell user that the rotation exceeds threshold (only if in PR procedure)
			double pupilRotation = pupilRegErrorResult.getRegErrorPhi() / Constants.DEG2RAD;
			double threshold = procedure.getProcedureConfigSet().getPupilRegErrorConfig().getPupilRotationThreshold();
			
			
			if (Math.abs(pupilRegErrorResult.getRegErrorX()) < PUPIL_ROTATION_XY_THRESH && 
					Math.abs(pupilRegErrorResult.getRegErrorY()) < PUPIL_ROTATION_XY_THRESH &&
					Math.abs(pupilRotation) > threshold && procedure.getProcedureType().isPupilRegistration()) {
				statusLogger.log("calc.pupil_rotation_exceeds_threshold", pupilRotation);  // degrees
				String warningMessage = MessageGenerator.generateMessage("calc.pupil_rotation_exceeds_threshold", pupilRotation);
				userPromptMgmt.displayInfoDialog("Pupil Rotation Warning", warningMessage);
			}
			
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
			centerPupil = userPromptMgmt.displayYesNoDialog("Send Pupil Reg Commands", text + "\n\nSend commands to correct pupil registration errors?");
			
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
				
				desiredCenterPupilMech = userPromptMgmt.displayGenericMultiChoiceDialog("Pupil Reg Cmd Mechanism", text + "\n\nChoose mechanism to center pupil:", 
						choices, values);
							
			} else {
				desiredCenterPupilMech = procedureConfig.getAutoCenterPupilMechanism();
			}					
		}


		CoarseTiltMirror coarseMirror = physicalModel.getInstrument().getCamera().getCoarseTiltMirror();
		FineTiltMirror fineMirror = physicalModel.getInstrument().getCamera().getFineTiltMirror();

		// need to know current positions which is available in the coarse and fine mirror objects
		CalcPrCommandsResult calcPrCommandsResult = computationLibrary.calcPrCommands(centerPupil, desiredCenterPupilMech, pupilRegErrorResult, 
				procedure.getProcedureConfigSet().getPupilRegErrorConfig(), fineMirror, coarseMirror);

				
		/*****************************************************/
		/*         move fine, coarse, both, or none          */
		/*****************************************************/
		
		if (calcPrCommandsResult.isOffloaded()) {
			statusLogger.log("calc.pupil_reg.cmd_offloaded");
		}
		
		boolean commandsSent = false;
		
		// if frame from file, do not send commands
		if (!procedureConfig.isFrameFromFile()) {
		
			commandsSent = correctPupil(calcPrCommandsResult);

		}
		
		// return false if we need to take a new frame
		// if the error was > thresh (10 mm)and a move was performed, then return false
		float frameOkThreshold = 
				procedure.getProcedureConfigSet().getPupilRegErrorConfig().getFrameOkThreshold();
					
	
		logger.info("frameOkThreshold = " + frameOkThreshold);
		if ((Math.abs(regErrorMm.x) > frameOkThreshold || Math.abs(regErrorMm.y) > frameOkThreshold) && commandsSent) {
			return false; // retake the frame
		}
		
		return true;
		
		
	}
	
	private boolean correctPupil(CalcPrCommandsResult calcPrCommandsResult) throws Exception {
		
		Future<Point> coarseMirrorCommandFuture = null;
		Future<Point> fineMirrorCommandFuture = null;

		boolean commandsSent = false;
		
		try {
			
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
			
			long waitPeriodMs = Utils.waitForComplete(coarseMirrorCommandFuture, fineMirrorCommandFuture);
			
			if (calcPrCommandsResult.hasCoarseMirrorCommands() || calcPrCommandsResult.hasFineMirrorCommands()) {
	
				statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
				commandsSent = true;
			}
	
		} catch (Exception e) {
			
			Throwable internalException = e;
			if (e.getCause() instanceof java.util.concurrent.ExecutionException) {
				internalException = e.getCause().getCause();
			}
			
			String text = MessageGenerator.generateMessage("camera.cmd.exception", internalException.getMessage());
			
			statusLogger.log("camera.cmd.exception", internalException.getMessage());

			String[] choices = {"Continue", "Retry Camera Commands", "Abort Test"};
			int[] values = {UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE, UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY, UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT};

			int response = userPromptMgmt.displayGenericMultiChoiceDialog("Camera Command Failure", text, choices, values);

			if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
				throw new AbortProcedureException("User Aborted Test");
			} 
			
			if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY) {
				
				// home the appropriate mirror if affected
				if (internalException instanceof CommandFailureException) {
					int failureCode = ((CommandFailureException)internalException).getFailureCode();
					
					// check if failureCode is anything we can try to correct by homing a motor/stage
					Integer mechanism = CameraMgmt.errorCodeToMechanism.get(new Integer(failureCode));
					if (mechanism != null) {
						readyCameraSubflow.homeMechanism(mechanism);
					}
					
				}
				
				// retry recursively
				correctPupil(calcPrCommandsResult);
			}
			
			
		}
		
		return commandsSent;
	}


}
