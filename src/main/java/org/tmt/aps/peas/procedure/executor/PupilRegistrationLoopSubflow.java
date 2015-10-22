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
public class PupilRegistrationLoopSubflow {

	static Logger logger = Logger.getLogger(PupilRegistrationLoopSubflow.class);
	
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
	private PupilRegistrationSubflow pupilRegistrationSubflow;
	@EJB
	private CenterTelescopeSubflow centerTelescopeSubflow;


	@Abortable
	public void pupilRegistrationLoop(Procedure procedure, Session currentSession) throws Throwable {
		

		while (true) {

			centerTelescopeSubflow.centerTelescope(procedure, currentSession);
			
			/**********************************************/
			/*        PupilRegistration Subflow           */
			/**********************************************/			
			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();

			boolean frameOk = pupilRegistrationSubflow.execute(procedure, findCentroidsResult);

			if (frameOk) break;
			
		}
		
	}

}
