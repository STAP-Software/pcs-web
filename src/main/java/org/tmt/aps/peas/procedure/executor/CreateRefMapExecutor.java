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
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
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
			
			procedureExecutionMgmt.performProcedureStartup(procedure);
			
			
			// TODO: frame simulation mode sets iterations = 1 (why?) - this should also be part of form validation

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			
			// TODO: what is this message for??
			statusLogger.log("camera.not_init"); 

			 // TODO: mask chosen will determine the type of ref beam map
			 // TODO: the integration time is given by the filter and mask type chosen - this will be a new table
			 //       This should probably be a default given that can be overridden by the operator.  Not sure who should be able to override, though.
			 // Spot_Count is numSpots from pupilMaskType
			
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
				Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(procedure.getGlobalConfig().getCoarseMirrorDefault());

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
		        	ReferenceBeam refBeam = physicalModel.getInstrument().getCamera().getReferenceBeamByWavelength(procedureConfig.getFilter().getWavelength());
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
		        Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture, coarseMirrorCommandFuture);
	        	statusLogger.log("camera.cmd.complete");
				
			}
	
			statusLogger.log("frame.get");
			
			ProcedureCcdFrame procedureCcdFrame = frameMgmt.getProcedureCcdFrame(procedureConfig, procedure.getProcedureType(), procedure.getProcedureNumber(), 
					0, 0, procedureConfig.getIntegrationTime(), physicalModel.getInstrument().getCcd().getAllHotPixelRects(), procedure.getGlobalConfig().isRemoveBadPixels());
			CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
			
			// TODO: this is where we display the frame
			// tell the async controller to update the frame
			frameDisplayMgmt.displayFrame();
			
			 
			statusLogger.log("fandi.start");
			
			List<Point> subimageList = null;
			
			// TODO: use NumSpots and maybe findAndIdentify should take an array of FloatPoints
			float centroids[][] = new float [36][2];
			computationLibrary.findAndIdentify(ccdFrame.getCorrectedFrame(), centroids);
			
			// TODO: this is where we display the marked frame
			frameDisplayMgmt.displayMarkedFrame();

			statusLogger.log("fandi.search_count", 1);
			statusLogger.log("fandi.frame_scale", 0.9530617);
			statusLogger.log("fandi.rotation", 0.3370904);
			statusLogger.log("fandi.frame_scale", 0.9840439);
			statusLogger.log("fandi.center_calc");
			
			statusLogger.log("fandi.end.success");

			graphicDisplayMgmt.displaySubimageCentroids(subimageList);

			

		// TODO: do a status log of the following
        // This test has successfully completed\n and the reference beam map is created.\nThe map is now ready to save to disk.                  

		 
		// TODO: test against auto-save-map option, ask user if they want to save the map if needed
		
		// TODO: implement save 
        //  CALL REF_MAP_SAVE_INIT()
	   	//  CALL SAVE_REF_MAP(ZREFMAP_SAVE_FILE)

                                                                       
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

	private void wait(int ms) {
		// here we wait until the pending display is cleared
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {

		}

	}

}
