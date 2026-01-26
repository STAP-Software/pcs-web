package org.tmt.aps.peas.procedure.executor;

import java.util.Hashtable;
import java.util.concurrent.Future;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmtAsync;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CcdGain;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * Executor for the Ready Camera subflow
 * This is a common flow, but not a procedure
 * @author smichaels
 */
@Singleton
@Startup
public class ReadyCameraSubflow {

	static Logger logger = Logger.getLogger(ReadyCameraSubflow.class);
	

	@EJB
	private StatusLogger statusLogger;
	@EJB
	private CameraMgmt cameraMgmt;	
	@EJB
	private CcdMgmt ccdMgmt;
	@EJB
	private CameraMgmtAsync cameraMgmtAsync;
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	private PhysicalModel physicalModel;


	
	/**
	 * Executor method: this method is the Ready Camera sub-flow
	 */
	@Abortable
	public void execute(Procedure procedure) throws Throwable {
		
		readyCameraFlow(procedure);
	}
	
	private void readyCameraFlow(Procedure procedure) throws Throwable {
		
		try {
			
			readyCamera(procedure);
		
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
					Integer mechanism = CameraMgmt.errorCodeToMechanism.get(Integer.valueOf(failureCode));
					if (mechanism != null) {
						homeMechanism(mechanism);
					}
					
				}
				
				// retry recursively
				readyCameraFlow(procedure);
			}
		}
	}
			
	private void readyCamera(Procedure procedure) throws Exception {
			
			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

			// always command the coarse and fine mirror to setup values at the start of all procedures
			Point coarseMirrorDefault = procedure.getProcedureConfigSet().getGlobalConfig().getCoarseMirrorDefault();
			Point desiredCoarseMirrorPosition = coarseMirrorDefault;
			if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs()) {
				// in the case of SUFS, we add the steering to the sufs group
				Point sufsCoarseOffsets = procedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig().getCoarseMirrorOffsetCurrent();
				desiredCoarseMirrorPosition = Point.add(coarseMirrorDefault, sufsCoarseOffsets);
			} 
			
			Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(desiredCoarseMirrorPosition);
			statusLogger.log("camera.cmd.coarse_mirror", desiredCoarseMirrorPosition.x, desiredCoarseMirrorPosition.y);
			
			Point fineMirrorDefault = procedure.getProcedureConfigSet().getGlobalConfig().getFineMirrorDefault();
			Future<Point> fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(fineMirrorDefault);
			statusLogger.log("camera.cmd.fine_mirror", fineMirrorDefault.x, fineMirrorDefault.y);
			
			
			Future<Integer> twoPosCommandFuture = null;
			Future<Integer> refBeamFuture = null;
			// command to mask selected
			statusLogger.log("camera.cmd.pupil_wheel", procedureConfig.getPupilMask().getWheelPosition());
			Future<Integer> pupilMaskCommandFuture = cameraMgmt.commandPupilMask(procedureConfig.getPupilMask().getWheelPosition());
			// command to filter selected
			statusLogger.log("camera.cmd.filter_wheel", procedureConfig.getFilter().getWheelPosition());
			Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());

			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// select ref beam based on filter wavelength
				ReferenceBeam refBeam = procedureConfig.getReferenceBeam();
				statusLogger.log("camera.cmd.ref_beam", refBeam.getRefBeamNum());
				refBeamFuture = cameraMgmt.commandReferenceBeamState(refBeam.getRefBeamNum());

				// extend two pos mirror
				statusLogger.log("camera.cmd.two_pos_device", "extend");
				twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.EXTENDED);
			} else {
				// turn off reference beams
				statusLogger.log("camera.cmd.ref_beam", 0);
				refBeamFuture = cameraMgmt.commandReferenceBeamState(0);

				// retract two pos mirror
				statusLogger.log("camera.cmd.two_pos_device", "extend");
				twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.RETRACTED);
			}
			
		
			statusLogger.log("ccd.cmd.gain", procedureConfig.getCcdGainNumber());
			Future<Integer> ccdGainFuture = ccdMgmt.setGain(procedureConfig.getCcdGainNumber());
			
			// get the gain we will have if successful to set the offsets right now without having to wait
			CcdGain ccdGain = physicalModel.getInstrument().getCcd().getCcdGain(procedureConfig.getCcdGainNumber());
			statusLogger.log("ccd.cmd.offset", ccdGain.getGainOffsetChannel0(), ccdGain.getGainOffsetChannel1());
			Future<Integer> ccdOffsetFuture = ccdMgmt.setOffset(ccdGain.getGainOffsets());

			// open shutter
			// FIXME: remove this call when all shutter usage is deprecated
			//cameraMgmt.commandCcdShutterState(CameraCommand.OPEN);

			
			// wait for all commands to complete
			long waitPeriodMs = Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture,
					coarseMirrorCommandFuture, fineMirrorCommandFuture, ccdGainFuture, ccdOffsetFuture);
			statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);

			
		}


	}

	public void homeMechanism(int mechanism) throws Exception {
		
		Future<Integer> commandFuture = null;
		
		switch (mechanism) {
		
		case CameraMgmt.X_TILT_MOTOR:
			statusLogger.log("camera.cmd.homing", "tilt plate X");
			commandFuture = cameraMgmtAsync.commandFineTiltMirrorX(0);
			break;
			
		case CameraMgmt.Y_TILT_MOTOR:
			statusLogger.log("camera.cmd.homing", "tilt plate Y");
			commandFuture = cameraMgmtAsync.commandFineTiltMirrorX(0);
			break;
			
		case CameraMgmt.X_STEERING_MOTOR:
			statusLogger.log("camera.cmd.homing", "steering mirror X");
			commandFuture = cameraMgmtAsync.commandCoarseTiltMirrorX(0);
			break;
			
		case CameraMgmt.Y_STEERING_MOTOR:
			statusLogger.log("camera.cmd.homing", "steering mirror Y");
			commandFuture = cameraMgmtAsync.commandCoarseTiltMirrorX(0);
			break;
		}
				
		// wait for all commands to complete
		long waitPeriodMs = Utils.waitForComplete(commandFuture);
		statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);

		
	}

	


}