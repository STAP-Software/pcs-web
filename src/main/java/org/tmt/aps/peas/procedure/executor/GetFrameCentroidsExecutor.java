/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.ArrayList;
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

		while (true) {

			statusLogger.log("frame.get");

			procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(),
					procedure.getProcedureNumber(), 0, 0, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd()
							.getAllHotPixelRects(), procedure.getProcedureConfigSet().getGlobalConfig().isRemoveBadPixels());
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

			// tell the async controller to update the frame
			frameDisplayMgmt.displayFrame();

			statusLogger.log("fandi.start");

			// use NumSpots and maybe findAndIdentify should take an array of FloatPoints
			int numSpots = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots();

			FIConfig fiConfig = procedure.getProcedureConfigSet().getFiConfig();
			FIResult fiResult = null;
			try {

				fiResult = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots, fiConfig, procedure.getRefBeamMap(),
						procedure.getRefDefMap());
				List<FloatPoint> centroids = new ArrayList<FloatPoint>();
				logger.info("Find and Identify completed");
				try {
					centroids = computationLibrary.findCentroids(ccdFrame.getCorrectedFrame(), fiResult, procedure.getProcedureConfigSet()
							.getFindCentConfig());
				} catch (Exception e) {
					CentroidMap centroidMap = buildCentroidMap(centroids, procedureConfig, fiConfig, fiResult);
					procedureCcdFrame.setCentroidMap(centroidMap);
					throw e;
				}
				CentroidMap centroidMap = buildCentroidMap(centroids, procedureConfig, fiConfig, fiResult);
				procedureCcdFrame.setCentroidMap(centroidMap);

				statusLogger.log("fandi.end.success");

				// display the marked frame
				frameDisplayMgmt.setMarking(centroids);
				frameDisplayMgmt.displayMarkedFrame();

				graphicDisplayMgmt.displaySubimageCentroids(centroidMap);

				computationLibrary.evalFiResult(fiResult, fiConfig, procedureConfig);
				break; // success, break of out while loop
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

				String text = buf.toString();

				// user interaction
				statusLogger.log("procedure.exception", text);

				int response = userPromptMgmt.displayFlowControlTriFlowDialog(text);

				if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					throw new Exception("User Aborted Test");
				} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
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

}
