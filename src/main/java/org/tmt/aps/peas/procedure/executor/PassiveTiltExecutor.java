/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class PassiveTiltExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private AcsMgmt acsMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private ImageProcessor imageProcessor;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private FrameDisplayMgmt frameDisplayMgmt;
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
	private PupilRegistrator pupilRegistrator;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("PassiveTiltExecutor::PostConstruct::");
	}

	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("PassiveTiltExecutor::testMethod::");
		return null;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("PassiveTiltExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PassiveTiltProcedureOutput procedureOutput = (PassiveTiltProcedureOutput)procedure.getProcedureOutput();
			
			procedureExecutionMgmt.performProcedureStartup(procedure);

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");

			// TODO: implement PP-338, 339
			// if frame source is file, use the centroid map associated with the frame (if old frame use current ref map) TBD
			// autoRefmapCheck();
			/*
			 * OK = AUTO_REFMAP_CHECK(ZPASSIVE_AUTOREFMAP, NUMBER_TRIALS, MASK_MENU_PT, FILT_POS, 0)
			 */
			// FIXME: get latest for now
			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), 
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
					procedureConfig.getFilter().getFilterType().getFilterTypeId(), -1);
			procedure.setRefBeamMap(currentRefMap);
			
			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// always command the coarse mirror to setup values at the start of all procedures
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getProcedureConfigSet()
						.getGlobalConfig().getCoarseMirrorDefault());

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
					ReferenceBeam refBeam = physicalModel.getInstrument().getCamera()
							.getReferenceBeamByWavelength(procedureConfig.getFilter().getWavelength());
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

				// wait for all commands to complete
				Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture,
						coarseMirrorCommandFuture);
				statusLogger.log("camera.cmd.complete");

			}

			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());
			
			ProcedureCcdFrame procedureCcdFrame = null;
			List<FloatPoint> centroidOffsets = null;
			
			while (true) {

				procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);

				try {

					statusLogger.log("calc.centroid_resid");

					// TODO: centroid offsets throws an exception if telescope pointing out of tolerance
					// TODO: centroid offset calc
					/*****************************************************/
					/*              calculateCentroidOffsets             */
					/*****************************************************/
					centroidOffsets = computationLibrary.calculateCentroidOffsets(procedureCcdFrame.getCentroidMap().getValues(), 
							procedure.getRefBeamMap().getCentroidMap().getValues());
					
					statusLogger.log("calc.rigid_body_rot", 0.284E-03);

					statusLogger.log("telescope.desired_move", 0.04, 0.14);

					int trialPct = (int) (((100) / procedureConfig.getNumberOfTrials()) * 0.95);

					procedureExecutionState.setPercentComplete(trialPct);

					break;  // leave the loop if no exception
					
				} catch (Exception e) {

					// ask user if they want to re-take the frame
					int reply = userPromptMgmt.displayFlowControlTriFlowDialog("" + e.getMessage());

					if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
						// TODO: put in logic here (throw user abort exception?
					} else if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
						break;  // continue on
					}
					
					// go back and re-take frame
				}

			}

			// TODO: calc average offsets and calc avg translation, rotation and scale from average offsets

			// FIXME: move up to before display
			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/
			//computationLibrary.calculateCentroidStats();

			// TODO: we should persist image rotation, scale, rrmsTotal, focusError, enclosedEnergy and enclosed50Energy
			// TODO: and also: ZPROCLOG_DATA_ACS_FOCUS = ZPROCLOG_DATA_PRIMARY_ACT_FM_RMS/41.1
			// TODO: and these: procedure number and all frame heading records

			// TODO: also whatever this does
			// CALL GET_PROC_STATS(ZPASSIVE_FRAME_SOURCE)
			
			
			procedureOutput.setCentroidOffsets(centroidOffsets.toArray(new FloatPoint[0]));
			procedureOutput.setCentroidOffsetsRms(11.56f);
			procedureOutput.setCentroidOffsetsFocus(77.77f);
			procedureOutput.setM1CmdsSent(false);
			float[][] m1ActuatorCmds = new float[36][3];
			procedureOutput.setM1ActuatorCmds(m1ActuatorCmds);
			procedureOutput.setM1ActuatorCmdsRms(33.4f);
			procedureOutput.setM1PistonCmdsRms(22.4f);
			procedureOutput.setM1PistonResidualRms(44.45f);
			procedureOutput.setRotationFromRefBeam(55.23f);
			procedureOutput.setScaleChangeFromRefBeam(1.004f);
			procedureOutput.setTranslationFromRefBeam(new FloatPoint(2.3f, 4.5f));
			

			// TODO: display the average centroid offsets - this is probably not needed since we only do one trial
			graphicDisplayMgmt.displayCentroidOffsets(procedureOutput);

			// TODO: implement ttOffsetsToActs
			/*****************************************************/
			/*                  ttOffsetsToActs                  */
			/*****************************************************/
			// computationLibrary.ttOffsetsToActs(a, b);

			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - no need to wait for response				
				cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
			}


			// TODO: display the pistonDeltas
			// graphicDisplayMgmt.displayPistonDeltas(????);
			

			// TODO: Calculate standard deviation
			// CALL CALC_PRIMARY_STATS_ONE_TRIAL(PRIMARY_ACT_RMS,PRIMARY_ACT_NO_FM_RMS,PRIMARY_ACT_FM_RMS,FINE_SCREEN_TEST)
			// CALL CALC_PRIMARY_STATS_N_TRIAL(PRIMARY_ACT_RMS_STDEV,PRIMARY_ACT_NO_FM_RMS_STDEV,PRIMARY_ACT_FM_RMS_STDEV,FINE_SCREEN_TEST)
			// CALL CREATE_PRIMACT_STATS(PRIMARY_ACT_RMS,PRIMARY_ACT_NO_FM_RMS,PRIMARY_ACT_FM_RMS,PRIMARY_ACT_RMS_STDEV,
			//             PRIMARY_ACT_NO_FM_RMS_STDEV,PRIMARY_ACT_FM_RMS_STDEV,FINE_SCREEN_TEST,STRING) 
			// ZPROCLOG_DATA_FS_PIST_RMS = PISTON_RMS
			// ZPROCLOG_DATA_FS_PIST_RESID_RMS = RESID_RMS
			           

			// TODO: use resource bundles
			boolean commandAcs = userPromptMgmt.displayYesNoDialog("Command Primary Mirror?");

			// FIXME: temp var to keep compilation
			Double[][] actDeltas = new Double[36][3];
	        
			// command ACS
			if (commandAcs) {
				
				try {
					// send out the commands
					acsMgmt.commandActuatorDeltas(actDeltas);
					
					// TODO: use resource bundles
					statusLogger.log("Actuator Delta Send Successful");
					logger.info("doSendActDeltaCommands: success");
				} catch (Exception e) {
					// TODO: use resource bundles
					statusLogger.log("Error sending actuator deltas");
					e.printStackTrace();
				}
	           // TODO: Mark the command as sent
	           // ZPROCLOG_DATA_ACS_CMD_SENT = 1
	                                                                                
			}                        
			
			
			
			statusLogger.log("procedure.end", procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setExecutionStatus(false);
			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		/*
		 * getProcStats();
		 */

		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}

	/*
	 * 
	 * // Let's be nice and turn off the lights
	 * 
	 * IF (GET_SYMBOL('STUB_DEMO')) OK = CAMERA_COMMAND( 'RBF') C C Put centroid offsets into its own array again C
	 */

	private void wait(int ms) {
		// here we wait until the pending display is cleared
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {

		}

	}

}
