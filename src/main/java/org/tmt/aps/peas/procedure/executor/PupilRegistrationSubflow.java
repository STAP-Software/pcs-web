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
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

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


	@Abortable
	public boolean execute(Procedure procedure, FindCentroidsResult findCentroidsResult) throws Throwable {
		
		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		

		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

		
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

		
		
		procedureExecutionState.setPercentComplete(90);
		
		/*****************************************************/
		/*         move fine, coarse, both, or none          */
		/*****************************************************/
		
		if (calcPrCommandsResult.isOffloaded()) {
			statusLogger.log("calc.pupil_reg.cmd_offloaded");
		}
		
		Future<Point> coarseMirrorCommandFuture = null;
		Future<Point> fineMirrorCommandFuture = null;
		
		boolean commandsSent = false;
		// if frame from file, do not send commands
		if (!procedureConfig.isFrameFromFile()) {
		
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
				commandsSent = true;
			}
		
		}
		
		// return false if we need to take a new frame
		// if the error was > thresh (10 mm)and a move was performed, then return false
		float frameOkThreshold = procedure.getProcedureConfigSet().getPupilRegErrorConfig().getFrameOkThreshold();
		if ((Math.abs(regErrorMm.x) > frameOkThreshold || Math.abs(regErrorMm.y) > frameOkThreshold) && commandsSent) {
			return false; // retake the frame
		}
		
		return true;
		
		
	}
	
	


}
