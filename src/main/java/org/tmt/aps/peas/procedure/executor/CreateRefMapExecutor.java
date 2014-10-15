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

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
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
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

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
			FloatPoint[] centroids = computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), numSpots);
			
			statusLogger.log("fandi.end.success");
			
			// display the marked frame
			frameDisplayMgmt.setMarking(centroids);
			frameDisplayMgmt.displayMarkedFrame();
			
			graphicDisplayMgmt.displaySubimageCentroids(Arrays.asList(FloatPoint.roundToPoint(centroids)));

			statusLogger.log("procedure.refmap.created");                

			// TODO: remove this: just to have something to save
			boolean saveMap = true;
			procedureOutput.setMapSaved(saveMap);
			
			// save the reference beam map
			RefBeamMap refBeamMap = refBeamMapMgmt.saveRefBeamMap(Arrays.asList(centroids), procedure);
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

}
