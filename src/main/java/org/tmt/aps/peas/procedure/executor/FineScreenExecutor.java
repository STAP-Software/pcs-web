/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;

import java.util.Arrays;
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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltIterationOutput;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
@Startup
public class FineScreenExecutor {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private CameraMgmt cameraMgmt;
	@EJB
	private AcsMgmt acsMgmt;
	@EJB
	private DcsMgmt dcsMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private ImageProcessor imageProcessor;
	@EJB
	private GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	private ReadyCamera readyCamera;
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
	PhysicalModel physicalModel;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;
	@EJB
	private CenterTelescopeCalc centerTelescopeCalc;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB
	private ConstantsCache constantsCache;
	@EJB
	private SubimageDefCache subimageDefCache;
	@EJB
	private CreateRefMapExecutor createRefMapExecutor;

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

		logger.info("Fine Screen Executor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			GlobalConfig globalConfig = procedure.getProcedureConfigSet().getGlobalConfig();

			ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

			PassiveTiltProcedureOutput procedureOutput = (PassiveTiltProcedureOutput) procedure.getProcedureOutput();

			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			statusLogger.log("camera.not_init");

			RefBeamMap currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);

			if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD || currentRefMap == null) {

				boolean autoTakeRefMap = false;
				if (currentRefMap == null) {
					autoTakeRefMap = true;
				} else if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_NO) {
					autoTakeRefMap = false;
				} else {

					try {
						
						computationLibrary.autoRefMapCheck(procedure.getProcedureConfigSet().getAutoRefMapConfig(), globalConfig.getCoarseMirrorDefault(), 
								globalConfig.getFineMirrorDefault(), physicalModel.getInstrument().getCcd().getTemperature(), 1, new Date(), currentRefMap);

					} catch (AutoRefMapCheckException e) {

						statusLogger.log(e.getKey(), e.getArg1(), e.getArg2());

						if (procedureConfig.getAutoTakeRefBeam() == Constants.AUTO_TAKE_REF_MAPS_PROMPT) {
							// prompt user
							autoTakeRefMap = userPromptMgmt.displayYesNoDialog(e.getText() + "\nTake new Ref Map?");

						} else {
							autoTakeRefMap = true;
						}
					}
				}

				if (autoTakeRefMap) {

					CreateRefBeamMapProcedureOutput po = new CreateRefBeamMapProcedureOutput();
					Procedure subProcedure = procedureExecutionMgmt.performProcedureSetup(
							ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP, currentSession.getSessionId(), 
							procedure.getTestNumber(), po);

					procedureExecutionMgmt.performProcedureStartup(subProcedure, null);

					procedureExecutionState.setPendingSubProcedure(subProcedure);

					// execute the subprocedure
					createRefMapExecutor.executeSynchronousProcedure(subProcedure, currentSession);

					currentRefMap = centroidMapMgmt.getCurrentRefBeamMap(physicalModel.getInstrument().getInstrumentId(), procedureConfig
							.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), procedureConfig.getFilter().getFilterType()
							.getFilterTypeId(), -1);


				}
			}

			procedure.setRefBeamMap(currentRefMap);

			logger.debug("light source 1 = " + procedureConfig.getLightSource());
			
			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCamera.execute(procedure);
			
			statusLogger.log("procedure.using_curr_frame");
			statusLogger.log("procedure.trials", procedureConfig.getNumberOfTrials());

			logger.debug("light source 2 = " + procedureConfig.getLightSource());

			
			/*
			 * 
			         
        
        // TODO: what is a work message dialog??
        // perhaps we would like to update the status panel to contain current messages along with % complete.
        CALL CREATE_WORK_MESSAGE_DIALOG()
        TEXT = 'Fine Screen, Initialization'



C If needed auto point telesope back to segment 0
	IF (.NOT.AUTO_POINT_TELESCOPE(FS_TEST,0)) GOTO 900
                                                                               

	// ready camera
	// auto ref map call


	DO I = 1, NUMBER_TRIALS
	
           WRITE(CI, FMT='(I2)') I
           WRITE(CNT, FMT='(I2)') NUMBER_TRIALS
           TEXT = 'Fine Screen, Loop '//CI//' of '//CNT

           CALL FUPDATE_WORK_MESSAGE(TEXT, LEN(TEXT))
           
           // TODO: allow user to abort procedure inside this loop.
           
           
          // TODO: call get corrected frame

		  // TODO: call find and identify, findAllCentroids

          // TODO: optionally display centroids

		  // TODO: call pupil_registration for fine screen, then call CENTER_PUPIL
          
          
		  // TODO: if the 'auto' pupil registration fails, then go into 'manual' mod  

	      TEXT = 'Error Running Auto. Pupil Registration'
	      CALL DISP_WRITE(TEXT, LEN(TEXT))

	      CALL FWARN_DIALOG(TEXT, LEN(TEXT), M_DIALOG)

              IF (ZFINE_FRAME_SOURCE.EQ.CCD) THEN
	         TEXT = 'Error Running Auto. Pupil Registration'//NL//
     +                'Starting  Manual Registration'
	         CALL DISP_WRITE(TEXT, LEN(TEXT))

	         CALL FWARN_DIALOG(TEXT, LEN(TEXT), M_DIALOG)

                 CALL CENTER_PUPIL_MANUAL
              ENDIF
	  
	  // TODO: in either case, check if PR is out of tolerance (FRAME_OK)
C
C Is Frame Ok?
C
           IF (.NOT.FRAME_OK) THEN
	      TEXT = 'Pupil Registration Out of Tolerance'
	      CALL DISP_WRITE(TEXT, LEN(TEXT))

C	      OK = FYNWARN_DIALOG(TEXT, 'Re-Take Frame', 'Continue', YES,
C     +             M_DIALOG)
	      OK = FYNWARN_DIALOG(TEXT, LEN(TEXT),
     +    'Re-Take Frame', LEN('Re-Take Frame'), 
     +    'Continue', LEN('Continue'), YES, M_DIALOG)
              IF (OK) GOTO 100
	   ENDIF

		// TODO: calculate centroid offsets and optionally display them

		// TODO: locally store offsets for this trial, plus image translation, rotation and scale

	END DO ! End do of loop over N frames

	// TODO: average all offsets over all iterations

	// TODO: call calculate_centroid_stats on the average

	// TODO: log imageRotation, imageScale, rrmsTotal, focusError, 50% enclosed energy, 80% enclosed energy

	// TODO: optionally display avg offsets

	
	// FIXME: Do we still do this? 
	// FIXME: the following code is poorly written with too much cut and paste.  
    IF (ZFINE_CALC_MODE.EQ.CALC_MODE_PASSIVE) THEN

    	CALL CONVERT_FINE_TO_PASSIVE

	   	REF_PUPIL_NUMBER = POS_36
        PASSIVE_TILT_TEST = .TRUE.
	   	CALL CALCULATE_CENTROID_STATS(OFFSETS)

		// TODO: log pseudo pt rms, focus, e80, e50, etc

		// TODO: optionally display pseudo passive tilt centroid offsets

		// TODO: call actuator_lengths
        ACTUATOR_LENGTHS(ACT_OPTION, ZFINE_AUTODISP_ACT, ZFINE_AUTOPRIMACT)
	   
	   ZPROCLOG_DATA_ACS_FOCUS = ZPROCLOG_DATA_PRIMARY_ACT_FM_RMS/41.1

    ELSE IF (ZFINE_CALC_MODE.EQ.CALC_MODE_SECONDARY) THEN
                                                                                
        TEXT = 'Calculating Segment Zernikes'
        CALL DISP_WRITE(TEXT, LEN(TEXT))

        CALL CALCULATE_ZERNIKE

	   	IF(ZFINE_CALC_CHOICE.EQ.0) GOTO 200
	    IF(ZFINE_CALC_CHOICE.EQ.1) ACT_OPTION = 1  ! Piston
	    IF(ZFINE_CALC_CHOICE.EQ.2) ACT_OPTION = 2  ! Tilt
	    IF(ZFINE_CALC_CHOICE.EQ.3) ACT_OPTION = 3  ! Both
                                                                  
        TEXT = 'Calculating Secondary Actuators'
        CALL DISP_WRITE(TEXT, LEN(TEXT))

        SECONDARY_ACTUATOR(ACT_OPTION, ZFINE_AUTOSECONDACT, ZFINE_AUTOCENTERTEL)
           
           
		// FIXME: we do not modify values.  We create new ones.
C Secondary_actuators modifies the offsets to account for the secondary misplacment. 
C So, we need to recalculate the zernike's

		// FIXME: probably don't have to do this
C Let's save the original zernike's.
	   DO I = 1, 36
	      DO J = 1, 15
	         ORIGINAL_ZERNIKE(I,J) = SEGMENT_ZERNIKE(I,J)
	      ENDDO
	   ENDDO 

 
           TEXT = 'Calculating Segment Zernikes'
           CALL DISP_WRITE(TEXT, LEN(TEXT))

           CALL CALCULATE_ZERNIKE                            

	ELSE ! Calc both primary and passive 

           TEXT = 'Calculating Segment Zernikes'

           CALL DISP_WRITE(TEXT, LEN(TEXT))

           CALL CALCULATE_ZERNIKE

                
	   IF(ZFINE_CALC_CHOICE.EQ.0) GOTO 200
	   IF(ZFINE_CALC_CHOICE.EQ.1) ACT_OPTION = 1  ! Piston
	   IF(ZFINE_CALC_CHOICE.EQ.2) ACT_OPTION = 2  ! Tilt
	   IF(ZFINE_CALC_CHOICE.EQ.3) ACT_OPTION = 3  ! Both
                                                                  
           TEXT = 'Calculating Secondary Actuators'

           CALL DISP_WRITE(TEXT, LEN(TEXT))

           OK = SECONDARY_ACTUATOR(ACT_OPTION, ZFINE_AUTOSECONDACT, ZFINE_AUTOCENTERTEL)
           IF (.NOT.OK) GOTO 600

C
C Secondary_actuators modifies the offsets to account for the
C secondary misplacment. So, we need to recalculate the zernike's
C
C
C Let's save the original zernike's.
C

	   DO I = 1, 36
	      DO J = 1, 15
	         ORIGINAL_ZERNIKE(I,J) = SEGMENT_ZERNIKE(I,J)
	      ENDDO
	   ENDDO 
           TEXT = 'Calculating Segment Zernikes'

           CALL DISP_WRITE(TEXT, LEN(TEXT))

           CALL CALCULATE_ZERNIKE

C
C Passive tilt stuff
C
           CALL CONVERT_FINE_TO_PASSIVE

	   REF_PUPIL_NUMBER = POS_36
           PASSIVE_TILT_TEST = .TRUE.
C
C	   calculate ACS FM
C
	   CALL CALCULATE_CENTROID_STATS(OFFSETS)

           ZPROCLOG_DATA_CENTOFF_PSEUDO_RMS = RRMS_TOTAL
           ZPROCLOG_DATA_CENTOFF_PSEUDO_FOCUS = FOCUS_ERROR 
           ZPROCLOG_DATA_CENTOFF_PSEUDO_E80 = ENCLOSED_ENERGY 
           ZPROCLOG_DATA_CENTOFF_PSEUDO_E50 = ENCLOSED_50_ENERGY 
c	   ZPROCLOG_DATA.ACS_FOCUS = FOCUS_ERROR / PCSFOCUSTOACS

C PCSP
C Conditional must evaluate to logical
C           IF (ZFINE_AUTODISP_AVG_OFFSETS) THEN
           IF (ZFINE_AUTODISP_AVG_OFFSETS .NE. 0) THEN
C PCSP END

	      CALL DISPLAY_CENTROID_OFFSETS(OFFSETS, IMAGE_TRANSLATION,
     +          IMAGE_ROTATION, IMAGE_SCALE, 0)
	   ENDIF

	   DO J=1,36       ! Store pseudo pt offsets
           OFFSETS_PT(J,1) = OFFSETS(J,1)
           OFFSETS_PT(J,2) = OFFSETS(J,2)
       ENDDO
       IMAGE_TRANS_PT(1) = IMAGE_TRANSLATION(1)
       IMAGE_TRANS_PT(2) = IMAGE_TRANSLATION(2)
       IMAGE_ROTATION_PT = IMAGE_ROTATION
       IMAGE_SCALE_PT = IMAGE_SCALE


        PASSIVE_TILT_TEST = .FALSE.

        OK = ACTUATOR_LENGTHS(ACT_OPTION,ZFINE_AUTODISP_ACT, ZFINE_AUTOPRIMACT)
	   	REF_PUPIL_NUMBER = POS_508
        


	   ZPROCLOG_DATA_ACS_FOCUS = ZPROCLOG_DATA_PRIMARY_ACT_FM_RMS/41.1

        END IF                                                         


                                                                               
      FUNCTION FS_SECONDACT()
C*******************************************************************************
C  REVISIONS:                                                                   
C                                                                               
C  Vers  1.0    8/24/88    (Scott Michaels)  - This is the original version     
C  Vers  2.0    4/20/95    (Scott Michaels)  - Motif Upgrade
C*******************************************************************************

	IMPLICIT NONE

      INCLUDE    'CENTROIDS.CMN'
      INCLUDE    'PCS_LOGICALS.CMN'
      INCLUDE    'CAMERA_INTERFACE.CMN'
      INCLUDE    'SETUP.CMN'
      INCLUDE    'FUNCTIONS.CMN'
      INCLUDE    'ZERNIKE.CMN'
      INCLUDE    'ACS_DCS_SHR.CMN'
      INCLUDE    'INCOMPLETE_MIRROR.CMN'
      INCLUDE    'PCS_FORT2GUI.CMN'
                                                                                
        INTEGER    ACT_OPTION                                       
	LOGICAL    FS_SECONDACT

C**************************************************************                 
C                                                                               
C                                                                               
C**************************************************************                 
                                       
	FS_SECONDACT = .FALSE.
                                         
	IF(ZFINE_CALC_CHOICE.EQ.0) GOTO 900
	IF(ZFINE_CALC_CHOICE.EQ.1) ACT_OPTION = 1  ! Piston
	IF(ZFINE_CALC_CHOICE.EQ.2) ACT_OPTION = 2  ! Tilt
	IF(ZFINE_CALC_CHOICE.EQ.3) ACT_OPTION = 3  ! Both
                                                                  
        TEXT = 'Calculating Secondary Actuators'

        CALL DISP_WRITE(TEXT, LEN(TEXT))

        OK = SECONDARY_ACTUATOR(ACT_OPTION, PROMPT_USER, 
     +    ZFINE_AUTOCENTERTEL)

        IF (.NOT.OK) GOTO 900
	
	FS_SECONDACT = .TRUE.

900	RETURN
	END


*/
			
			
			
			
			/*****************************************************/
			/*          centerTelescopeCalc subprocedure         */
			/*****************************************************/
			
			// This is not implemented as a standard subprocedure because of the data we need returned.
			
			CenterTelescopeCalcResult centerTelescopeCalcResult = centerTelescopeCalc.centerTelescope(procedure, currentSession);
			
			CentroidOffsetsResult centroidOffsetsResult= centerTelescopeCalcResult.getCentroidOffsetsResult();
			FloatPoint deltaAzEl = centerTelescopeCalcResult.getDeltaAzEl();
			

			procedureExecutionState.setPercentComplete(80);

			/*****************************************************/
			/*              calculateCentroidStats               */
			/*****************************************************/

			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());

			
			CentroidStatsResult centroidStatsResult = computationLibrary.calculateCentroidStats(centroidOffsetsResult.getCcdCentroidOffsets(), subimageDefList.getNspotTypes(), 
					subimageDefList.getMissingSpotFlags(), findCentroidsResult.getFindCentStatusList());

			/*****************************************************/
			/*              passiveTiltScaleError                */
			/*****************************************************/
			
			//need to get centerSpots 
			List<FloatPoint> centerSpots = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getCenterSpot());
			
			ScaleError scaleError = computationLibrary.passiveTiltScaleError(centroidOffsetsResult.getCcdCentroidOffsets(),
					centerSpots);

			// fill the iteration output
			PassiveTiltIterationOutput pio = new PassiveTiltIterationOutput();
			procedureOutput.addIteration(pio);

			pio.setIteration(0);
			pio.setDeltaAzEl(deltaAzEl);

			pio.setCcdCentroidOffsets(centroidOffsetsResult.getCcdCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setCartesianCentroidOffsets(centroidOffsetsResult.getCartesianCentroidOffsets().toArray(new FloatPoint[0]));
			pio.setScaleError(scaleError.getScaleError());

			pio.setMaxSpotNum(centroidStatsResult.getMaxSpotNum());
			pio.setMaxOffset(centroidStatsResult.getMaxOffset());
			pio.setRmsOffset(centroidStatsResult.getRmsOffset());

			pio.setEnclosedEnergy50(centroidStatsResult.getEnclosedEnergy50());
			pio.setEnclosedEnergy80(centroidStatsResult.getEnclosedEnergy80());

			pio.setScaleError(scaleError.getScaleError());
			pio.setSlopeError(scaleError.getSlopeError());

			pio.setTelescopeMoved(false);

			// fill the output - many of these are copied from the one iteration
			procedureOutput.setCcdCentroidOffsets(pio.getCcdCentroidOffsets());
			procedureOutput.setCartesianCentroidOffsets(pio.getCartesianCentroidOffsets());

			procedureOutput.setScaleError(pio.getScaleError());

			procedureOutput.setMaxSpotNum(pio.getMaxSpotNum());
			procedureOutput.setMaxOffset(pio.getMaxOffset());
			procedureOutput.setRmsOffset(pio.getRmsOffset());

			procedureOutput.setEnclosedEnergy50(pio.getEnclosedEnergy50());
			procedureOutput.setEnclosedEnergy80(pio.getEnclosedEnergy80());

			procedureOutput.setScaleError(pio.getScaleError());
			procedureOutput.setSlopeError(pio.getSlopeError());

			procedureOutput.setRotationFromRefBeam(centroidOffsetsResult.getImageRotation());
			procedureOutput.setScaleChangeFromRefBeam(centroidOffsetsResult.getImageScale());
			procedureOutput.setTranslationFromRefBeam(centroidOffsetsResult.getImageTranslation());

			// Display the average centroid offsets - this is probably not needed since we only do one trial
			graphicDisplayMgmt.displayCentroidOffsets(procedureOutput);

			// Go from segment tip/tilt offsets to actuator deltas with pistons set to zero
			/*****************************************************/
			/*                  ttOffsetsToActs                  */
			/*****************************************************/
			List<FloatPoint> actPosList = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getPrimaryActPos());
			// lpz = local piston zeroed on a segment
			float[][] lpzActDeltas = computationLibrary.ttOffsetsToActs(actPosList, procedureConfig.getPupilMask().getSecPerPixel(),
					centroidOffsetsResult.getCartesianCentroidOffsets());

			// Decompose the calculated actuators into pure tip/tilt and pure piston.
			// This code is to ensure that the pistons are indeed zero prior to proceding.
			/*****************************************************/
			/*                  decomposeActs                    */
			/*****************************************************/
			DecomposeActsResult decomposeActResult = computationLibrary.decomposeActs(lpzActDeltas);

			// Calculate the optimal pistons associated with the calculated
			// actuators (minimizes the changes to the edges). Note that this
			// routine just determines the optimal pistons; if you want to add
			// these on to the tip/tilt pistons, you need to do it yourself.

			/*****************************************************/
			/*                  optimalPistons                   */
			/*****************************************************/
			float[][] controlMatrix = constantsCache.getPrimaryMirrorConstants().getaMatrix();
			float[][] pistonActs = computationLibrary.optimalPistons(controlMatrix, decomposeActResult.getTipTiltActs());

			// calculate RMS of the actuator cmds
			float pistonActsRms = computationLibrary.calcRms(pistonActs);

			
			// combine tip/tilt and piston commands
			/*****************************************************/
			/*               calcDesiredActCommands              */
			/*****************************************************/
			float[][] desiredActDeltas = computationLibrary.addMatricies(decomposeActResult.getTipTiltActs(), pistonActs);

			// calculate RMS of the actuator cmds
			float desiredActDeltasRms = computationLibrary.calcRms(desiredActDeltas);

			// set iteration and procedure outputs
			pio.setTipTiltActuatorDeltas(decomposeActResult.getTipTiltActs());
			pio.setPistonActuatorDeltas(pistonActs);
			pio.setPistonActuatorDeltasRms(pistonActsRms);

			pio.setM1ActuatorCmds(desiredActDeltas);
			pio.setM1ActuatorCmdsRms(desiredActDeltasRms);

			procedureOutput.setM1ActuatorCmds(pio.getM1ActuatorCmds());
			procedureOutput.setM1ActuatorCmdsRms(pio.getM1ActuatorCmdsRms());
			procedureOutput.setTipTiltActuatorDeltas(pio.getTipTiltActuatorDeltas());
			procedureOutput.setPistonActuatorDeltas(pio.getPistonActuatorDeltas());
			procedureOutput.setPistonActuatorDeltasRms(pio.getPistonActuatorDeltasRms());

			// display the pistonDeltas
			graphicDisplayMgmt.displayActuatorDeltas(procedureOutput);

			// display RMS piston deltas to user in dialog
			String text = MessageGenerator.generateMessage("pt.m1_act_cmds_rms", desiredActDeltasRms);
			boolean commandAcs = userPromptMgmt.displayYesNoDialog(text + "\nCommand Primary Mirror?");

			// command ACS
			boolean commandsSent = false;
			if (commandAcs) {

				try {
					// send out the commands
					acsMgmt.commandActuatorDeltas(desiredActDeltas);

					statusLogger.log("pt.m1_act_cmd_success");
					logger.info("doSendActDeltaCommands: success");
					commandsSent = true;
					
					// take and store a snapshot
					int snapNum = acsMgmt.commandTakeSnap();
					procedureOutput.setM1SnapNumberAfter(snapNum);
					
				} catch (Exception e) {
					statusLogger.log("pt.m1_act_cmd_failed");
					logger.error(MessageGenerator.generateMessage("command.error"), e);
				}

			}
			procedureOutput.setM1CmdsSent(commandsSent);

			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
		        Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete");
			}

			statusLogger.log("procedure.success", procedure.getProcedureType().getProcedureTypeName());

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
