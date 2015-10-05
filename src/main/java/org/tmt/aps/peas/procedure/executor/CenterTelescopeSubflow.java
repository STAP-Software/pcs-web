package org.tmt.aps.peas.procedure.executor;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Startup
public class CenterTelescopeSubflow {

	static Logger logger = Logger.getLogger(CenterTelescopeSubflow.class);
	
	@EJB
	private UserPromptMgmt userPromptMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private DcsMgmt dcsMgmt;
	@EJB
	private SubimageDefCache subimageDefCache;
	@EJB
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;


	@Abortable
	public void centerTelescope(Procedure procedure, Session currentSession) throws Throwable {
		
		//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
		ProcedureCcdFrame procedureCcdFrame = null;
		CentroidOffsetsResult centroidOffsetsResult = null;

		FloatPoint lastMove = null;
		
		
		while (true) {

			logger.debug("light source 3 = " + procedureConfig.getLightSource());

			procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);

			statusLogger.log("calc.centroid_resid");

			/*****************************************************/
			/*             calculateCentroidOffsets              */
			/*****************************************************/

			FindCentroidsResult findCentroidsResult = procedureCcdFrame.getCentroidMap().getFindCentroidsResult();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());
											
			centroidOffsetsResult = computationLibrary.calculateCentroidOffsets(procedureCcdFrame.getCentroidMap().getFindCentroidsResult().getCentroidList(),
					procedure.getRefBeamMap().getCentroidMap().getFindCentroidsResult().getCentroidList(), 
					procedure.getProcedureConfigSet().getCentroidOffsetsConfig(), procedureConfig.getPupilMaskType(), subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), 
					findCentroidsResult.getFindCentStatusList());

			// go from centroidOffsetsResult.imageTranslation to deltaAz,El
			CenterTelescopeCalcResult centerTelescopeCalcResult = computationLibrary.centerTelescopeCalc(centroidOffsetsResult.getImageTranslation(), 
					new FloatPoint(0,0), procedureConfig.getPupilMask().getSecPerPixel());

			// test deltaAzEl against thresholds for telescope move
			AutoCenterTelConfig autoCenterTelConfig = procedure.getProcedureConfigSet().getAutoCenterTelConfig();
			AutoCenterTelCheckResult aResult = computationLibrary.autoCenterTelescopeCheck(autoCenterTelConfig, centerTelescopeCalcResult.getDeltaAzEl(), lastMove);
			// log what result was found
			statusLogger.log(aResult.getReasonKey(), aResult.getReasonArgs());

			if (aResult.getRecenterTelescope().isNo() || procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_NO) {
				break; // leave the loop if nothing to do
			}

			if (aResult.getRecenterTelescope().isYes() && procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_YES) {

				// perform telescope move
				lastMove = centerTelescopeCalcResult.getDeltaAzEl();
				statusLogger.log("telescope.cmd.start");
				dcsMgmt.commandTelescopeDeltas(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
				statusLogger.log("telescope.cmd.end");
			}

			// prompt user if required by settings or required due to abnormal result
			boolean userReply = false;
			if (aResult.getRecenterTelescope().isPrompt()) {
				
				// ask user if they want to center the telescope
				userReply = userPromptMgmt.displayYesNoDialog(MessageGenerator.generateMessage(aResult.getReasonKey(),
						aResult.getReasonArgs()) + "\nMove Telescope?");
				
				if (userReply) {
					// perform telescope move
					lastMove = centerTelescopeCalcResult.getDeltaAzEl();
					statusLogger.log("telescope.cmd.start");
					dcsMgmt.commandTelescopeDeltas(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
					statusLogger.log("telescope.cmd.end");						
				} else {
					break;
				}
				
			} else if (procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_PROMPT) {
				// ask user if they want to center the telescope
				userReply = userPromptMgmt.displayYesNoDialog(MessageGenerator.generateMessage(aResult.getReasonKey(),
						aResult.getReasonArgs()) + "\nMove Telescope?");
				
				if (userReply) {
					// perform telescope move
					lastMove = centerTelescopeCalcResult.getDeltaAzEl();
					statusLogger.log("telescope.cmd.start");
					dcsMgmt.commandTelescopeDeltas(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
					statusLogger.log("telescope.cmd.end");						
				} else {
					break; // if user doesn't want to move telescope, no point in re-taking frame
				}
				
			}


			if (aResult.getRetakeFrame().isNo()) {
				break;
			}

			if (aResult.getRetakeFrame().isPrompt()) {

				// ask user if they want to re-take the frame
				int reply = userPromptMgmt.displayFlowControlTriFlowDialog("Frame needs to be retaken.  Press: 'Retry' to re-take frame, 'Continue' to continue procedure with this frame, 'Abort' to abort test now.");
			
				if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					
					// TODO: put in logic here (throw user abort exception?
					
				} else if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE) {
					break; // continue on
				}
			}

			// go back and re-take frame

		}
		
	}

}
