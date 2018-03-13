/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.CcdDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.CcdLeftRightBiasException;
import org.tmt.aps.peas.procedure.exception.FandIException;
import org.tmt.aps.peas.procedure.exception.HandMarkRequiredException;
import org.tmt.aps.peas.procedure.exception.NonLinearIntensitiesException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * The GetFrameCentroids common subflow methods
 * @author smichaels
 */
@Singleton
@Startup
public class GetFrameCentroidsExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private CcdMgmt ccdMgmt;
	@EJB
	private CcdDefMgmt ccdDefMgmt;
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
	private SubimageDefCache subimageDefCache;
	@EJB
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private ProcedureExecutionState procedureExecutionState;
	@EJB 
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	//ComputationLibrary computationLibrary;
	ProcedureCcdFrame procedureCcdFrame = null;
	CentroidMap centroidMap = null;
	FIConfig fiConfig = null;
	FIResult fiResult = null;
	FindCentroidsResult findCentroidsResult = null;
	Procedure procedure = null;
	ProcedureConfig procedureConfig = null;
	int frameNumber = 0;

	@PostConstruct
	void init() {
		logger.debug("GetFrameCentroidsExecutor::PostConstruct::");
	}

	/**
	 * This method is the GetFrameCentroids sub-flow
	 */
	public ProcedureCcdFrame executeProcedure(Procedure procedure, Session currentSession) throws Throwable {

		logger.info("GetFrameCentroidsExecutor::executeProcedure::");

		// store a local copy for functions to access
		this.procedure = procedure;

		procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();

		//computationLibrary = computationContext.getComputationLibrary();

		// CreateRefBeamMapProcedureOutput procedureOutput = (CreateRefBeamMapProcedureOutput)procedure.getProcedureOutput();

		statusLogger.log("subprocedure.start", "Get Frame Centroids");

		procedureCcdFrame = null;
		centroidMap = null;
		fiResult = null;
		findCentroidsResult = null;

		// initialize frame number
		frameNumber = procedure.getProcedureCcdFrameCount();


		takeFrameAndFindCentroids();


		return procedureCcdFrame;
	}

	
	private void takeFrameAndFindCentroids() throws Exception {

		statusLogger.log("frame.get");

		int iteration = procedureExecutionState.getCurrentIteration();
		
		procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureConfigSet().getFrameCorrectionConfig(), 
				procedure.getProcedureType(), procedure.getProcedureNumber(),
				iteration, frameNumber, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd().getAllHotPixelRects(),
				procedureConfig.isRemoveBadPixels());
		
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();

		// tell the async controller to update the frame
		frameDisplayMgmt.displayFrame(frameNumber);

		// for now we don't increment frame number if frame from file
		if (!procedureConfig.isFrameFromFile())
			frameNumber++;

		statusLogger.log("fandi.start");

		// use NumSpots and maybe findAndIdentify should take an array of FloatPoints
		int numSpots = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots();

		fiConfig = procedure.getProcedureConfigSet().getFiConfig();

		SubimageDefList subimageDefList = null;
		if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs() && !procedure.getProcedureType().isCreateRefMap()) {
			subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getSufsGroup());
		} else {
			subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());			
		}
		
		boolean findAllMaskSpots = procedure.getProcedureType().isCreateRefMap() && !procedureConfig.getPupilMaskType().isPupilMaskTypeSufs();
		
		
		fiResult = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots, fiConfig, procedure.getCurrentRefBeamMap(),
				subimageDefList.getSubimageDefListCentroids(), subimageDefList.getMissingSpotFlags(), findAllMaskSpots);

		logger.info("Find and Identify completed");

		try {

			computationLibrary.evalFiResult(fiResult, fiConfig, procedureConfig);

			centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);

			// test for non-linear subimage maximums
			computationLibrary.checkSubimageIntensities(centroidMap, physicalModel.getInstrument().getCcd()
					.getNonLinearThreshold());
			
			// test for ccd gain offset bias threshold exceeded
			int leftRightBiasThreshold = procedure.getProcedureConfigSet().getFrameCorrectionConfig().getLeftRightBiasThreshold();
			computationLibrary.checkFrameLeftRightBias(ccdFrame, leftRightBiasThreshold);

		} catch (FandIException e) {
			handleExceptionCases(e);
		}

	}
	
	private void handleExceptionCases(FandIException e) throws AbortProcedureException, Exception {

		try {
			throw e;
		} catch (UserAssistRequiredException e1) {
			handleUserAssistRequiredException(e1);
		} catch (NonLinearIntensitiesException e1) {
			handleNonLinearIntensitiesException(e1);
		} catch (CcdLeftRightBiasException e1) {
			handleCcdLeftRightBiasException(e1);
		} catch (HandMarkRequiredException e1) {
			handleHandMarking();
		} catch (FandIException e1) {
			// impossible, we never throw this
		}
	}
	
	private void handleUserAssistRequiredException(UserAssistRequiredException e) throws AbortProcedureException, Exception {

		StringBuffer buf = new StringBuffer(MessageGenerator.generateMessage("fandi.end.question"));
		if (e.isNdetectNotAllSingle()) {
			buf.append(MessageGenerator.generateMessage("fandi.ndetect_not_single"));
		}

		if (e.isFracThreshExceeded()) {
			buf.append(MessageGenerator.generateMessage("fandi.frac_vs_threshold", fiResult.getFracFilledBoxes(),
					fiConfig.getFracFilledThresh()));
		}

		if (e.isFracThreshExceededFindCent()) {
			buf.append(MessageGenerator.generateMessage("find_cent.frac_vs_threshold", e.getFracThreshExceededFindCent(),
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

		int response = userPromptMgmt.displayFlowControlTriFlowDialog("Procedure Exception", text);

		if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
			throw new AbortProcedureException("User Aborted Test");
		} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {

			if (e.isFracThreshExceededPT()) {
				handleHandMarking();
			} else {
			
				// fracThreshExceededFindCent is only thrown from findCent, so we don't need to find again
				if (!e.isFracThreshExceededFindCent()) {
					centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);				
				}
				procedureCcdFrame.setCentroidMap(centroidMap);
			}
			
		} else {
			takeFrameAndFindCentroids();
		}
	}

	private void handleNonLinearIntensitiesException(NonLinearIntensitiesException e) throws AbortProcedureException, Exception {

		String text = MessageGenerator.generateMessage("fandi.intensities.nonlinear");

		// user interaction
		statusLogger.log("procedure.exception", text);

		int response = userPromptMgmt.displayFlowControlTriFlowDialog("Procedure Exception", text);

		if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
			throw new AbortProcedureException("User Aborted Test");
		} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY) {
			// we get here if we are going to re-take frame (Retry)
			takeFrameAndFindCentroids();
		}

	}

	private void handleCcdLeftRightBiasException(CcdLeftRightBiasException e) throws AbortProcedureException, Exception {

		String text = MessageGenerator.generateMessage("frame.bias.threshold");

		// user interaction
		statusLogger.log("procedure.exception", text);

		int response = userPromptMgmt.displayFlowControlTriFlowDialog("Procedure Exception", text);

		if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
			throw new AbortProcedureException("User Aborted Test");
		} else if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY) {

			// we get here if we are going to trigger calibration and re-take frame (Retry)

			// trigger the offset calibration and get the new gain offsets
			statusLogger.log("ccd.cmd.calibration.start");

			int[] offsets = ccdMgmt.triggerOffsetCalibration();
			
			statusLogger.log("ccd.cmd.calibration.end", offsets[0], offsets[1]);
			
			// 2. store these values in CcdGain for the current gain value
			ccdDefMgmt.updateCcdGainOffsets(offsets);
				
			// retake frame
			takeFrameAndFindCentroids();
		}
		

	}

	private void handleHandMarking() throws AbortProcedureException, Exception {

		if (procedure.getProcedureType().isPassiveTilt()) {

			fiResult = handMark(procedure, fiConfig);
			centroidMap = findAndDisplayCentroids(procedure, fiConfig, fiResult);
			procedureCcdFrame.setCentroidMap(centroidMap);

		} 
			

		
	}




	// TODO: generalize this, does not need to be explicit in an executor

	/**
	 * Builds the centroid map
	 * @param findCentroidsResult result of findCentroids computation
	 * @param procedureConfig the procedure configuration
	 * @param fiConfig the find and identify configuration
	 * @param fiResult the result of the findAndIdentify computation
	 * @return the centroid map
	 */
	public CentroidMap buildCentroidMap(FindCentroidsResult findCentroidsResult, ProcedureConfig procedureConfig, FIConfig fiConfig, FIResult fiResult) throws Exception {

		CentroidMap centroidMap = new CentroidMap();
		String centroidMapData = FloatPointListEncoder.encodeList(Arrays.asList(findCentroidsResult.getCentroidList()));
		centroidMap.setCentroidMapData(centroidMapData);
		
		centroidMap.setFindCentroidsResult(findCentroidsResult);
		
		String intensityMapData = FloatListEncoder.encodeList(findCentroidsResult.getIntensityList());
		centroidMap.setIntensityMapData(intensityMapData);
		
		String peakMapData = FloatListEncoder.encodeList(findCentroidsResult.getPeakList());
		centroidMap.setPeakMapData(peakMapData);
		
		String rawPeakMapData = FloatListEncoder.encodeList(findCentroidsResult.getRawPeakList());
		centroidMap.setRawPeakMapData(rawPeakMapData);
		
		float medianPeakIntensity = computationLibrary.getMedianValue(findCentroidsResult.generateGoodPeakList());
		centroidMap.setMedianPeakIntensity(medianPeakIntensity);
		
		String findCentStatusData = IntegerListEncoder.encodeList(findCentroidsResult.getFindCentStatusList());
		centroidMap.setFindCentStatusData(findCentStatusData);
		
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
		
		centroidMap.setEmptyBoxCount(fiResult.getN0123()[0]);
		centroidMap.setSingleDetectBoxCount(fiResult.getN0123()[1]);
		centroidMap.setDoubleDetectBoxCount(fiResult.getN0123()[2]);
		centroidMap.setManyDetectBoxCount(fiResult.getN0123()[3]);
		

		String fandiPredictedCentroidMapData = FloatPointListEncoder.encodeList(FloatPointListEncoder.constructFromXandY(fiResult.getXiRst(), fiResult.getYiRst()));
		centroidMap.setFandiPredictedCentroidMapData(fandiPredictedCentroidMapData);
		
		String fandiPeakCentroidMapData = FloatPointListEncoder.encodeList(FloatPointListEncoder.constructFromXandY(fiResult.getxPeak(), fiResult.getyPeak()));
		centroidMap.setFandiPeakCentroidMapData(fandiPeakCentroidMapData);

		String nDetectData = IntegerListEncoder.encodeList(fiResult.getnDetect());
		centroidMap.setnDetectData(nDetectData);

		centroidMap.setTranslationSolutionCount(fiResult.getnSolution());

		return centroidMap;
	}

	private CentroidMap findAndDisplayCentroids(Procedure procedure, FIConfig fiConfig, FIResult fiResult) throws Exception {


		try {

			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
	
			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			ProcedureCcdFrame procedureCcdFrame = procedure.getLatestProcedureCcdFrame();
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
	
			SubimageDefList subimageDefList = null;
			if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs() && !procedure.getProcedureType().isCreateRefMap()) {
				subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getSufsGroup());
			} else {
				subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());			
			}
			
			boolean findAllMaskSpots = procedure.getProcedureType().isCreateRefMap() && !procedureConfig.getPupilMaskType().isPupilMaskTypeSufs();

			centroidMap = null;
			try {
	
				findCentroidsResult = computationLibrary.findCentroids(ccdFrame.getCorrectedFrame(), fiResult, 
						procedure.getProcedureConfigSet().getFindCentConfigInterior(), 
						procedure.getProcedureConfigSet().getFindCentConfigPeripheral(), 
						subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), findAllMaskSpots);
				
				// if passive tilt hand-mark, we want to use the hand-marked location for any spots that failed - as long as it is not missing due to incomplete mirror
				if (fiResult.isHandMarked()) {
					// fiResult is a hand-marked result
					
					for (int i=0; i<findCentroidsResult.getCentroidList().length; i++) {
						// if the findCent result is zero and the mirror is actually there, use the hand-marked location
						if (findCentroidsResult.getFoundSubimageFlags()[i] == 0 && 
								procedure.getProcedureConfigSet().getGlobalConfig().getMirrorListInt()[i] != 0) {
							
							Subimage markedSubimage = new Subimage(fiResult.getPeakLocationArray()[i], 0.0f, 0.0f, 0.0f, Constants.FIND_CENT_STATUS_SUCCESS);
							findCentroidsResult.setSubimage(i, markedSubimage);
						}
					}
				}
				
				centroidMap = buildCentroidMap(findCentroidsResult, procedureConfig, fiConfig, fiResult);
	
				procedureCcdFrame.setCentroidMap(centroidMap);

				// display the marked frame
				frameDisplayMgmt.setMarking(Arrays.asList(findCentroidsResult.getCentroidList()));
							
				frameDisplayMgmt.displayMarkedFrame();
				
				if (procedureConfig.isAutoDisplayCentroids()) {
					graphicDisplayMgmt.displaySubimageCentroids(centroidMap);
				}

				// use median peak intensity converted to ADU
				float medianPeakIntensityAdu = centroidMap.getMedianPeakIntensity() * physicalModel.getInstrument().getCcd().getCcdGain().getGainValue();
				
				// display warning if subimageIntensityThreshold is not reached
				if (medianPeakIntensityAdu < procedure.getProcedureConfigSet().getFindCentConfigInterior().getSubimageIntensityThreshold() && 
						procedureConfig.isAutoDisplaySubimageIntensityWarning()) {
					String warningMessage = MessageGenerator.generateMessage("find_cent.subimage_intensity_warning", medianPeakIntensityAdu, 
							procedure.getProcedureConfigSet().getFindCentConfigInterior().getSubimageIntensityThreshold());
					userPromptMgmt.displayInfoDialog("Subimage Intensity Warning", warningMessage);
				}
				
				// display warning if image rotation from most recent ref map exceeds threshold
				
				RefBeamMap newestRefBeamMap = null;
				
				if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs()) {
				
					newestRefBeamMap = centroidMapMgmt.getNewestRefBeamMap(physicalModel.getInstrument().getInstrumentId(), 
						procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getSufsGroup());
				
				} else {
					newestRefBeamMap = centroidMapMgmt.getNewestRefBeamMap(physicalModel.getInstrument().getInstrumentId(), 
							procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), -1);
					
				}
				
				
				if (newestRefBeamMap != null) {
					float delta = Math.abs(newestRefBeamMap.getCentroidMap().getRotation() - centroidMap.getRotation());
					
					float threshold = constantsCache.getMaskConstants().getMaskRotationDifferenceThreshold();
					
					if (delta > threshold) {
						
						String warningMessage = MessageGenerator.generateMessage("find_cent.image_rotation_warning",  delta);
						userPromptMgmt.displayInfoDialog("Subimage Rotation Warning", warningMessage);
	
					}
				}
				
				
				// test for fracFilledThresh failed because of findCent
				   int expectedSpotCount = 0;

				   if (procedure.getProcedureType().isCreateRefMap()) {
					//SUFS subimageDefList being used is a special list just for reference maps 
                      if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs()) {
                	     expectedSpotCount = subimageDefList.fandiExpectedSpotCount();
                       } else {
                	     expectedSpotCount = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots() ; 
                       }                   
                      expectedSpotCount = 	subimageDefList.fandiExpectedSpotCount();
				  }
					
				
				// add any other missed spots from findCentroids
				float findCentFilledBoxes = fiResult.getNumFilledBoxes() - findCentroidsResult.missedSpots();
				float findCentFracFilled = findCentFilledBoxes/expectedSpotCount;
				
				if (findCentFracFilled < fiConfig.getFracFilledThresh() && fiResult.getFracFilledBoxes() >= fiConfig.getFracFilledThresh()) {
					
					statusLogger.log("find_cent.frac_vs_threshold", findCentFracFilled, fiConfig.getFracFilledThresh());
							
					if (procedure.getProcedureType().isCreateRefMap()) {
						// if this is a ref beam map, just fail
						String text = MessageGenerator.generateMessage("find_cent.frac_vs_threshold", findCentFracFilled, fiConfig.getFracFilledThresh());
						throw new Exception(text);
					} else {
						// otherwise create and throw a user assist exception
						UserAssistRequiredException uare = new UserAssistRequiredException();
						uare.setFracThreshExceededFindCent(findCentFracFilled);
						throw uare;
					}
				}
				
			} catch (UserAssistRequiredException e) {
				throw e;
			} catch (Exception e) {
				if (procedure.getProcedureType().isPassiveTilt()) {
					throw new HandMarkRequiredException();
				} else {
					throw e;
				}
			}
	
			// if PassiveTilt ask the user if the correct centroids have been found
			if (procedure.getProcedureType().isPassiveTilt()) {
			
				// inform the user if the number of spots found is different than the expected total
				if (findCentroidsResult.foundSpotCount() != procedure.getProcedureConfigSet().getGlobalConfig().getMirrorCount()) {
					
					String warningMessage = MessageGenerator.generateMessage("find_cent.subimage_count_warning", findCentroidsResult.foundSpotCount(), 
							procedure.getProcedureConfigSet().getGlobalConfig().getMirrorCount());
					userPromptMgmt.displayInfoDialog("Centroid Count Warning", warningMessage);
					
				}
				
				
				boolean userResponse = graphicDisplayMgmt.displaySubimageCentroids(centroidMap, UserPrompt.PROMPT_TYPE_YES_NO,
						"Have the correct centroids been found?");
		
				// as part of the display, ask the user if it is OK (only passive tilt)
				// throw a UserAssistException if they don't like it.
				if (!userResponse) {
					throw new HandMarkRequiredException();
				}
			}
			

		} catch (FandIException e1) {

			handleExceptionCases(e1);
		}

		return centroidMap;
	}

	FIResult handMark(Procedure procedure, FIConfig fiConfig) throws AbortProcedureException {

		List<FloatPoint> handMarked = null;
		ProcedureCcdFrame procedureCcdFrame = procedure.getLatestProcedureCcdFrame();
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
		int mirrorCount = procedure.getProcedureConfigSet().getGlobalConfig().getMirrorCount();
		Integer[] mirrorConfig = procedure.getProcedureConfigSet().getGlobalConfig().getMirrorList();
		

		while (true) {
			frameDisplayMgmt.displayFrame(MessageGenerator.generateMessage("instructions.pt_hand_mark"), "PTNumbering.jpg");
			frameDisplayMgmt.clearMarking();
			frameDisplayMgmt.setPendingMarkAction(true);
			// wait for user to mark frame
			statusLogger.log("frame.mark_waiting");

			try {
				while (frameDisplayMgmt.getPendingMarkAction()) {
					Thread.sleep(500);
				}
			} catch (InterruptedException e) {
			}

			// get marking data from the frame display
			handMarked = frameDisplayMgmt.getMarkList();

			if (procedureExecutionState.getAbortRequested()) {
				throw new AbortProcedureException("User Aborted Procedure");
			}
			
			if (handMarked.size() == mirrorCount) {
				break;
			} else {
				userPromptMgmt.displayInfoDialog("Frame Marking Error", MessageGenerator.generateMessage("frame.mark_incorrect_number"));
			}
		}

		return new FIResult(handMarked, ccdFrame.getCorrectedFrame(), mirrorConfig);

	}
}
