/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.executor;


import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.CreateRefBeamMapProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
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
	private ComputationLibraryImpl computationLibrary;
	@EJB
	private PupilRegistrator pupilRegistrator;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private CentroidMapMgmt refBeamMapMgmt;
	@EJB
	private ReadyCameraSubflow readyCameraSubflow;
	@EJB	
	private GetFrameCentroidsExecutor getFrameCentroidsExecutor;

	
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
		executeSynchronousProcedure(procedure, currentSession);
	}
		
	public void executeSynchronousProcedure(Procedure procedure, Session currentSession) {

		logger.info("CreateRefMapExecutor::executeProcedure::");

		try {

			ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
			
			//ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
			
			CreateRefBeamMapProcedureOutput procedureOutput = (CreateRefBeamMapProcedureOutput)procedure.getProcedureOutput();
						
			statusLogger.log("procedure.start", procedure.getProcedureType().getProcedureTypeName());
			
						
		    // TODO: Special logic for SUFS                                                    
			/*
			IF (ZREFMAP_REF_TYPE.EQ.MASK_MENU_SUFS) THEN
		           REF_SUFS_GROUP = CURRENT_SUFS_GROUP

			END IF
			*/

			/**********************************************/
			/*                 Ready Camera               */
			/**********************************************/			
			readyCameraSubflow.execute(procedure);

			procedureExecutionState.setPercentComplete(25);
			
			ProcedureCcdFrame procedureCcdFrame = getFrameCentroidsExecutor.executeProcedure(procedure, currentSession);
			
			procedureExecutionState.setPercentComplete(60);

			statusLogger.log("procedure.refmap.created");                

			// TODO: remove this: just to have something to save
			boolean saveMap = true;
			procedureOutput.setMapSaved(saveMap);
			
			// save the reference beam map
			RefBeamMap refBeamMap = buildRefMap(procedureCcdFrame.getCentroidMap(), procedure);
			procedure.setRefBeamMap(refBeamMap);

			procedureExecutionState.setPercentComplete(80);

			// TODO: if SUFS, then Home the coarse mirror 
			// CALL UFS_SEGMENT_SELECT(0)
			// CALL UFS_SEG_POS_WRITE
			
			if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED) {
				// turn off reference beams - need to wait for response				
				Future<Integer> refBeamFuture = cameraMgmt.commandReferenceBeamState(CameraCommand.OFF);
				procedureExecutionState.setPercentComplete(90);
				long waitPeriodMs = Utils.waitForComplete(refBeamFuture);
	        	statusLogger.log("camera.cmd.complete", waitPeriodMs/1000.0);
			}

			statusLogger.log("procedure.success",  procedure.getProcedureType().getProcedureTypeName());

			procedureExecutionState.setPercentComplete(100);

		} catch (Throwable e) {
			procedureExecutionMgmt.handleProcedureException(procedure, e);
		}
		
		/*
		 * getProcStats();
		 */
		
		procedureExecutionMgmt.performProcedureCompletion(procedure, currentSession);
	}
	
	
	public RefBeamMap buildRefMap(CentroidMap centroidMap, Procedure procedure) {
		RefBeamMap refBeamMap = new RefBeamMap();
		refBeamMap.setCreateDate(new Date());
		
		refBeamMap.setInstrumentId(procedure.getInstrument().getInstrumentId());
		refBeamMap.setFilterTypeId(procedure.getProcedureConfigSet().getProcedureConfig().getFilter().getFilterType().getFilterTypeId());
		
		//centroidMap.setCreateDate(new Date());
		
		refBeamMap.setCentroidMap(centroidMap);
		refBeamMap.setRefBeamDefMapFlg(false);

		return refBeamMap;
	}
}
