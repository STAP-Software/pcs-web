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
import org.tmt.aps.peas.refBeamMap.business.RefBeamMapMgmt;
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
	private RefBeamMapMgmt refBeamMapMgmt;

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
			
			// display the marked frame
			frameDisplayMgmt.setMarking(centroids);
			frameDisplayMgmt.displayMarkedFrame();

			// TODO: supply real numbers for the status logger outputs
			statusLogger.log("fandi.search_count", 1);
			statusLogger.log("fandi.frame_scale", 0.9530617);
			statusLogger.log("fandi.rotation", 0.3370904);
			statusLogger.log("fandi.frame_scale", 0.9840439);
			statusLogger.log("fandi.center_calc");
			
			statusLogger.log("fandi.end.success");

			graphicDisplayMgmt.displaySubimageCentroids(Arrays.asList(FloatPoint.roundToPoint(centroids)));

			statusLogger.log("procedure.refmap.created");                

			// just to have something to save to test with, ask Mitch what we really want
			boolean saveMap = true;
			procedureOutput.setMapSaved(saveMap);
			
			// TODO: we need to decide what to do with this code.  If offsets are required, perhaps we apply them when we use the maps
			// not when they are created.  That way we always know that refMaps are the actual centroids in all cases.
			
			/*
			IF (ZREFMAP_REF_TYPE.EQ.MASK_MENU_PH) THEN
			C
			C  We offset the 651, 891, 852, and 870 reference beam images
			C  because the corresponding reference beams themselves are
			C  physically displaced in the focal plane.  Otherwise the
			C  operator will have to keep moving the telescope to compensate
			C  for this physical offset.  The numerical offsets below were
			C  determined in pixels and then converted to arcseconds so that
			C  when we change CCDs the offsets will still be correct.
			C
			C  GC and MT (24 Jan 95)
			C

			           IF (REF_FILTER_NUMBER.EQ.FILT_POS_651) THEN

			              FILTER_OFFSET_X = -15.07 * 0.1876 / SECPERPIX_160
			              FILTER_OFFSET_Y = 0.74 * 0.1876 / SECPERPIX_160
			C Uses ref beam 3/ 890 nm
			           ELSE IF ((REF_FILTER_NUMBER.EQ.FILT_POS_891).OR.
			     +               (REF_FILTER_NUMBER.EQ.FILT_POS_852).OR.
			     +               (REF_FILTER_NUMBER.EQ.FILT_POS_870)) THEN 
			              FILTER_OFFSET_X = 14.47 * 0.1876 / SECPERPIX_160
			              FILTER_OFFSET_Y = 1.63 * 0.1876 / SECPERPIX_160
			           ENDIF

			           IF ((REF_FILTER_NUMBER.EQ.FILT_POS_651).OR.
			     +        (REF_FILTER_NUMBER.EQ.FILT_POS_891).OR.
			     +        (REF_FILTER_NUMBER.EQ.FILT_POS_852).OR.
			     +        (REF_FILTER_NUMBER.EQ.FILT_POS_870)) THEN 
			             DO J = 1, 203
			                CENTROID(J,1) = CENTROID(J,1)  - FILTER_OFFSET_X
			                CENTROID(J,2) = CENTROID(J,2)  - FILTER_OFFSET_Y
			             ENDDO
			           ENDIF
			        ENDIF
			*/
			
			
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
