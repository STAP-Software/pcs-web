/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoCenterTelConfigDefaults;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfigDefaults;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfigDefaults;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FIConfigDefaults;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.FindCentConfigDefaults;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.ProcedureConfigDefaults;
import org.tmt.aps.peas.config.model.RefMapConfigDefaults;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.frame.business.FrameDisplayMgmt;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.FrameSimulator;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Stateless
public class ProcedureExecutionMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private SessionMgmt sessionMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private FrameDisplayMgmt frameDisplayMgmt;
	@EJB
	private StatusLogger statusLogger;
	@EJB
	private ProcedureExecutionState procedureExecutionState;
	@EJB
	private ProcedureOutputMgmt procedureOutputMgmt;
	@EJB
	private PhysicalModel physicalModel;
	@EJB
	private GlobalConfigMgmt globalConfigMgmt;
	@EJB
	private CentroidMapMgmt centroidMapMgmt;
	@EJB 
	private ProcedureMgmt procedureMgmt;
	@EJB 
	private CameraDefMgmt cameraDefMgmt;
	@EJB 
	private DcsMgmt dcsMgmt;
	@EJB 
	private FrameSimulator frameSimulator;

	public void performProcedureStartup(Procedure procedure, List<FitsFilename> selectedFitsFiles) {

		// global config needs loaded in case it has changed from nominal
		GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt.findDefaultConfig(physicalModel.getTelescope().getTelescopeId(),
				physicalModel.getInstrument().getInstrumentId());
		// store with procedure config set
		procedure.getProcedureConfigSet().setGlobalConfig(new GlobalConfig(globalConfigDefaults));

		procedure.setInstrument(physicalModel.getInstrument());
		procedure.setTelescope(physicalModel.getTelescope());

		// add the associated ref def map to the fi config for this procedure
		if (!procedure.getProcedureType().isCenterTelescope()) {
			RefBeamMap refDefMap = centroidMapMgmt.getRefBeamDefMap(procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask()
					.getPupilMaskType().getPupilMaskTypeId());
			procedure.setRefDefMap(refDefMap);
		}

		// get FindCentDefaults and create a procedure related copy
		FindCentConfigDefaults findCentConfigDefaults = globalConfigMgmt.findFindCentConfig(procedure.getProcedureConfigSet()
				.getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId());
		procedure.getProcedureConfigSet().setFindCentConfig(new FindCentConfig(findCentConfigDefaults));

		
		
		// if this is frame from file, associate the frame now
		if (procedure.getProcedureConfigSet().getProcedureConfig().isFrameFromFile()) {

			try {
				frameSimulator.init(selectedFitsFiles);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// get the star info from the DCS interface
			try {
				StarInfo starInfo = dcsMgmt.queryStar();
				procedure.setStarName(starInfo.getStarName());
				procedure.setStarSpType(starInfo.getStarColor());
				procedure.setStarVmag(String.format("%.2f", starInfo.getStarMag()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		
		procedure.setExecutionStartTime(new Date());
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_EXECUTING);

		// tell the world so the UI can disable things the user cannot touch
		procedureExecutionState.setExecutionStatus(true);
		logger.info("Setting execution status to true");
		procedureExecutionState.setPercentComplete(0);

		statusLogger.initLog();

		frameDisplayMgmt.init();

	}

	public void handleProcedureException(Procedure procedure, Throwable exception) {

		Throwable procedureException = exception;

		try {
			throw exception;

		} catch (UnsatisfiedLinkError e) {
			procedureException = new Exception("Fortran libraries not accessible due to hot deployment.  To fix, restart JBoss.");

		} catch (EJBTransactionRolledbackException e) {

			if (e.getCause() instanceof UndeclaredThrowableException) {
				UndeclaredThrowableException e1 = (UndeclaredThrowableException) e.getCausedByException();
				Throwable e2 = e1.getCause();
				procedureException = e2;
			}

		} catch (Throwable e) {
			procedureException = e;
		}

		exception.printStackTrace();
		statusLogger.log("procedure.exception", procedureException.getMessage());

		procedure.setProcedureState(Procedure.PROCEDURE_STATE_ABORTED);
		procedureExecutionState.setExecutionStatus(false);
		procedureExecutionState.setProcedureException(procedureException);

	}

	public void performProcedureCompletion(Procedure procedure, Session currentSession) {

		try {
			procedure.setExecutionEndTime(new Date());

			// this persists the procedure
			sessionMgmt.updateCurrentSession(currentSession);

			// save the current coarse mirror state in global config
			Point coarsePosition = physicalModel.getInstrument().getCamera().getCoarseTiltMirror().getCurrentPosition();
			
			// create a config defaults object to save back
			GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt.findDefaultConfig(physicalModel.getTelescope().getTelescopeId(), physicalModel.getInstrument().getInstrumentId());
			globalConfigDefaults.setCoarseMirrorX(coarsePosition.x);
			globalConfigDefaults.setCoarseMirrorY(coarsePosition.y);
			globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);

			// persist all the frames
			if (procedure.getProcedureCcdFrameList() != null) {
				for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
					procedureCcdFrame.setProcedure(procedure); // need the assigned procedure id
					frameMgmt.associateCcdFrame(procedureCcdFrame);

					// load up png file again because associateCcdFrame reloads ccd frame fresh
					// FIXME: we should not have to do this.
					String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

					CcdFrame loadedFitsFile = null;

					logger.debug("filename = " + filename);
					try {

						loadedFitsFile = frameMgmt.loadFitsFrame(filename);

					} catch (Exception e) {
						e.printStackTrace();
					}

					// if a png file for display exists, read it in. Otherwise create it.
					byte[] falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
					procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

					// save the associated centroid map
					if (procedureCcdFrame.getCentroidMap() != null) {
						centroidMapMgmt.saveCentroidMap(procedureCcdFrame.getCentroidMap());
					}
				}
			}

			statusLogger.saveLog(procedure.getProcedureId());

			// associate ref beam map
			if (procedure.getRefBeamMap() != null) {
				centroidMapMgmt.associateRefBeamMap(procedure.getRefBeamMap(), procedure);
			}
			
			// persist the procedure output
			if (procedure.getProcedureOutput() != null) {
				procedureOutputMgmt.createProcedureOutput(procedure.getProcedureOutput(), procedure.getProcedureId());
				for (ProcedureIterationOutput pio : procedure.getProcedureOutput().getProcedureIterationOutputList()) {
					procedureOutputMgmt.createProcedureOutput(pio, procedure.getProcedureId());
				}
			}
			// set up for immediate viewing
			procedure.setProcedureOutput(procedureOutputMgmt.findProcedureOutput(procedure.getProcedureId()));

			// procedure frame data for immediate viewing
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {

				procedureMgmt.setupFrameLog(procedureCcdFrame);
			}
			
			procedureExecutionState.requestCompleteProcedure(); // if this is a subprocedure, transfer control to superprocedure
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_COMPLETED);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Procedure performProcedureSetup(Long procedureTypeId, Long sessionId, ProcedureOutput procedureOutput) throws Exception {
		Procedure procedure = new Procedure();

		// get the procedure type object
		ProcedureType procedureType = procedureMgmt.findProcedureType(procedureTypeId);
		procedure.setProcedureType(procedureType);

		procedure.setProcedureOutput(procedureOutput);

		// if the executionStatus is 'running', then we must be starting a sub-procedure
		boolean isSubProcedure = procedureExecutionState.getExecutionStatus();
		String superProcedureNum = isSubProcedure ? procedureExecutionState.getCurrentProcedure().getProcedureNumber() : null;

		String procNum = sessionMgmt.getNextProcedureNumber(sessionId, superProcedureNum);
		procedure.setProcedureNumber(procNum);


		ProcedureConfigDefaults procedureConfigDefaults = procedureMgmt.findDefaultProcedureConfig(physicalModel.getTelescope()
				.getTelescopeId(), physicalModel.getInstrument().getInstrumentId(), procedureTypeId);

		// copy the default config into the current procedure config for potential modification
		ProcedureConfig procedureConfig = new ProcedureConfig(procedureConfigDefaults);

		// and associate it with the procedure
		procedure.getProcedureConfigSet().setProcedureConfig(procedureConfig);

		// get the default mask, if it is installed on the wheel
		PupilMask defaultMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(procedureConfig.getPupilMaskType().getPupilMaskTypeId(),
				physicalModel.getInstrument().getCamera().getPupilWheel().getPupilWheelId());

		procedureConfig.setPupilMask(defaultMask);

		// get the filter to default to if it exists
		Filter defaultFilter = cameraDefMgmt.getFilterByFilterTypeAndWheel(procedureConfig.getFilterType().getFilterTypeId(),
				physicalModel.getInstrument().getCamera().getFilterWheel().getFilterWheelId());

		procedureConfig.setFilter(defaultFilter);

		// if procedure type is create ref map, then populate ref beam and integration times from the table
		if (procedureTypeId.equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {

			// these get set into procedure config
			setupCreateRefMapDefaults(procedure, physicalModel.getInstrument().getInstrumentId(), defaultMask.getPupilMaskType()
					.getPupilMaskTypeId(), defaultFilter.getFilterType().getFilterTypeId());
		}

		// select defaults based on mask and light source
		if (!procedure.getProcedureType().isCenterTelescope()) {

			FIConfigDefaults fiConfigDefaults = globalConfigMgmt.findFIConfigDefaults(physicalModel.getInstrument().getInstrumentId(),
					procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask().getPupilMaskType().getPupilMaskTypeId(),
					procedure.getProcedureConfigSet().getProcedureConfig().getLightSource());

			// use defaults as actuals if user doesn't subsequently change them
			FIConfig fiConfig = new FIConfig(fiConfigDefaults);

			procedure.getProcedureConfigSet().setFiConfig(fiConfig);

		}
		
		// set centroid offsets calculation defaults based on procedure type
		CentroidOffsetsConfigDefaults centroidOffsetsConfigDefaults = globalConfigMgmt.findCentroidOffsetsConfig(procedureType
				.getProcedureTypeId());
		procedure.getProcedureConfigSet().setCentroidOffsetsConfig(new CentroidOffsetsConfig(centroidOffsetsConfigDefaults));

		// get AutoRefMapDefaults based on procedure type
		AutoRefMapConfigDefaults autoRefMapConfigDefaults = globalConfigMgmt.findAutoRefMapConfig(procedure.getProcedureType().getProcedureTypeId());
		procedure.getProcedureConfigSet().setAutoRefMapConfig(new AutoRefMapConfig(autoRefMapConfigDefaults));

		// get AutoCenterTelDefaults
		AutoCenterTelConfigDefaults autoCenterTelConfigDefaults = globalConfigMgmt.findAutoCenterTelConfig(procedure.getProcedureType().getProcedureTypeId());
		procedure.getProcedureConfigSet().setAutoCenterTelConfig(new AutoCenterTelConfig(autoCenterTelConfigDefaults));

		// clear any marking
		frameDisplayMgmt.clearMarking();
		
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_NEW);

		return procedure;
	}
	
	public void setupCreateRefMapDefaults(Procedure procedure, Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		RefMapConfigDefaults refMapConfigDefaults = globalConfigMgmt.findRefMapConfigDefaults(instrumentId, pupilMaskTypeId, filterTypeId);

		// Set up default ref beam and int time

		procedure.getProcedureConfigSet().getProcedureConfig().setLightSource(ProcedureConfig.LIGHT_SOURCE_LED);
		procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(refMapConfigDefaults.getReferenceBeam());
		procedure.getProcedureConfigSet().getProcedureConfig().setIntegrationTime(refMapConfigDefaults.getIntegrationTime());

		// make the list of possible int times equal to the 'one' we have
		 List<Float> integrationTimeList = new ArrayList<Float>();
		integrationTimeList.add(refMapConfigDefaults.getIntegrationTime());
		procedure.getProcedureConfigSet().getProcedureConfig().setIntegrationTimeList(integrationTimeList);
	}

	
}
