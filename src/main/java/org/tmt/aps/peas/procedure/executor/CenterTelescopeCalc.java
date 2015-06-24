package org.tmt.aps.peas.procedure.executor;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.model.ProcedureConfig;

public class CenterTelescopeCalc {

	static Logger logger = Logger.getLogger(CenterTelescopeCalc.class);
	
	// This should return a return code and other values
	// lastTelescopeMoveOk
	// lastDeltaAz, El
	// frameOk
	// move/don't move/two consecutive bad moves/move too large/automode ask user/image off too much/move too small
	// then caller presents options to user
	
	public enum CenterTelescopeOptions {
		CENTER_TELESCOPE_OPTION_MOVE_TELESCOPE, 
		CENTER_TELESCOPE_OPTION_ABORT, 
		CENTER_TELESCOPE_OPTION_TWO_CONSECUTIVE_BAD_MOVES, 
		CENTER_TELESCOPE_OPTION_MOVE_TOO_LARGE, 
		CENTER_TELESCOPE_OPTION_MOVE_TOO_SMALL, 
		CENTER_TELESCOPE_OPTION_IMAGE_OFF_TOO_MUCH, 
		CENTER_TELESCOPE_OPTION_AUTOMODE_ASK_USER
		};
	
	

	
	public void centerTelescopeToleranceTests(FloatPoint deltaAzEl, ProcedureConfig procedureConfig) {
		
		double deltaAz = deltaAzEl.x;
		double deltaEl = deltaAzEl.y;
		
		// TODO: az, el should be formatted as F6.2
		String text = "The telescope needs to be moved \n" + deltaAz + " arc sec. in AZ \n" + deltaEl + " arc sec. in EL \n";

		boolean automodeAbort = false;

		// temp stuff for now
		double moveDis = 0.0;
		boolean OK = false;
		double TOL = 0.0;
		double MAX_TOL = 0.0; // TODO calc commented out
		boolean LAST_TEL_MOVE_OK = false;
		double FRAME_OK_NO_MOVE_TEL = 0.0;
		double FRAME_NOT_OK_MOVE_TEL = 0.0;
		double FRAME_OK_MOVE_TEL = 0.0;
		double lastDeltaAz = 0.0;
		double lastDeltaEl = 0.0;

		boolean lastTelescopeMoveOk = false; // TODO this will be global in some way

		if ((procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_YES) && !procedureConfig.isFrameFromFile()) {

			moveDis = Math.sqrt(deltaAz * deltaAz + deltaEl * deltaEl);
			if (moveDis < FRAME_OK_NO_MOVE_TEL) {
				lastTelescopeMoveOk = true;
				return;
			} else if (moveDis < FRAME_OK_MOVE_TEL) {
				lastTelescopeMoveOk = true;
			} else if (moveDis < FRAME_NOT_OK_MOVE_TEL) {
				if (LAST_TEL_MOVE_OK) {

					// FRAME_OK = .FALSE.
					// LAST_TEL_MOVE_OK = .FALSE.
					// LAST_DELTA_AZ = DELTA_AZ
					// LAST_DELTA_EL = DELTA_EL
				} else {
					text = "WARNING!!!\n" + "Automode has detected two consecutive large telescope moves!\n" + "The last move was:\n"
							+ lastDeltaAz + " Arc Sec in AZ " + lastDeltaEl + "Arc Sec in EL\n"
							+ "Automode seeks permission to move the telescope: " + deltaAz + " Arc Sec in AZ, " + deltaEl
							+ "Arc Sec in EL";

					// OK = FYNWARN_DIALOG(TEXT, LEN(TEXT),
					// 'Move Telescope', LEN('Move Telescope'),
					// 'Abort Test', LEN('Abort Test'), NO,
					// ZGLOBAL_CURRENT_PARENT)

					if (OK) {
						// FRAME_OK = .FALSE.
						// LAST_TEL_MOVE_OK = .FALSE.
						// LAST_DELTA_AZ = DELTA_AZ
						// LAST_DELTA_EL = DELTA_EL
					} else {

						// AUTOMODE_ABORT = .TRUE.
					}
				}
			} else {
				text = "Telescope move too large for Automode!";

				// OK = FYNWARN_DIALOG(TEXT, LEN(TEXT),
				// 'Command Telescope Anyway', LEN('Command Telescope Anyway'),
				// 'Cancel', LEN('Cancel'), NO,
				// ZGLOBAL_CURRENT_PARENT)

				// FRAME_OK = .FALSE.
			}
		} else if ((procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_YES) && procedureConfig.isFrameFromFile()) {

			return;

		} else if (procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_NO) {
			return;

		} else {

			// TODO: put up dialog: if user says don't move, then return;
			// OK = FYN_DIALOG(TEXT, LEN(TEXT), 'Move Telescope',
			// LEN('Move Telescope'), 'Don''t Move Telescope',
			// LEN('Don''t Move Telescope'), YES,
			// ZGLOBAL_CURRENT_PARENT)

		}

		testMoveSize(deltaEl, deltaAz, TOL, MAX_TOL);
		
		//centerTelescopeCommand(deltaEl, deltaAz, TOL);
	}
	
	public void testMoveSize(double deltaEl, double deltaAz, double TOL, double MAX_TOL) {
		
		// If both are smaller then TOL then quit.
		// Uncommented code 12/5/94 we know don't send commands less then tol.

		if ((Math.abs(deltaEl) < TOL) && (Math.abs(deltaAz) < TOL)) {

			String text = "Telescope move to small. Command not sent";
			// CALL FERROR_DIALOG(TEXT, LEN(TEXT), ZGLOBAL_CURRENT_PARENT)

			// RETURN
		}

		// If either one is bigger then 1/2 the field of view then quit.

		// MAX_TOL = NUM_FRAME_COLS / 2.0 * SECPERPIX_36

		if ((Math.abs(deltaAz) > MAX_TOL) || (Math.abs(deltaEl) > MAX_TOL)) {

			String text = "Image is off by more than " + (int) (MAX_TOL) + " arc sec.  Telescope command not sent";
			// CALL FERROR_DIALOG(TEXT, LEN(TEXT), ZGLOBAL_CURRENT_PARENT)

			// RETURN
		}

	}
	

	public void centerTelescopeCommand(double deltaEl, double deltaAz, double TOL) {


		// Convert Arc Seconds to radians

		// DELTA_AZIMUTH = DELTA_AZ * PI / ( 60.0 * 60.0 * 180.0)
		// DELTA_ELEVATION = DELTA_EL * PI / ( 60.0 * 60.0 * 180.0)

		// Send DCS command

		try {
			
			//dcsMgmt.commandTelescopeDeltas(); // was OK = DCS_COMMAND(DCS_PRESET_COLLIM)
			String text = "Telescope move successful";
			// CALL DISP_WRITE(TEXT, LEN(TEXT))

			// ZPROCLOG_FRAMELOG_TEL_MOVED(ZPROCLOG_DATA_FRAME_LOG_COUNT) = 1
		
		} catch (Exception e) {
			
			logger.error(MessageGenerator.generateMessage("command.error"), e);
			
			String text = "Move Telescope command failure";
			// CALL DISP_WRITE(TEXT, LEN(TEXT))
			// CALL FWARN_DIALOG(TEXT, LEN(TEXT), ZGLOBAL_CURRENT_PARENT)
			
		}

	}

}
