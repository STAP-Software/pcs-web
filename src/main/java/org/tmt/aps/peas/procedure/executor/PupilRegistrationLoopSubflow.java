package org.tmt.aps.peas.procedure.executor;

import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Abortable;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

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

			Future<Integer> dcsFuture = centerTelescopeSubflow.centerTelescope(procedure, currentSession);
			
			/**********************************************/
			/*        PupilRegistration Subflow           */
			/**********************************************/			
			FindCentroidsResult findCentroidsResult = procedure.getLatestProcedureCcdFrame().getCentroidMap().getFindCentroidsResult();

			boolean frameOk = pupilRegistrationSubflow.execute(procedure, findCentroidsResult);

			
			// if we are waiting on DCS, here is where we must be completed.  This allows parallelism between possible camera commands to fix pupil reg
			// and DCS moves to center the camera
			long waitPeriodMs = Utils.waitForComplete(dcsFuture);
			statusLogger.log("dcs.cmd_completed", waitPeriodMs/1000.0);
			
			
			if (frameOk) break;
			
		}
		
	}

}
