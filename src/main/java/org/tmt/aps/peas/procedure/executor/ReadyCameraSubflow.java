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
public class ReadyCameraSubflow {

	static Logger logger = Logger.getLogger(ReadyCameraSubflow.class);
	

	@EJB
	private StatusLogger statusLogger;
	@EJB
	private CameraMgmt cameraMgmt;


	@Abortable
	public void execute(Procedure procedure) throws Throwable {
		
		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
		if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {
			
			// always command the coarse and fine mirror to setup values at the start of all procedures
			Point coarseMirrorDefault = procedure.getProcedureConfigSet().getGlobalConfig().getCoarseMirrorDefault();
			Point desiredCoarseMirrorPosition = coarseMirrorDefault;
			if (procedureConfig.getPupilMaskType().isPupilMaskTypeSufs()) {
				// in the case of SUFS, we add the steering to the sufs group
				Point sufsCoarseOffsets = procedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig().getCoarseMirrorOffsetCurrent();
				desiredCoarseMirrorPosition = Point.add(coarseMirrorDefault, sufsCoarseOffsets);
			} 
			
			Future<Point> coarseMirrorCommandFuture = cameraMgmt.commandCoarseTiltMirror(desiredCoarseMirrorPosition);
			statusLogger.log("camera.cmd.coarse_mirror", desiredCoarseMirrorPosition.x, desiredCoarseMirrorPosition.y);
			
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
			long waitPeriodMs = Utils.waitForComplete(pupilMaskCommandFuture, filterCommandFuture, twoPosCommandFuture, refBeamFuture,
					coarseMirrorCommandFuture, fineMirrorCommandFuture);
			statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);

		}


	}
	
	


}
