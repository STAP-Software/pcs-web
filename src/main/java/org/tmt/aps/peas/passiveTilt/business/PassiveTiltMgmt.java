package org.tmt.aps.peas.passiveTilt.business;

import java.awt.Point;
import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.camera.business.CameraMgmt;
import org.tmt.aps.peas.common.fortran.FortranProxy;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.ImageFrame;
import org.tmt.aps.peas.frame.model.RegistrationDelta;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
public class PassiveTiltMgmt {

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private ImageProcessor imageProcessor;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	private StatusLogMgmt statusLogMgmt;
	@EJB
	private ProcedureExecutionMgmt procedureExecutionMgmt;
	@EJB
	private FortranProxy fortranProxy;
	@EJB
	private PupilRegistrator pupilRegistrator;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@Asynchronous
	public void executeProcedure(Procedure procedure) {
		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfig();
			
			procedure.setExecutionStartTime(new Date());
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_EXECUTING);
			
			procedureExecutionMgmt.setExecutionStatus(true);
			procedureExecutionMgmt.setPercentComplete(0);
			
			statusLogMgmt.initLog();
			
			// TODO: frame simulation mode sets iterations = 1 (why?) - this should also be part of form validation

			wait(1);
			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// TODO: implement
				// autoPointTelescope();

				cameraMgmt.commandPupilMask(Constants.PUPIL_MASK_PASSIVE_TILT);

				cameraMgmt.commandFilter(procedureConfig.getFilter());

				// TODO: implement
				// autoRefmapCheck();

				cameraMgmt.readyCamera();

				cameraMgmt.selectRefBeam(); // check if this is a command or something else

				// FIXME
				// cameraMgmt.cameraCommand("45E"); // what is this really? we need to abstract this
			}

			for (int i = 0; i < procedureConfig.getNumberOfTrials(); i++) {

				statusLogMgmt.log("Current frame being used for test ");
				ImageFrame frame = frameMgmt.getCorrectedFrame(procedureConfig.getFrameSource());
				
				// TODO: this is where we display the frame
				
				wait(3);

				statusLogMgmt.log("Calling Find and Identify ");
				List<Point> subimageList = imageProcessor.findAndIdentify(frame);
				
				// TODO: this is where we display the marked frame

				wait(2);
				graphicDisplayMgmt.displaySubimageCentroids(subimageList);

				// TODO: argument list is not complete
				RegistrationDelta registrationDelta = imageProcessor.pupilRegistration36(subimageList);

				// FIXME: in PCS Fortran pupilRegistration36 always returns true
				if (true) {

					pupilRegistrator.centerPupil(registrationDelta); // this should do the generate misregistration string if simulation
																		// mode
					// TODO: investigate automode_abort flag. Need to exit gracefully (non-exception) if set.

				} else {

					// FIXME: this might be removed

					// TODO: tell user that auto pupil registration failed
					// centerPupilManual(); // this would know how to handle simulation modes.

				}

				// TODO: if not frame_ok (global variable), then ask user if they want to retake frame
				// this construct will probably work
				if (false) {
					i--;
					continue;
				}
				// TODO:
				List<Point> referenceSubimageList = null;
				List<Point> centroidOffsets = imageProcessor.calculateCentroidOffsets(subimageList, referenceSubimageList);

				// TODO: if not frame_ok (global variable), then ask user if they want to retake frame
				// this construct will probably work
				if (false) {
					i--;
					continue;
				}

				graphicDisplayMgmt.displayCentroidOffsets(centroidOffsets);

				int trialPct = (int) ((((i+1)*100)/procedureConfig.getNumberOfTrials()) * 0.95);
				
				procedureExecutionMgmt.setPercentComplete(trialPct);
			}

			// FIXME: what is this really? we need to abstract this
			// cameraMgmt.cameraCommand("RBF"); //

			imageProcessor.calculateCentroidStats();

			fortranProxy.actuatorLengths();
			
			userPromptMgmt.displayYesNoDialog("here is some text");
			
			procedureExecutionMgmt.setExecutionStatus(false);
			procedureExecutionMgmt.setPercentComplete(100);

		} catch (Exception e) {
			
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_ABORTED);

		}
		/*
		 * getProcStats();
		 */
		procedure.setExecutionEndTime(new Date());
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_COMPLETED);

	}

	private void wait(int secs) {
		// here we wait until the pending display is cleared
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {

		}

	}

}
