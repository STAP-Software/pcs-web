/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;


import java.util.ArrayList;
import java.util.Date;
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
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class CreateRefMapExecutor {

	Logger logger = Logger.getLogger(this.getClass());


	@EJB
	private CameraMgmt cameraMgmt;
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
	private PhysicalModel physicalModel;
	@EJB
	private CentroidMapMgmt refBeamMapMgmt;

	private List<String> logMessages;

	public List<String> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<String> logMessages) {
		this.logMessages = logMessages;
	}

	@PostConstruct
	void init() {
		logger.debug("CreateRefMapExecutor::PostConstruct::");
	}
	
	@Asynchronous
	public Future<?> testMethod() {
		logger.debug("CreateRefMapExecutor::testMethod::");
		return null;
	}
	
	@Asynchronous
	public void executeProcedure(Procedure procedure, Session currentSession) {

		logger.info("CreateRefMapExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfig();
			
			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			CreateRefBeamMapProcedureOutput procedureOutput = (CreateRefBeamMapProcedureOutput)procedure.getProcedureOutput();
			
			procedureExecutionMgmt.performProcedureStartup(procedure);
			
			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			
						
		    // TODO: Special logic for SUFS                                                    
			/*
			IF (ZREFMAP_REF_TYPE.EQ.MASK_MENU_SUFS) THEN
		           REF_SUFS_GROUP = CURRENT_SUFS_GROUP

			END IF
			*/

			
			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {

				// TODO: SUFS Specific code
				/*
				IF (ZREFMAP_REF_TYPE.EQ.MASK_MENU_SUFS) THEN  ! SUfs specific code
		           OK = SUFS_GROUP_SELECT(ZREFMAP_GROUP)
		           IF (.NOT.OK) THEN
		              TEXT = 'Group not positioned correctly error.'
		              CALL DISP_WRITE(TEXT, LEN(TEXT))

		              GOTO 900
		           END IF
		        END IF
				*/
				
				// TODO: Special logic for selecting which ref beam for UFS/SUFS
				/*
				IF (ZREFMAP_REF_TYPE.EQ.MASK_MENU_SUFS) THEN
					IF(ZREFMAP_REF.GT.9) THEN
						REFNUM = 'F'
					ELSE
						WRITE(UNIT=REFNUM, FMT='(I1)') ZREFMAP_REF
					END IF
					OK = ACTIVATE_REF_BEAM(REFNUM)
				END IF
				*/
				
				// always command the coarse mirror to setup values at the start of all procedures
				statusLogger.log("camera.cmd.coarse_mirror", procedure.getGlobalConfig().getCoarseMirrorDefault());
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getGlobalConfig().getCoarseMirrorDefault());

				Future<Integer> twoPosCommandFuture = null;
				Future<Integer> refBeamFuture = null;
				// command to mask selected
				statusLogger.log("camera.cmd.pupil_wheel", procedureConfig.getPupilMask().getWheelPosition());
				Future<Integer> pupilMaskCommandFuture = cameraMgmt.commandPupilMask(procedureConfig.getPupilMask().getWheelPosition());
				// command to filter selected
				statusLogger.log("camera.cmd.filter_wheel", procedureConfig.getFilter().getWheelPosition());
				Future<Integer> filterCommandFuture = cameraMgmt.commandFilterWheel(procedureConfig.getFilter().getWheelPosition());
		        	
		        // turn on ref beam
		        ReferenceBeam refBeam = physicalModel.getInstrument().getCamera().getReferenceBeamByWavelength(procedureConfig.getFilter().getWavelength());
				statusLogger.log("camera.cmd.ref_beam", refBeam.getRefBeamNum());
		        refBeamFuture = cameraMgmt.commandReferenceBeamState(refBeam.getRefBeamNum()); 

				// extend two pos mirror
		        statusLogger.log("camera.cmd.two_pos_device", "extend");
		        twoPosCommandFuture = cameraMgmt.commandTwoPositionDevice(CameraCommand.EXTENDED);
			
				// wait for all commands to complete
		        Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture, coarseMirrorCommandFuture);
	        	statusLogger.log("camera.cmd.complete");
				
			}
	
			statusLogger.log("frame.get");
			
			ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(), procedure.getProcedureNumber(), 
					0, 0, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd().getAllHotPixelRects(), procedure.getGlobalConfig().isRemoveBadPixels());
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
			
			// tell the async controller to update the frame
			frameDisplayMgmt.displayFrame();
						 
			statusLogger.log("fandi.start");
			
			// use NumSpots and maybe findAndIdentify should take an array of FloatPoints			
			int numSpots = procedureConfig.getPupilMask().getPupilMaskType().getNumSpots();
			
			FIResult fiResult = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots, procedure.getFiConfigActual(), null);
			List<FloatPoint> centroids = new ArrayList<FloatPoint>();
			try {
				centroids = computationLibrary.findCentroids(ccdFrame.getCorrectedFrame(), fiResult, procedure.getFindCentConfig());
			} catch (Exception e) {
				CentroidMap centroidMap = buildCentroidMap(centroids, procedureConfig, procedure.getFiConfigActual(), fiResult);
				procedureCcdFrame.setCentroidMap(centroidMap);
				throw e;
			}
			CentroidMap centroidMap = buildCentroidMap(centroids, procedureConfig, procedure.getFiConfigActual(), fiResult);
			procedureCcdFrame.setCentroidMap(centroidMap);
			
			statusLogger.log("fandi.end.success");
			
			// display the marked frame
			frameDisplayMgmt.setMarking(centroids);
			frameDisplayMgmt.displayMarkedFrame();
			
			graphicDisplayMgmt.displaySubimageCentroids(FloatPointListEncoder.roundToPoint(centroids));
			
			try {
				computationLibrary.evalFiResult(fiResult, procedure.getFiConfigActual(), procedureConfig);
				
			} catch (UserAssistRequiredException e) {
				
				
				StringBuffer buf = new StringBuffer(MessageGenerator.generateMessage("fandi.end.question"));
				if (e.isNdetectNotAllSingle()) {
					buf.append(MessageGenerator.generateMessage("fandi.ndetect_not_single"));
				}
				
				if (e.isFracThreshExceeded()) {
					buf.append(MessageGenerator.generateMessage("fandi.frac_vs_threshold", fiResult.getFracFilledBoxes(), procedure.getFiConfigActual().getFracFilledThresh()));
				}
				
				if (e.isFourierThreshExceeded()) {
					buf.append(MessageGenerator.generateMessage("fandi.fourqual_vs_threshold", fiResult.getFourierQuality(), procedure.getFiConfigActual().getFourierQualityThresh()));					
				}
				
				String text = buf.toString();
				
				// user interaction
				statusLogger.log("procedure.exception", text);

				int response = userPromptMgmt.displayFlowControlDialog(text);
				
				if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					throw new Exception("User Aborted Test");
				}

				// TODO: handle re-taking frame
				
			}
						


			statusLogger.log("procedure.refmap.created");                

			// TODO: remove this: just to have something to save
			boolean saveMap = true;
			procedureOutput.setMapSaved(saveMap);
			
			// save the reference beam map
			RefBeamMap refBeamMap = buildRefMap(centroidMap, procedure);
			procedure.setRefBeamMap(refBeamMap);
                                                                       
			// TODO: if SUFS, then Home the coarse mirror 
			// CALL UFS_SEGMENT_SELECT(0)
			// CALL UFS_SEG_POS_WRITE
			
								
			int trialPct = (int) ((((1)*100)/procedureConfig.getNumberOfTrials()) * 0.95);
				
			procedureExecutionState.setPercentComplete(trialPct);
						
			statusLogger.log("procedure.end",  procedure.getProcedureType().getProcedureTypeName());

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
	
	// TODO: generalize this, does not need to be explicit in an executor
	public CentroidMap buildCentroidMap(List<FloatPoint> centroids, ProcedureConfig procedureConfig, FIConfig fiConfig, FIResult fiResult) {
		
		CentroidMap centroidMap = new CentroidMap();
		String centroidMapData = FloatPointListEncoder.encodeList(centroids);
		centroidMap.setCentroidMapData(centroidMapData);
		
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
	
	public RefBeamMap buildRefMap(CentroidMap centroidMap, Procedure procedure) {
		RefBeamMap refBeamMap = new RefBeamMap();
		refBeamMap.setCreateDate(new Date());
		
		refBeamMap.setInstrumentId(procedure.getInstrument().getInstrumentId());
		refBeamMap.setFilterTypeId(procedure.getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		
		//centroidMap.setCreateDate(new Date());
		
		refBeamMap.setCentroidMap(centroidMap);
		refBeamMap.setRefBeamDefMapFlg(false);

		return refBeamMap;
	}
}
