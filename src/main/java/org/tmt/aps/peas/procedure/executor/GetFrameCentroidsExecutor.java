/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.exception.HandMarkRequiredException;
import org.tmt.aps.peas.procedure.exception.NonLinearIntensitiesException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class GetFrameCentroidsExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private ComputationContext computationContext;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("GetFrameCentroidsExecutor::PostConstruct::");
	}

	public ProcedureCcdFrame executeProcedure(Procedure procedure, Session currentSession) throws Throwable {

		logger.info("GetFrameCentroidsExecutor::executeProcedure::");

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();

		ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

		// CreateRefBeamMapProcedureOutput procedureOutput = (CreateRefBeamMapProcedureOutput)procedure.getProcedureOutput();

		// FIXME: this is not the correct procedure name, also should say "sub-procedure start"
		statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());

		ProcedureCcdFrame procedureCcdFrame = null;
		CentroidMap centroidMap = null;
		
		// initialize frame number
		
		int frameNumber = procedureConfig.isFrameFromFile() ? 0 : procedure.getProcedureCcdFrameCount();

		while (true) {

			statusLogger.log("frame.get");

			procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(),
					procedure.getProcedureNumber(), 0, frameNumber, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd()
							.getAllHotPixelRects(), procedure.getProcedureConfigSet().getGlobalConfig().isRemoveBadPixels());
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

			// tell the async controller to update the frame
			frameDisplayMgmt.displayFrame(frameNumber);
			
			frameNumber++;
			

			statusLogger.log("fandi.start");

			// use NumSpots and maybe findAndIdentify should take an array of FloatPoints
			int numSpots = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots();

			FIConfig fiConfig = procedure.getProcedureConfigSet().getFiConfig();
			FIResult fiResult = null;
			try {

				fiResult = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots, fiConfig, procedure.getRefBeamMap(),
						procedure.getRefDefMap());

				logger.info("Find and Identify completed");

				computationLibrary.evalFiResult(fiResult, fiConfig, procedureConfig);

				centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);
				procedureCcdFrame.setCentroidMap(centroidMap);
				
				// test for non-linear subimage maximums
				computationLibrary.checkSubimageIntensities(ccdFrame.getCorrectedFrame(), centroidMap, physicalModel.getInstrument().getCcd().getNonLinearThreshold());
				
				break; // success, break of out while loop

			} catch (NonLinearIntensitiesException e) {
				
				String text = MessageGenerator.generateMessage("fandi.intensities.nonlinear");
				
				// user interaction
				statusLogger.log("procedure.exception", text);

				int response = userPromptMgmt.displayFlowControlTriFlowDialog(text);

				if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					throw new Exception("User Aborted Test");
				} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
					break;
				}
				// we get here if we are going to re-take frame (Retry)
				
			} catch (HandMarkRequiredException e) {

				if (procedure.getProcedureType().isPassiveTilt()) {

					fiResult = handMark(procedure, fiConfig);

					try {
						centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);
						procedureCcdFrame.setCentroidMap(centroidMap);
						break; // everything ok, move forward
					} catch (Exception e1) {
						statusLogger.log("procedure.exception", e1.getMessage());
						throw new Exception("Aborted Test: " + e1.getMessage());
					}

				} else {
					break;
				}

			} catch (UserAssistRequiredException e) {

				StringBuffer buf = new StringBuffer(MessageGenerator.generateMessage("fandi.end.question"));
				if (e.isNdetectNotAllSingle()) {
					buf.append(MessageGenerator.generateMessage("fandi.ndetect_not_single"));
				}

				if (e.isFracThreshExceeded()) {
					buf.append(MessageGenerator.generateMessage("fandi.frac_vs_threshold", fiResult.getFracFilledBoxes(),
							fiConfig.getFracFilledThresh()));
				}

				if (e.isFourierThreshExceeded()) {
					buf.append(MessageGenerator.generateMessage("fandi.fourqual_vs_threshold", fiResult.getFourierQuality(),
							fiConfig.getFourierQualityThresh()));
				}

				if (e.isBadNSolution()) {
					buf.append(MessageGenerator.generateMessage("fandi.bad_nsolution", fiResult.getnSolution()));
				}

				String text = buf.toString();

				// user interaction
				statusLogger.log("procedure.exception", text);

				int response = userPromptMgmt.displayFlowControlTriFlowDialog(text);

				if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					throw new Exception("User Aborted Test");
				} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {

					try {

						centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);
						procedureCcdFrame.setCentroidMap(centroidMap);

					} catch (HandMarkRequiredException hme) {

						if (procedure.getProcedureType().isPassiveTilt()) {

							fiResult = handMark(procedure, fiConfig);

							try {
								centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);
								procedureCcdFrame.setCentroidMap(centroidMap);
								break; // everything ok, move forward
							} catch (Exception e1) {
								statusLogger.log("procedure.exception", e1.getMessage());
								throw new Exception("Aborted Test: " + e1.getMessage());
							}

						} else {
							break;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					break;
				}

			} catch (Exception e) {
				// user interaction
				statusLogger.log("procedure.exception", e.getMessage());

				String unknownError = (e.getMessage() == null) ? "Unknown Error: " : "";

				int response = userPromptMgmt.displayFlowControlBiFlowDialog(unknownError + e.getMessage());

				if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					throw new Exception("User Aborted Test");
				}

			}

		}
		return procedureCcdFrame;
	}

	// TODO: generalize this, does not need to be explicit in an executor

	public CentroidMap buildCentroidMap(List<FloatPoint> centroids, ProcedureConfig procedureConfig, FIConfig fiConfig, FIResult fiResult) {

		CentroidMap centroidMap = new CentroidMap();
		String centroidMapData = FloatPointListEncoder.encodeList(centroids);
		centroidMap.setCentroidMapData(centroidMapData);
		centroidMap.setValues(centroids);

		// FIXME: these are stored in FIConfigActual table, associate from there, do not store here
		centroidMap.setForcedRotation(fiConfig.getForceRotationValue());
		centroidMap.setForcedRotationFlg(fiConfig.isForceRotation());
		centroidMap.setForcedScale(fiConfig.getForceScaleValue());
		centroidMap.setForcedScaleFlg(fiConfig.isForceScale());

		centroidMap.setPupilMaskType(procedureConfig.getPupilMask().getPupilMaskType());

		centroidMap.setFourierQuality(fiResult.getFourierQuality());
		centroidMap.setScale(fiResult.getScale());
		centroidMap.setRotation(fiResult.getRotation());
		centroidMap.setTranslationX(fiResult.getTranslation().getX());
		centroidMap.setTranslationY(fiResult.getTranslation().getY());
		centroidMap.setNumFilledBoxes(fiResult.getNumFilledBoxes());
		centroidMap.setFracFilledBoxes(fiResult.getFracFilledBoxes());

		return centroidMap;
	}

	private CentroidMap findAndDisplayCentroids(Procedure procedure, FIConfig fiConfig, FIResult fiResult) throws Exception {
		List<FloatPoint> centroids = null;

		ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		ProcedureCcdFrame procedureCcdFrame = procedure.getLatestProcedureCcdFrame();
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

		centroids = computationLibrary.findCentroids(ccdFrame.getCorrectedFrame(), fiResult, procedure.getProcedureConfigSet()
				.getFindCentConfig());
		CentroidMap centroidMap = buildCentroidMap(centroids, procedureConfig, fiConfig, fiResult);


		// display the marked frame
		frameDisplayMgmt.setMarking(centroids);
		frameDisplayMgmt.displayMarkedFrame();

		boolean userResponse = graphicDisplayMgmt.displaySubimageCentroids(centroidMap, UserPrompt.PROMPT_TYPE_YES_NO,
				"Have the correct centroids been found?");

		// as part of the display, ask the user if it is OK (only passive tilt)
		// throw a UserAssistException if they don't like it.
		if (!userResponse) {
			throw new HandMarkRequiredException();
		}
		
		return centroidMap;
	}

	private FIResult handMark(Procedure procedure, FIConfig fiConfig) throws Exception {

		List<FloatPoint> handMarked = null;
		ProcedureCcdFrame procedureCcdFrame = procedure.getLatestProcedureCcdFrame();
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

		while (true) {
			frameDisplayMgmt.displayFrame(MessageGenerator.generateMessage("instructions.pt_hand_mark"), "PTNumbering.jpg");
			frameDisplayMgmt.clearMarking();
			frameDisplayMgmt.setPendingMarkAction(true);
			// wait for user to mark frame
			statusLogger.log("frame.mark_waiting");
			while (frameDisplayMgmt.getPendingMarkAction()) {
				Thread.sleep(500);
			}

			// get marking data from the frame display
			handMarked = frameDisplayMgmt.getMarkList();

			if (handMarked.size() == 36) {
				break;
			} else {
				// TODO: put in resource bundle
				userPromptMgmt.displayInfoDialog("You did not mark the correct number of spots");

			}
		}

		return new FIResult(handMarked, ccdFrame.getCorrectedFrame());

	}
}
