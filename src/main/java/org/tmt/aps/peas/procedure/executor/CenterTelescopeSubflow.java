package org.tmt.aps.peas.procedure.executor;

import java.util.concurrent.Future;

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
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

/**
 * Executor for the Center Telescope sub-procedure
 * @author smichaels
 *
 */
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
	@EJB
	private PhysicalModel physicalModel;


	/**
	 * Executor method: this method is the Center Telescope sub-flow
	 */
	@Abortable
	public Future<Exception> centerTelescope(Procedure procedure, Session currentSession) throws Throwable {
		
		//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
		ProcedureCcdFrame procedureCcdFrame = null;
		CentroidOffsetsResult centroidOffsetsResult = null;

		FloatPoint lastMove = null;
		
		Future<Exception> future = null;
		
		while (true) {

			logger.debug("light source 3 = " + procedureConfig.getLightSource());

			procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);

			statusLogger.log("calc.centroid_resid");

			/*****************************************************/
			/*             calculateCentroidOffsets              */
			/*****************************************************/

			FindCentroidsResult findCentroidsResult = procedureCcdFrame.getCentroidMap().getFindCentroidsResult();
			Integer sufsGroup = procedure.getProcedureType().isSufs() ? procedureConfig.getSufsGroup() : null;
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList( procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), sufsGroup);
											
			centroidOffsetsResult = computationLibrary.calculateCentroidOffsets(procedureCcdFrame.getCentroidMap().getFindCentroidsResult().getCentroidList(),
					procedure.getCurrentRefBeamMap().getCentroidMap().getFindCentroidsResult().getCentroidList(), 
					procedure.getProcedureConfigSet().getCentroidOffsetsConfig(), procedureConfig.getPupilMaskType(), subimageDefList.getNspotTypes(), subimageDefList.getMissingSpotFlags(), 
					findCentroidsResult.getFindCentStatusList());

			// go from centroidOffsetsResult.imageTranslation to deltaAz,El
			// secPerPixel value can be extracted from the procedureOutput object
			CenterTelescopeCalcResult centerTelescopeCalcResult = computationLibrary.centerTelescopeCalc(centroidOffsetsResult.getImageTranslation(), 
					new FloatPoint(0,0), procedure.getProcedureOutput().getStartupComputationsResult().getArcsecPerPixel());

			// test deltaAzEl against thresholds for telescope move
			AutoCenterTelConfig autoCenterTelConfig = procedure.getProcedureConfigSet().getAutoCenterTelConfig();
			AutoCenterTelCheckResult aResult = computationLibrary.autoCenterTelescopeCheck(autoCenterTelConfig, centerTelescopeCalcResult.getDeltaAzEl(), lastMove);
			// log what result was found
			statusLogger.log(aResult.getReasonKey(), aResult.getReasonArgs());
			
			
			if (procedureConfig.isFrameFromFile()) {
				break; // we will not center telescope if frame from file
			}

			if (aResult.getRecenterTelescope().isNo() || procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_NO) {
				break; // leave the loop if nothing to do
			}

			boolean moveTelescope = false;
			
			if (aResult.getRecenterTelescope().isYes() && procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_YES) {
				moveTelescope = true;
			}

			// prompt user if required by settings or required due to abnormal result
			if (aResult.getRecenterTelescope().isPrompt() || procedureConfig.getAutoCenterTelescope() == Constants.AUTO_CENTER_TELESCOPE_PROMPT) {
				
				// ask user if they want to center the telescope
				moveTelescope = userPromptMgmt.displayYesNoDialog("Move Telescope", MessageGenerator.generateMessage(aResult.getReasonKey(),
						aResult.getReasonArgs()) + "\nMove Telescope?");
				
			}
	
			boolean retakeFrame = false;

			if (aResult.getRetakeFrame().isPrompt()) {

				// ask user if they want to re-take the frame
				int reply = userPromptMgmt.displayFlowControlTriFlowDialog("Retake Frame", "Frame needs to be retaken.  Press: 'Retry' to re-take frame, 'Continue' to continue procedure with this frame, 'Abort' to abort test now.");
			
				if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
					
					throw new AbortProcedureException("User Aborted Test");
					
				} else if (reply == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_RETRY) {
					retakeFrame = true;
				}
			} else if (aResult.getRetakeFrame().isYes()) {
				retakeFrame = true;
			}

			
			if (moveTelescope) {
				
				if (retakeFrame) {
					// perform telescope move SYNCHRONOUS
					lastMove = centerTelescopeCalcResult.getDeltaAzEl();
					statusLogger.log("telescope.cmd.start");
					dcsMgmt.commandTelescopeDeltas(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
					statusLogger.log("telescope.cmd.end");
				} else {
					// perform telescope move ASYNCHRONOUS, and wait elsewhere (new case for waiting on a procedure step from a subprocedure to complete)
					lastMove = centerTelescopeCalcResult.getDeltaAzEl();
					statusLogger.log("telescope.cmd.start");
					future = dcsMgmt.commandTelescopeDeltasAsync(centerTelescopeCalcResult.getDeltaAzEl().asDoubleArray());
					break;
				}
			} else {
				
				if (!retakeFrame) {
					// the odd case where we don't move the telescope but retake the frame
					break;
				}
			}
			
			
			// go back and re-take frame

		}
		return future;
	}

}
