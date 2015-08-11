package org.tmt.aps.peas.procedure.executor;

import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Singleton
@Startup
public class ReadyCamera {

	static Logger logger = Logger.getLogger(ReadyCamera.class);
	

	@EJB
	private StatusLogger statusLogger;
	@EJB
	private CameraMgmt cameraMgmt;


	@Abortable
	public void execute(Procedure procedure) throws Throwable {
		
		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
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
			
			// always command the coarse and fine mirror to setup values at the start of all procedures
			Point coarseMirrorDefault = procedure.getProcedureConfigSet().getGlobalConfig().getCoarseMirrorDefault();
			Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(coarseMirrorDefault);
			statusLogger.log("camera.cmd.coarse_mirror", coarseMirrorDefault.x, coarseMirrorDefault.y);
			
			Point fineMirrorDefault = procedure.getProcedureConfigSet().getGlobalConfig().getFineMirrorDefault();
			Future<Point> fineMirrorCommandFuture = cameraMgmt.commandFineTiltMirror(fineMirrorDefault);
			statusLogger.log("camera.cmd.fine_mirror", fineMirrorDefault.x, fineMirrorDefault.y);
			
			
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
				ReferenceBeam refBeam = procedureConfig.getReferenceBeam();
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
					coarseMirrorCommandFuture, fineMirrorCommandFuture);
			statusLogger.log("camera.cmd.complete");

		}


	}
	
	


}
