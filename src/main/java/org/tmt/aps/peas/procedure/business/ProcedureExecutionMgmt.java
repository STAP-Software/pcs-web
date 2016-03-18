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
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoCenterTelConfigDefaults;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfigDefaults;
import org.tmt.aps.peas.config.model.CalcM2M1Config;
import org.tmt.aps.peas.config.model.CalcM2M1ConfigDefaults;
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
import org.tmt.aps.peas.config.model.PupilRegErrorConfig;
import org.tmt.aps.peas.config.model.PupilRegErrorConfigDefaults;
import org.tmt.aps.peas.config.model.RefMapConfigDefaults;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfig;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfigDefaults;
import org.tmt.aps.peas.config.model.SufsOffsetsToZernikesConfig;
import org.tmt.aps.peas.config.model.SufsOffsetsToZernikesConfigDefaults;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
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
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Stateless
public class ProcedureExecutionMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	@EJB
	private SessionMgmt sessionMgmt;
	@EJB
	private FrameMgmt frameMgmt;
	@EJB
	private CameraMgmt cameraMgmt;
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
	@EJB
	ExtInfConfigState extInfConfigState;

	public void performProcedureStartup(Procedure procedure, List<FitsFilename> selectedFitsFiles) throws Exception {

		logger.info("performProcedureStartup 1");

		// global config needs loaded in case it has changed from nominal
		GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt.findDefaultConfig(physicalModel.getTelescope().getTelescopeId(),
				physicalModel.getInstrument().getInstrumentId());
		// store with procedure config set
		procedure.getProcedureConfigSet().setGlobalConfig(new GlobalConfig(globalConfigDefaults));

		procedure.setInstrument(physicalModel.getInstrument());
		procedure.setTelescope(physicalModel.getTelescope());

		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();
		
		// need to propagate pupilMask and filter types into to procedureConfig object
		// FIXME: this needs to be handled more automatically within the model classes
		procedureConfig.setPupilMaskType(procedureConfig.getPupilMask().getPupilMaskType());
		procedureConfig.setFilterType(procedureConfig.getFilter().getFilterType());
		
		PupilMaskType pupilMaskType = procedureConfig.getPupilMaskType();
		FilterType filterType = procedureConfig.getFilterType();

		logger.info("performProcedureStartup 2");

		// get FindCentDefaults and create a procedure related copy
		FindCentConfigDefaults findCentConfigDefaultsInterior = globalConfigMgmt.findFindCentConfig(pupilMaskType.getPupilMaskTypeId(), filterType.getFilterTypeId(), Constants.SPOT_TYPE_INTERIOR);
		FindCentConfigDefaults findCentConfigDefaultsPeripheral = globalConfigMgmt.findFindCentConfig(pupilMaskType.getPupilMaskTypeId(), filterType.getFilterTypeId(), Constants.SPOT_TYPE_PERIPHERAL);
		procedure.getProcedureConfigSet().setFindCentConfigInterior(new FindCentConfig(findCentConfigDefaultsInterior));
		procedure.getProcedureConfigSet().setFindCentConfigPeripheral(new FindCentConfig(findCentConfigDefaultsPeripheral));
		
		if (procedure.getProcedureType().isSufs()) {
			// store the current sufs coarse mirror offsets for this group
			
			globalConfigMgmt.updateSufsCoarseOffsetsCurrent(procedure.getInstrument().getInstrumentId(), procedureConfig.getSufsGroup(), 
					procedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig().getCoarseMirrorOffsetCurrentX(),
					procedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig().getCoarseMirrorOffsetCurrentY());
		}

		logger.info("performProcedureStartup 3");

		// if this is frame from file, associate the frame now
		if (procedure.getProcedureConfigSet().getProcedureConfig().isFrameFromFile()) {

			//try {
				frameSimulator.init(selectedFitsFiles);
			//} catch (Exception e) {
			//	logger.error(MessageGenerator.generateMessage("generic.error"), e);
			//}

		} else {
			// get the star info from the DCS interface
			try {
				StarInfo starInfo = dcsMgmt.queryStar();
				procedure.setStarName(starInfo.getStarName());
				procedure.setStarSpType(starInfo.getStarColor());
				procedure.setStarVmag(String.format("%.2f", starInfo.getStarMag()));
			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
			}

		}

		procedure.setExecutionStartTime(new Date());
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_EXECUTING);

		// tell the world so the UI can disable things the user cannot touch
		procedureExecutionState.setExecutionStatus(true);
		logger.info("Setting execution status to true");
		
		procedureExecutionState.setPercentComplete(0);
		
		// if the session is new, then create it
		if (procedure.getSession().isNewRecord()) {
			Session session = procedureMgmt.createSession(procedure.getSession());
			procedure.setSession(session);
		}
		procedureMgmt.createProcedure(procedure);

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
			} else if (e.getCause() instanceof UnsatisfiedLinkError) {
				procedureException = new Exception("Fortran libraries not accessible due to hot deployment.  To fix, restart JBoss.");
			}

		} catch (Throwable e) {
			procedureException = e;
		}

		logger.error(MessageGenerator.generateMessage("generic.error"), procedureException);

		statusLogger.log("procedure.exception", procedureException.getMessage());
		
		
		procedure.setProcedureState(Procedure.PROCEDURE_STATE_ABORTED);
		procedureExecutionState.setExecutionStatus(false);
		procedureExecutionState.setProcedureException(procedureException);

	}

	public void performProcedureCompletion(Procedure procedure, Session currentSession) {

		try {
			
			procedure.setExecutionEndTime(new Date());

			logger.debug("performProcedureCompletion 1");
			// this persists the procedure
			procedureMgmt.updateProcedure(procedure);

			sessionMgmt.updateCurrentSession(currentSession);

			// save the current coarse mirror state in global config
			// get and wait for the current state and store it
			Future<Boolean> refreshFuture = cameraMgmt.refreshStatus();
			Utils.waitForComplete(refreshFuture);
			
			Point coarsePosition = physicalModel.getInstrument().getCamera().getCoarseTiltMirror().getCurrentPosition();
			Point finePosition = physicalModel.getInstrument().getCamera().getFineTiltMirror().getCurrentPosition();
			logger.debug("performProcedureCompletion::persist procedure");

			// if not running with simulated camera I/F, save the current coarse mirror positions in global config defaults
			if (extInfConfigState.getExtInfConnectConfig().isCameraEnabled() && 
					!procedure.getProcedureConfigSet().getProcedureConfig().getPupilMaskType().isPupilMaskTypeSufs()) {
				// create a config defaults object to save back
				GlobalConfigDefaults globalConfigDefaults = globalConfigMgmt
						.findDefaultConfig(physicalModel.getTelescope().getTelescopeId(), physicalModel.getInstrument().getInstrumentId());
				globalConfigDefaults.setCoarseMirrorX(coarsePosition.x);
				globalConfigDefaults.setCoarseMirrorY(coarsePosition.y);
				globalConfigDefaults.setFineMirrorX(finePosition.x);
				globalConfigDefaults.setFineMirrorY(finePosition.y);
				globalConfigMgmt.saveDefaultConfig(globalConfigDefaults);
			}
			logger.debug("performProcedureCompletion::globalConfig updated");

			// persist all the frames
			if (procedure.getProcedureCcdFrameList() != null) {
				for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {
					procedureCcdFrame.setProcedure(procedure); // need the assigned procedure id

					// save the associated centroid map
					if (procedureCcdFrame.getCentroidMap() != null) {
						CentroidMap centroidMap = centroidMapMgmt.saveCentroidMap(procedureCcdFrame.getCentroidMap());
						procedureCcdFrame.setCentroidMap(centroidMap);
					}
					
					frameMgmt.associateCcdFrame(procedureCcdFrame);

					logger.debug("performProcedureCompletion::persisting frame");

					// load up png file again because associateCcdFrame reloads ccd frame fresh
					// FIXME: we should not have to do this.
					String filename = procedureCcdFrame.getCcdFrame().getFitsFilename();

					CcdFrame loadedFitsFile = null;

					logger.debug("filename = " + filename);
					try {

						loadedFitsFile = frameMgmt.loadFitsFrame(filename);
						procedureCcdFrame.getCcdFrame().setRawFrame(loadedFitsFile.getRawFrame());

					} catch (Exception e) {
						logger.error(MessageGenerator.generateMessage("generic.error"), e);
					}
					logger.debug("performProcedureCompletion::frame persisted");

					// if a png file for display exists, read it in. Otherwise create it.
					byte[] falseColorPng = frameMgmt.loadPng(loadedFitsFile, true);
					procedureCcdFrame.getCcdFrame().setFalseColorPng(falseColorPng);

					logger.debug("performProcedureCompletion::loadedOrCreatedPng");

					
				}
			}

			logger.debug("performProcedureCompletion::all frames and centroid maps completed");
			statusLogger.saveLog(procedure.getProcedureId());

			// associate ref beam map
			if (procedure.getRefBeamMap() != null) {
				
				// if refBeam map does not exist, then create it
				if (procedure.getRefBeamMap().isNewRecord()) {
					centroidMapMgmt.saveRefBeamMap(procedure.getRefBeamMap());
				}

				centroidMapMgmt.associateRefBeamMap(procedure.getRefBeamMap(), procedure);
			}

			logger.debug("performProcedureCompletion::ref map associated");

			// persist the procedure output
			if (procedure.getProcedureOutput() != null) {
				procedureOutputMgmt.createProcedureOutput(procedure.getProcedureOutput(), procedure.getProcedureId());
				for (ProcedureIterationOutput pio : procedure.getProcedureOutput().getProcedureIterationOutputList()) {
					procedureOutputMgmt.createProcedureOutput(pio, procedure.getProcedureId());
				}
			}

			// everything is now stored. Reload somethings for immediate viewing.

			try {
				// set up for immediate viewing
				procedure.setProcedureOutput(procedureOutputMgmt.findProcedureOutput(procedure));

				logger.debug("performProcedureCompletion::procedure output set up for immediate viewing");

				// procedure frame data for immediate viewing
				for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {

					procedureMgmt.setupFrameLog(procedureCcdFrame);
				}

			} catch (Exception e) {
				// don't stop just because we can't read it all back
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
			}
			
			// do not allow procedure to complete until frame requests have been met
			frameDisplayMgmt.waitForPendingDisplays();
			

			procedureExecutionState.requestCompleteProcedure(); // if this is a subprocedure, transfer control to superprocedure
			
			procedure.setProcedureState(Procedure.PROCEDURE_STATE_COMPLETED);

			// update in database
			procedureMgmt.updateProcedure(procedure);

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public Procedure performProcedureSetup(Long procedureTypeId, Session session, String testNumber, ProcedureOutput procedureOutput)
			throws Exception {
		Procedure procedure = new Procedure();

		// get the procedure type object
		ProcedureType procedureType = procedureMgmt.findProcedureType(procedureTypeId);
		procedure.setProcedureType(procedureType);
		procedure.setTestNumber(testNumber);

		procedure.setProcedureOutput(procedureOutput);
		procedure.setSession(session);

		// if the executionStatus is 'running', then we must be starting a sub-procedure
		boolean isSubProcedure = procedureExecutionState.getExecutionStatus();
		Procedure superProcedure = isSubProcedure ? procedureExecutionState.getCurrentProcedure() : null;
		String superProcedureNumber = (superProcedure == null) ? null : superProcedure.getProcedureNumber();

		String procNum = sessionMgmt.getNextProcedureNumber(session.getSessionId(), superProcedureNumber);
		procedure.setProcedureNumber(procNum);

		ProcedureConfigDefaults procedureConfigDefaults = procedureMgmt.findDefaultProcedureConfig(
				physicalModel.getTelescope().getTelescopeId(), physicalModel.getInstrument().getInstrumentId(), procedureTypeId);

		// copy the default config into the current procedure config for potential modification
		ProcedureConfig procedureConfig = new ProcedureConfig(procedureConfigDefaults);

		// and associate it with the procedure
		procedure.getProcedureConfigSet().setProcedureConfig(procedureConfig);

		
		// if we are a ref map being called as a subprocedure, we want to use the super-procedure's values for mask, filter and sufsGroup
		if (procedureTypeId.equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP) && isSubProcedure) {
			
			// get pupilMask and Filter from the parent
			PupilMask refMapMask = superProcedure.getProcedureConfigSet().getProcedureConfig().getPupilMask();
			procedureConfig.setPupilMask(refMapMask);
			procedureConfig.setPupilMaskType(refMapMask.getPupilMaskType());
			
			Filter refMapFilter = superProcedure.getProcedureConfigSet().getProcedureConfig().getFilter();
			procedureConfig.setFilter(refMapFilter);
			procedureConfig.setFilterType(refMapFilter.getFilterType());

			Integer sufsGroupNumber = superProcedure.getProcedureConfigSet().getProcedureConfig().getSufsGroup();
			procedureConfig.setSufsGroup(sufsGroupNumber);

			// use sufs coarse mirror offsets from the super procedure
			SufsCoarseOffsetsConfig sufsCoarseOffsetsConfigSuper = superProcedure.getProcedureConfigSet().getSufsCoarseOffsetsConfig();
			SufsCoarseOffsetsConfig sufsCoarseOffsetsConfig = new SufsCoarseOffsetsConfig(sufsCoarseOffsetsConfigSuper);
			procedure.getProcedureConfigSet().setSufsCoarseOffsetsConfig(sufsCoarseOffsetsConfig);
			
			// use reference beam based on SUFS group of super procedure
			int refBeamNum = physicalModel.getSufsGroupByNumber(sufsGroupNumber).getDefaultRefBeamNum();
			ReferenceBeam referenceBeam = globalConfigMgmt.findReferenceBeamByNumber(refBeamNum);
			procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(referenceBeam);
			
			
		} else {
		
			// get the default mask, if it is installed on the wheel
			PupilMask defaultMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(procedureConfig.getPupilMaskType().getPupilMaskTypeId(),
					physicalModel.getInstrument().getCamera().getPupilWheel().getPupilWheelId());
	
			procedureConfig.setPupilMask(defaultMask);
	
			// get the filter to default to if it exists
			Filter defaultFilter = cameraDefMgmt.getFilterByFilterTypeAndWheel(procedureConfig.getFilterType().getFilterTypeId(),
					physicalModel.getInstrument().getCamera().getFilterWheel().getFilterWheelId());
	
			procedureConfig.setFilter(defaultFilter);
		
		}
		

		// if procedure type is create ref map, then populate ref beam and integration times from the table
		if (procedureTypeId.equals(ProcedureType.PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP)) {

			// these get set into procedure config
			setupCreateRefMapDefaults(procedure, physicalModel.getInstrument().getInstrumentId(),
					procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId(), 
					procedureConfig.getFilter().getFilterType().getFilterTypeId());
		}

		// select defaults based on mask and light source
		reloadFIConfig(procedure, physicalModel.getInstrument().getInstrumentId());

		// set centroid offsets calculation defaults based on procedure type
		CentroidOffsetsConfigDefaults centroidOffsetsConfigDefaults = globalConfigMgmt
				.findCentroidOffsetsConfig(procedureType.getProcedureTypeId());
		procedure.getProcedureConfigSet().setCentroidOffsetsConfig(new CentroidOffsetsConfig(centroidOffsetsConfigDefaults));

		// select defaults based on pupil mask
		reloadPupilRegErrorConfig(procedure);
		
		// set pupilRegErrorCalc defaults based on pupilMaskType
		if (procedureConfig.getPupilMaskType().isPupilMaskTypePh() || procedureConfig.getPupilMaskType().isPupilMaskTypeFs()) {
			PupilRegErrorConfigDefaults pupilRegErrorConfigDefaults = 
					globalConfigMgmt.findPupilRegErrorConfig(procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());
			PupilRegErrorConfig pupilRegErrorConfig = new PupilRegErrorConfig(pupilRegErrorConfigDefaults);
			procedure.getProcedureConfigSet().setPupilRegErrorConfig(pupilRegErrorConfig);
		}

		// set calcm2m1 defaults
		if (procedure.getProcedureType().isFineScreen()) {
			CalcM2M1ConfigDefaults calcM2M1ConfigDefaults = 
					globalConfigMgmt.findCalcM2M1Config(procedure.getProcedureType().getProcedureTypeId());
			CalcM2M1Config calcM2M1Config = new CalcM2M1Config(calcM2M1ConfigDefaults);
			procedure.getProcedureConfigSet().setCalcM2M1Config(calcM2M1Config);
		}

		// get AutoRefMapDefaults based on procedure type
		AutoRefMapConfigDefaults autoRefMapConfigDefaults = globalConfigMgmt
				.findAutoRefMapConfig(procedure.getProcedureType().getProcedureTypeId());
		procedure.getProcedureConfigSet().setAutoRefMapConfig(new AutoRefMapConfig(autoRefMapConfigDefaults));

		// get AutoCenterTelDefaults
		AutoCenterTelConfigDefaults autoCenterTelConfigDefaults = globalConfigMgmt
				.findAutoCenterTelConfig(procedure.getProcedureType().getProcedureTypeId());
		procedure.getProcedureConfigSet().setAutoCenterTelConfig(new AutoCenterTelConfig(autoCenterTelConfigDefaults));

		
		if (procedureType.isSufs()) {
		
			// init group to one
			procedureConfig.setSufsGroup(1);
			
			// get SufsOffsetsToZernikesConfigDefaults
			SufsOffsetsToZernikesConfigDefaults sufsOffsetsToZernikesConfigDefaults = globalConfigMgmt.findSufsOffsetsToZernikesConfig();
			procedure.getProcedureConfigSet().setSufsOffsetsToZernikesConfig(new SufsOffsetsToZernikesConfig(sufsOffsetsToZernikesConfigDefaults));
			
			// get SufsCoarseOffsetsConfigDefaults
			SufsCoarseOffsetsConfigDefaults sufsCoarseOffsetsConfigDefaults = globalConfigMgmt.findSufsCoarseOffsetsConfig(
					physicalModel.getInstrument().getInstrumentId(), new Long(procedureConfig.getSufsGroup()));
			procedure.getProcedureConfigSet().setSufsCoarseOffsetsConfig(new SufsCoarseOffsetsConfig(sufsCoarseOffsetsConfigDefaults));
		}
		
		// clear any marking
		frameDisplayMgmt.clearMarking();

		procedure.setProcedureState(Procedure.PROCEDURE_STATE_NEW);

		return procedure;
	}

	public void setupCreateRefMapDefaults(Procedure procedure, Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		RefMapConfigDefaults refMapConfigDefaults = globalConfigMgmt.findRefMapConfigDefaults(instrumentId, pupilMaskTypeId, filterTypeId);

		// Set up default ref beam and int time

		procedure.getProcedureConfigSet().getProcedureConfig().setLightSource(ProcedureConfig.LIGHT_SOURCE_LED);
		
		if (procedure.getProcedureConfigSet().getProcedureConfig().getPupilMaskType().isPupilMaskTypeSufs()) {
			// override the defaults depending on the SUFS group
			int sufsGroup = procedure.getProcedureConfigSet().getProcedureConfig().getSufsGroup(); 
			// if this is the first time (e.g. sufsGroup == 0) then set to one
			sufsGroup = (sufsGroup == 0) ? 1 : sufsGroup;
			int refBeamNum = physicalModel.getSufsGroupByNumber(sufsGroup).getDefaultRefBeamNum();
			ReferenceBeam referenceBeam = globalConfigMgmt.findReferenceBeamByNumber(refBeamNum);
			procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(referenceBeam);

		} else {
			procedure.getProcedureConfigSet().getProcedureConfig().setReferenceBeam(refMapConfigDefaults.getReferenceBeam());
		}
		procedure.getProcedureConfigSet().getProcedureConfig().setIntegrationTime(refMapConfigDefaults.getIntegrationTime());

		// make the list of possible int times equal to the 'one' we have
		List<Float> integrationTimeList = new ArrayList<Float>();
		integrationTimeList.add(refMapConfigDefaults.getIntegrationTime());
		procedure.getProcedureConfigSet().getProcedureConfig().setIntegrationTimeList(integrationTimeList);
	}
	
	public void reloadFIConfig(Procedure procedure, Long instrumentId) throws Exception {
		
		if (!procedure.getProcedureType().isCenterTelescope()) {

			PupilMask selectedMask = procedure.getProcedureConfigSet().getProcedureConfig().getPupilMask();
		
			// reload FI Config Defaults when pupil mask changes
			FIConfigDefaults fiConfigDefaults = globalConfigMgmt.findFIConfigDefaults(instrumentId, 
					selectedMask.getPupilMaskType().getPupilMaskTypeId(),
					procedure.getProcedureConfigSet().getProcedureConfig().getLightSource());
		
			procedure.getProcedureConfigSet().setFiConfig(new FIConfig(fiConfigDefaults));
		}
		
	}
	
	public void reloadPupilRegErrorConfig(Procedure procedure) throws Exception {
	
		ProcedureConfig procedureConfig = procedure.getProcedureConfigSet().getProcedureConfig();	
			
		// set pupilRegErrorCalc defaults based on pupilMaskType
		if (procedureConfig.getPupilMaskType().isPupilMaskTypePh() || procedureConfig.getPupilMaskType().isPupilMaskTypeFs()) {
			PupilRegErrorConfigDefaults pupilRegErrorConfigDefaults = 
					globalConfigMgmt.findPupilRegErrorConfig(procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeId());
			PupilRegErrorConfig pupilRegErrorConfig = new PupilRegErrorConfig(pupilRegErrorConfigDefaults);
			procedure.getProcedureConfigSet().setPupilRegErrorConfig(pupilRegErrorConfig);
		}
	
	}

}
