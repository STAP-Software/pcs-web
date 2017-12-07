/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.ApplicationException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CorrectOverscanDarkResult;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extinf.TimeoutException;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.FitsFilesMaps;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.business.CameraStateMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.BadDarkMedianValueException;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.statusLog.business.StatusLogger;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;
import org.tmt.aps.peas.visualization.model.UserPrompt;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.HDU;
import nom.tam.fits.Header;
import nom.tam.fits.PrimaryHDU;
import nom.tam.util.BufferedDataOutputStream;

/**
 * Session EJB that manages frames: CCD frame taking, FITS frame loading/storing, frame database record reading/writing. 
 * @author smichaels
 */
@Stateless
public class FrameMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	@EJB
	PeasProperties peasProperties;
	@EJB
	FrameSimulator frameSimulator;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	CameraStateMgmt cameraStateMgmt;
	@EJB
	ProcedureExecutionState procedureExecutionState;
	@EJB
	CcdMgmt ccdMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	@EJB
	CameraMgmt cameraMgmt;
	@EJB
	private ComputationLibraryImpl computationLibrary;
	@EJB
	ExtInfConfigState extInfConfigState;
	@EJB
	StatusLogger statusLogger;
	@EJB
	UserPromptMgmt userPromptMgmt;


	/**
	 * Calls {@link #loadFitsFrame(String)}
	 * @param fitsFilename
	 */
	public CcdFrame getCcdFrame(String fitsFilename) throws Exception {

		CcdFrame ccdFrame = loadFitsFrame(fitsFilename);
		return ccdFrame;
	}

	/**
	 * Searches for a CcdFrame record by fitsFilename
	 * @param fitsFilename the fitsFilename to match
	 * @return the CcdFrame entity
	 */
	public CcdFrame findCcdFrame(String fitsFilename) {

		try {
			TypedQuery<CcdFrame> query = em.createNamedQuery("findCcdFrameByFilename", CcdFrame.class);
			query.setParameter("fitsFilename", fitsFilename);

			return query.getSingleResult();

		} catch (Exception e) {
			// this is ok, since this is only called internally, and we check for a null value
			return null;
		}
	}

	/**
	 * Returns a list of ProcedureCcdFrames for the passed procedure
	 * @param procedureId the procedure id to search on
	 * @return a list of ProcedureCcdFrames; each ProcedureCcdFrame structure also includes CcdFrame and CentroidMap entities
	 */
	public List<ProcedureCcdFrame> getFramesForProcedure(Long procedureId) {

		TypedQuery<ProcedureCcdFrame> query = em.createNamedQuery("findAllFramesForProcedure", ProcedureCcdFrame.class);
		query.setParameter("procedureId", procedureId);

		return query.getResultList();

	}

	/**
	 * Saves a frame to FITS file  
	 * @param procedureCcdFrame the procedureCcdFrame structure containing the information to create the FITS filename and the raw frame
	 * @throws Exception
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveCcdFrame(ProcedureCcdFrame procedureCcdFrame) throws Exception {
		// determine FITS file name
		
		ProcedureConfig procedureConfig = procedureCcdFrame.getProcedure().getProcedureConfigSet().getProcedureConfig();
		
		FitsFilename fitsFilename = new FitsFilename(
				procedureCcdFrame.getProcedure().getTelescope().getTelescopeId(), 
				procedureCcdFrame.getProcedure().getProcedureType().getProcedureTypeCd(), 
				procedureCcdFrame.getProcedure().getProcedureNumber(),
				procedureCcdFrame.getProcedureIterationNumber(), 
				procedureConfig.getUfsSegment(),
				procedureConfig.getSufsGroup(), 
				procedureCcdFrame.getPhasingStepNumber(),
				procedureConfig.getFilter().getFilterNameAsNumber(),
				procedureConfig.getPupilMask().getPupilMaskType().getPupilMaskTypeName(),
				procedureConfig.getFilter().getFilterName());

		// save the frame to a FITS file
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
		ccdFrame.setFitsFilename(fitsFilename.generateFileName());
		ccdFrame.setInstrumentId(procedureCcdFrame.getProcedure().getInstrument().getInstrumentId());
		
		
		boolean overwritten = saveFitsFrame(ccdFrame);

		// save the Ccd record with the fits file name
		//logger.info(MessageGenerator.generateMessage("record.create", "ccdFrame"));
		//em.persist(ccdFrame);

		//associateCcdFrame(procedureCcdFrame);
		
		// create the png.  This will overwrite any previously generated png file with the same FITS name prefix
		byte[] falseColorPng = generatePng(ccdFrame, true);
		ccdFrame.setFalseColorPng(falseColorPng);

	}

	/**
	 * Associate a ccdFrame with a procedure.  If the ccdFrame may only have a FITS filename.  This function finds the CcdFrame record in the database 
	 * if it exists, otherwise it is stored in the database at this time.  
	 * @param procedureCcdFrame the procedure CcdFrame structure.  This may not be fully populated with a raw frame, but must at least have a FITS Filename
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void associateCcdFrame(ProcedureCcdFrame procedureCcdFrame) {
		// create a ProcedureCcdRecord

		// the passed CcdFrame will only have a filename
		// we need to read from the DB to get the real record

		CcdFrame ccdFrame = findCcdFrame(procedureCcdFrame.getCcdFrame().getFitsFilename());

		if (ccdFrame == null) {
			// we have to save it for the first time ourselves. This is how we avoid having to
			// populate the database with legacy values using a script, just do it as needed.
			//ccdFrame = new CcdFrame();
			//ccdFrame.setCreateDate(new Date());
			//ccdFrame.setFitsFilename(procedureCcdFrame.getCcdFrame().getFitsFilename());
			
			ccdFrame = procedureCcdFrame.getCcdFrame();
			
			CameraState cameraState = ccdFrame.getCameraState();
			
			if (cameraState != null) {
				logger.info(MessageGenerator.generateMessage("record.create", "cameraState"));
				em.persist(cameraState);
			}
			logger.info(MessageGenerator.generateMessage("record.create", "ccdFrame"));
			em.persist(ccdFrame);
		} 
	
		procedureCcdFrame.setCcdFrame(ccdFrame); // now the ccdFrame has a primary key

		if (procedureCcdFrame.getCentroidMap() != null) {
			em.merge(procedureCcdFrame.getCentroidMap());  // attach the detached object 
		}
		
		// perform the association
		logger.info(MessageGenerator.generateMessage("record.create", "procedureCcdFrame"));
		em.persist(procedureCcdFrame);
	}

	/*
	 * This should become a subprocedure
	 */
	private CcdFrame readFrameFromCcd(double exposureTime, ProcedureConfig procedureConfig, ProcedureType procedureType, String procedureNumber, List<Rect> badPixelList, boolean removeBadPixels) throws Exception {
		
		
		// if this is using a simulator for ccdMgmt, lets get a real frame for use depending on procedureType
		boolean ccdSimulator = !extInfConfigState.getExtInfConnectConfig().isCameraEnabled();
		
		// get the frame from CCD or from file, depending on the called type			
			
		
		int[][] frame = null;
		
		try {
			
			frame = ccdMgmt.getOverscannedImage(exposureTime);
		
		} catch (TimeoutException e) {
			
			String text = MessageGenerator.generateMessage("ccd.shutter_timeout");
			
			statusLogger.log("ccd.shutter_timeout");

			String[] choices = {"Continue", "Abort Test"};
			int[] values = {UserPrompt.PROMPT_VALUE_FLOW_CONTROL_CONTINUE, UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT};

			int response = userPromptMgmt.displayGenericMultiChoiceDialog("CCD Shutter Timeout", text, choices, values);

			if (response == UserPrompt.PROMPT_VALUE_FLOW_CONTROL_ABORT) {
				throw new AbortProcedureException("User Aborted Test");
			} 
		}
		
		
		
		
		CcdFrame ccdFrame = null;
		
		if (ccdSimulator) {
			// here we make a better frame than the external package simulator can
			// TODO: determine if this should be put in the simulator.  Will require a change in app packaging.
			// TODO: This needs to be improved to get a frame from file given the procedure type			
			
			switch (procedureConfig.getPupilMaskType().getPupilMaskTypeId().intValue()) {
			
			case Constants.PUPIL_MASK_PASSIVE_TILT: 
				ccdFrame = loadFitsFrame(peasProperties.getProp("org.tmt.aps.peas.frame_simulator_pt"));
				break;
			case Constants.PUPIL_MASK_FINE_SCREEN:
				ccdFrame = loadFitsFrame(peasProperties.getProp("org.tmt.aps.peas.frame_simulator_fs"));
				break;
			case Constants.PUPIL_MASK_PHASING:
				ccdFrame = loadFitsFrame(peasProperties.getProp("org.tmt.aps.peas.frame_simulator_ph"));
				break;
			case Constants.PUPIL_MASK_SUFS:
				ccdFrame = loadFitsFrame(peasProperties.getProp("org.tmt.aps.peas.frame_simulator_sufs"));
				break;
			default: 
				// none
				ccdFrame = loadFitsFrame(peasProperties.getProp("org.tmt.aps.peas.frame_simulator_ct"));
				break;
			
			}
							
			
			byte[] falseColorPng = loadPng(ccdFrame, true);
			ccdFrame.setFalseColorPng(falseColorPng);
			
			telescopeMgmt.refreshStatus();

			ccdFrame = populateCcdFrame(ccdFrame, exposureTime, procedureConfig.getSufsGroup(), -1, -1);
						
		} else {
		
			// TODO: does this need to be done in parallel with getting the exposure?
			// get the telescope status
			telescopeMgmt.refreshStatus();
			
			int[][] swapFrame = new int[frame[0].length][frame.length];
			for (int i = 0; i < frame[0].length; i++) {
				for (int j = 0; j < frame.length; j++) {
					swapFrame[i][j] = frame[j][i];
				}
			}
			
			
			// if the image is an overscan image, then correct for overscan
			Ccd ccd = physicalModel.getInstrument().getCcd();
			if (ccd.getCcdType().isTypeSciMeas() && swapFrame.length == ccd.getCcdType().getOverscanReadoutWidth()) {
			
				// correct the overscan image into a corrected image without overscan columns
				int overscanSize = (ccd.getCcdType().getOverscanReadoutWidth() - ccd.getCcdType().getNormalReadoutWidth())/2;
				
				CorrectOverscanDarkResult result = computationLibrary.correctOverscanFrameDarkOffsets(swapFrame, 
						ccd.getDarkOvscnLeftColStart(), 
						ccd.getDarkOvscnLeftColEnd(), 
						ccd.getDarkOvscnRightColStart(), 
						ccd.getDarkOvscnRightColEnd(),
						overscanSize);		
				
				
				if (result.getDarkMedianValueLeft() == 0 || result.getDarkMedianValueRight() == 0) {
					throw new BadDarkMedianValueException("CCD left or right median bias is zero!  Adjust CCD bias offsets values");
				}
				
				int[][] correctedFrame = result.getCorrectedFrame();
				if (removeBadPixels && badPixelList != null && badPixelList.size() > 0) {
					//removeBadPixels works on "swaped" frame, X is columns, Y is rows.
					correctedFrame = computationLibrary.removeBadPixels(correctedFrame, badPixelList);
				}
				
				
				short[][] rawFrame = new short[correctedFrame.length][correctedFrame[0].length];
				for (int i = 0; i < correctedFrame.length; i++) {
					for (int j = 0; j < correctedFrame[0].length; j++) {
						rawFrame[i][j] = (short) correctedFrame[i][j];
					}
				}
				


	
				ccdFrame = populateCcdFrame(rawFrame, exposureTime, procedureConfig.getSufsGroup(), 
						result.getDarkMedianValueLeft(), result.getDarkMedianValueRight());
			
			} else {
				
				short[][] rawFrame = new short[frame.length][frame[0].length];
				for (int i = 0; i < frame.length; i++) {
					for (int j = 0; j < frame[0].length; j++) {
						rawFrame[i][j] = (short) swapFrame[i][j];
					}
				}

				
				ccdFrame = populateCcdFrame(rawFrame, exposureTime, procedureConfig.getSufsGroup(), -1, -1);
			}
		}

		return ccdFrame;
	}
	
	public CcdFrame populateCcdFrame(short[][] rawFrame, double exposureTime, int sufsGroup) {
		return populateCcdFrame(rawFrame, exposureTime, sufsGroup, 0, 0);
	}
	
	public CcdFrame populateCcdFrame(short[][] rawFrame, double exposureTime, int sufsGroup, int darkMedianLeft, int darkMedianRight) {

		
		CcdFrame ccdFrame = new CcdFrame();
		ccdFrame.setAxes1(rawFrame.length);
		ccdFrame.setAxes2(rawFrame[0].length);
		ccdFrame.setRawFrame(rawFrame);
		ccdFrame.setCreateDate(new Date());
		ccdFrame.setNoOfAxes(2);
		
		// TODO: ccdFrame needs darkMedian left and right fields.  Add values right here.
		
		return populateCcdFrame(ccdFrame, exposureTime, sufsGroup, darkMedianLeft, darkMedianRight);
	}

	public CcdFrame populateCcdFrame(CcdFrame ccdFrame, double exposureTime, int sufsGroup, int darkMedianLeft, int darkMedianRight) {

		
		// save the camera state when the ccd frame was taken
		Instrument instrument = physicalModel.getInstrument();
		CameraState cameraState = new CameraState(instrument);
		ccdFrame.setCameraState(cameraState);
		ccdFrame.setInstrumentId(instrument.getInstrumentId());
		Telescope telescope = physicalModel.getTelescope();

		// store telescope information with frame when it is taken
		ccdFrame.setAvgMirrorTemp((float)telescope.getMirrorTemp());
		
		if (telescope.getM2Position() != null) {
			ccdFrame.setSecondaryAct1((float)telescope.getM2Position()[0]);
			ccdFrame.setSecondaryAct2((float)telescope.getM2Position()[1]);
			ccdFrame.setSecondaryAct3((float)telescope.getM2Position()[2]);
		}
		
		if (telescope.getTelPosition() != null) {
			ccdFrame.setTelescopeAz(telescope.getTelPosition().x);
			ccdFrame.setTelescopeEl(telescope.getTelPosition().y);
		}
		
		ccdFrame.setIntTime((float)exposureTime);
		ccdFrame.setSufsGroupNumber(sufsGroup);
		ccdFrame.setDarkMedianLeft(darkMedianLeft);
		ccdFrame.setDarkMedianRight(darkMedianRight);

		Ccd ccd = physicalModel.getInstrument().getCcd();
		
		ccdFrame.setCcdName(ccd.getCcdName());
		ccdFrame.setCcdGainValue(ccd.getCcdGain().getGainValue());
		ccdFrame.setCcdGainOffsetChannel0(ccd.getChannelOffset0());
		ccdFrame.setCcdGainOffsetChannel1(ccd.getChannelOffset1());
		
		ccdFrame.setCcdGainNumber(ccd.getCurrentGainNumber());
		ccdFrame.setCaseTemperature(ccd.getCaseTemperature());
		ccdFrame.setLeftTemperature(ccd.getLeftTemperature());
		ccdFrame.setRightTemperature(ccd.getRightTemperature());
		ccdFrame.setTemperatureSetting((float)ccd.getTemperatureSetting());

		return ccdFrame;
	}
	
	/**
	 * Returns a CcdFrame as part of a ProcedureCcdFrame structure either from reading the CCD or from the simulator, if read from CCD, the CcdFrame is saved to the database.
	 * @param procedureConfig used to determine the frame source, CCD or simulator
	 * @param procedureType the procedure type, used to populate the FITS filename
	 * @param procedureNumber the procedure number used to populate the FITS filename
	 * @param iteration the procedure iteration number used to populate the FITS filename
	 * @param frameNumber the frame number used to populate the FITS filename
	 * @param exposureTime the CCD exposure time
	 * @param badPixelList a list of bad pixels for the CCD
	 * @param removeBadPixels flag to remove bad pixels
	 * @return a procedureCcdFrame structure populated with the ccdFrame and procedure.  
	 * @throws Exception
	 */
	public ProcedureCcdFrame getProcedureCcdFrame(ProcedureConfig procedureConfig, ProcedureType procedureType, String procedureNumber, 
			int iteration, int frameNumber, double exposureTime, List<Rect> badPixelList, boolean removeBadPixels) throws Exception {

		CcdFrame ccdFrame = (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) ?
			readFrameFromCcd(exposureTime, procedureConfig, procedureType, procedureNumber, badPixelList, removeBadPixels) :
			frameSimulator.getFrame(frameNumber);
		
		
		procedureExecutionState.setCurrentFrame(ccdFrame);
		Procedure procedure = procedureExecutionState.getCurrentProcedure();

		ProcedureCcdFrame procedureCcdFrame = new ProcedureCcdFrame();
		procedureCcdFrame.setCcdFrame(ccdFrame);
		procedureCcdFrame.setNewFrameFlg(false); // frame from file
		procedureCcdFrame.setProcedureFrameNumber(frameNumber);
		procedureCcdFrame.setProcedure(procedure);
		if (procedureType.isCoarsePhasing()) {
			procedureCcdFrame.setProcedureIterationNumber(1); 
			procedureCcdFrame.setPhasingStepNumber(iteration);
			procedureCcdFrame.setPhasingFilterNumber(0);
		} else if (procedureType.isNarrowBandPhasing()) {
				procedureCcdFrame.setProcedureIterationNumber(1); 
				procedureCcdFrame.setPhasingStepNumber(0);
				procedureCcdFrame.setPhasingFilterNumber(iteration);
		} else {
			procedureCcdFrame.setProcedureIterationNumber(iteration);
			procedureCcdFrame.setPhasingStepNumber(0);
			procedureCcdFrame.setPhasingFilterNumber(0);
		}
		
		// add it to the procedure
		procedure.addProcedureCcdFrame(procedureCcdFrame);

		if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {
			// generate filename and store into the FITS file
			saveCcdFrame(procedureCcdFrame);			
		}



		return procedureCcdFrame;
	}

	/**
	 * @return a list of all fits files in the fits repository path specified in the peas.properties file
	 */
	public List<FitsFilename> findAllFitsFiles() throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		logger.debug("frame folder = " + frameFolder);

		// read in and parse each frame and build up
		File folder = new File(frameFolder);

		List<FitsFilename> fitsFileList = new ArrayList<FitsFilename>();

		for (File fileEntry : folder.listFiles()) {

			String filename = fileEntry.getName();

			if (filename.toLowerCase().endsWith(".fts")) {

				
				FitsFilename fitsFile = new FitsFilename(filename);
				
				if (fitsFile.isValid()) {

					fitsFileList.add(fitsFile);
	
					// one time only conversion - UNCOMMENT TO GENERATE PNG FILES FOR ALL FITS FILES
					// logger.info("file: " + filename);
					// CcdFrame ccdFrame = loadFitsFrame(filename);
					// loadPng(ccdFrame, true);
			
				} else {
					
					logger.error("Incorrect FITS filename format, file not added to list: " + filename);
				}
			}
		}

		return fitsFileList;
	}

	/**
	 * Find all FITS files in the fits repository path specified in the peas.properties file matching the filter
	 * @param filter wildcard filter
	 * @return the fits files that match the filter
	 */
	public List<FitsFilename> findMatchingFitsFiles(String filter) throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		logger.debug("frame folder = " + frameFolder);

		// read in and parse each frame and build up
		File folder = new File(frameFolder);

		List<FitsFilename> fitsFileList = new ArrayList<FitsFilename>();

		FileFilter fileFilter = new WildcardFileFilter(filter);

		for (File fileEntry : folder.listFiles(fileFilter)) {

			String filename = fileEntry.getName();

			if (filename.toLowerCase().endsWith(".fts")) {

				FitsFilename fitsFile = new FitsFilename(filename);

				fitsFileList.add(fitsFile);

			}
		}

		return fitsFileList;
	}

	/**
	 * Loads a FITS frame given its input stream and filename
	 * @param is the input stream to use
	 * @param filename the filename
	 * @return a CcdFrame entity containing the raw frame data
	 */
	public CcdFrame loadFitsFrame(InputStream is, String filename) throws Exception {
		Fits fitsFile = new Fits(is);
		return loadFitsFrame(fitsFile, filename);
	}

	/**
	 * Returns a frame given its FITS filename
	 * @param fitsFilename the fits filename
	 * @return the CcdFrame entitiy containing the raw frame data
	 */
	public CcdFrame loadFitsFrame(String fitsFilename) throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		String path = frameFolder + File.separator + fitsFilename;
		Fits fitsFile = new Fits(path);

		return loadFitsFrame(fitsFile, fitsFilename);
	}

	/** 
	 * Loads a FITS file given its Fits descriptor and filename
	 * @param fitsFile the descriptor 
	 * @param fitsFilename the FITS filename 
	 * @return the CcdFrame entitiy containing the raw frame data
	 */
	public CcdFrame loadFitsFrame(Fits fitsFile, String fitsFilename) throws Exception {

		BasicHDU[] bhdus = fitsFile.read();
		CcdFrame ccdFrame = new CcdFrame();
		ccdFrame.setFitsFilename(fitsFilename);

		if (bhdus != null) {

			for (int index = 0; index < bhdus.length; index++) {
				BasicHDU hdu = bhdus[index];

				logger.debug("hdu.class = " + hdu.getClass());

				PrimaryHDU imhdu = (PrimaryHDU) hdu;
				// imhdu.info();

				Data data = imhdu.getData();

				int leng = (int) data.getTrueSize(); // VS PADDED
				logger.debug("Length=" + leng);
				logger.debug("Data=" + data);
				int[] axes = imhdu.getAxes();

				logger.debug("data.getData: " + data.getData());

				short[][] shortArray = (short[][]) data.getData();

				logger.debug(imhdu.getBitPix() + " bits per pixel");
				logger.debug("Data = " + data.getData().getClass());

				int bpix = (int) imhdu.getBitPix();

				ccdFrame.setBitPix(bpix);

				ccdFrame.setNoOfAxes(imhdu.getHeader().getIntValue("NAXIS"));

				ccdFrame.setAxes1(axes[1]);

				ccdFrame.setAxes2(axes[0]);

				short[][] rawFrame = new short[shortArray[0].length][shortArray.length];

				for (int i = 0; i < shortArray[0].length; i++) {
					for (int j = 0; j < shortArray.length; j++) {
						rawFrame[i][j] = shortArray[j][i];
					}
				}

				ccdFrame.setRawFrame(rawFrame);
				
				// get pupilMask
				String mask = imhdu.getHeader().getStringValue("MASK");
				
				// TODO: we need metadata store that we can access for pupilmasktype so that Cd to MaskType mapping can be accessed.
				// for now, hardcode it
				
				PupilMaskType headerPupilMaskType = null;
				if (mask.equals("PT") || mask.equals("036")) {
					headerPupilMaskType = physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
				} else if (mask.equals("FS") || mask.equals("508")) {
					headerPupilMaskType = physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_508);
				} else if (mask.equals("PH") || mask.equals("CPH") || mask.equals("160")) {
					headerPupilMaskType = physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_160);
				} else if (mask.equals("UFS")) {
					headerPupilMaskType = physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_UFS);
				} else {
					headerPupilMaskType = physicalModel.getPupilMaskTypeById(PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS);
				}
				
				ccdFrame.setHeaderPupilMaskType(headerPupilMaskType);
				
				System.out.println("MASK = " + mask);
				
				String filter = imhdu.getHeader().getStringValue("FILTER");
				System.out.println("FILTER = " + filter);
				
				ccdFrame.setCcdName(imhdu.getHeader().getStringValue("CCD"));
				ccdFrame.setCcdGainValue(imhdu.getHeader().getFloatValue("CCDGAIN"));
				ccdFrame.setCcdGainOffsetChannel0(imhdu.getHeader().getIntValue("OFFSET0"));
				ccdFrame.setCcdGainOffsetChannel1(imhdu.getHeader().getIntValue("OFFSET1"));
				

				// fb.setObsDate(imhdu.getHeader().getStringValue("DATE-OBS"));

				Header header = hdu.getHeader();

				logger.debug("header = " + header);
			}

		}
		return ccdFrame;
	}

	/**
	 * Saves a frame to a FITS file
	 * @param ccdFrame the frame to save 
	 * @return true if the file already existed and was overwritten
	 */
	public boolean saveFitsFrame(CcdFrame ccdFrame) throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		logger.debug("ccdFrame = " + ccdFrame);
		String path = frameFolder + File.separator + ccdFrame.getFitsFilename();

		boolean overwrite = new File(path).exists();
		
		// First create a null FITS object.
		Fits myFits = new Fits();

		// Now create three extensions.
		// reverse the frame to match legacy frames
		short[][] reversedFrame = new short[ccdFrame.getRawFrame()[0].length][ccdFrame.getRawFrame().length];
		for (int i = 0; i < ccdFrame.getRawFrame().length; i++) {
			for (int j = 0; j < ccdFrame.getRawFrame()[i].length; j++) {
				reversedFrame[j][i] = ccdFrame.getRawFrame()[i][j];
			}
		}

		myFits.addHDU(HDU.create(reversedFrame));
		
		// enter standard headers
		myFits.getHDU(0).getHeader().addIntValue("BITPIX", 16, "");
		myFits.getHDU(0).getHeader().addIntValue("NAXIS", 2, "");
		myFits.getHDU(0).getHeader().addIntValue("NAXIS1", ccdFrame.getRawFrame().length, "");
		myFits.getHDU(0).getHeader().addIntValue("NAXIS2", ccdFrame.getRawFrame()[0].length, "");
		myFits.getHDU(0).getHeader().addIntValue("PCOUNT", 0, "");
		myFits.getHDU(0).getHeader().addIntValue("GCOUNT", 1, "");
		myFits.getHDU(0).getHeader().addBooleanValue("EXTEND", true, "");

		
		Camera camera = physicalModel.getInstrument().getCamera();
		Telescope telescope = physicalModel.getTelescope();
		if (procedureExecutionState.getExecutionStatus() && procedureExecutionState.getCurrentProcedure() != null) {
			ProcedureConfig procedureConfig = procedureExecutionState.getCurrentProcedure().getProcedureConfigSet().getProcedureConfig();
			myFits.getHDU(0).getHeader().addFloatValue("INT_TIME", procedureConfig.getIntegrationTime(), "Integration Time (sec)");
			if (procedureConfig.getSufsGroup() != null) {
				myFits.getHDU(0).getHeader().addIntValue("SUFS_GRP", procedureConfig.getSufsGroup(), "SUFS Group Number");
			}
			myFits.getHDU(0).getHeader().addStringValue("PROC_NUM", procedureExecutionState.getCurrentProcedure().getProcedureNumber(), "Procedure Number");
			
		} else {
			// manually taken frame
			myFits.getHDU(0).getHeader().addFloatValue("INT_TIME", ccdFrame.getIntTime(), "Integration Time (sec)");
		}
		
		myFits.getHDU(0).getHeader().addStringValue("FILTER", camera.getFilterWheel().getSelectedFilter().getFilterName(), "Filter Name");
		myFits.getHDU(0).getHeader().addStringValue("MASK", camera.getPupilWheel().getSelectedPupilMask().getMaskName(), "Mask Name");
		myFits.getHDU(0).getHeader().addStringValue("INSTRUME", physicalModel.getInstrument().getInstrumentName(), "Instrument Name");
		myFits.getHDU(0).getHeader().addStringValue("TELESCOP", telescope.getTelescopeName(), "Telescope Name");
		myFits.getHDU(0).getHeader().addFloatValue("AZ", telescope.getTelPosition().x, "Telescope Az");
		myFits.getHDU(0).getHeader().addFloatValue("EL", telescope.getTelPosition().y, "Telescope El");
		
		Ccd ccd = physicalModel.getInstrument().getCcd();
		
		myFits.getHDU(0).getHeader().addStringValue("CCD", ccd.getCcdName(), "CCD Name");
		myFits.getHDU(0).getHeader().addFloatValue("CCDGAIN", ccd.getCcdGain().getGainValue(), "CCD Gain");
		myFits.getHDU(0).getHeader().addIntValue("OFFSET0", ccd.getCcdGain().getGainOffsetChannel0(), "CCD Gain Offset Channel 0");
		myFits.getHDU(0).getHeader().addIntValue("OFFSET1", ccd.getCcdGain().getGainOffsetChannel1(), "CCD Gain Offset Channel 1");
		myFits.getHDU(0).getHeader().addFloatValue("PIXELSIZ", ccd.getCcdType().getPixelSize(), "CCD Pixel Size");

		myFits.getHDU(0).getHeader().addIntValue("DARKMEDL", ccdFrame.getDarkMedianLeft(), "Frame Dark Median Value Left Channel");
		myFits.getHDU(0).getHeader().addIntValue("DARKMEDR", ccdFrame.getDarkMedianRight(), "Frame Dark Media Value Right Channel");

		
		java.io.FileOutputStream fo = new java.io.FileOutputStream(path);
		BufferedDataOutputStream o = new BufferedDataOutputStream(fo);
		myFits.write(o);
		
		return overwrite;
	}

	/**
	 * Loads a png file to a byte array.  
	 * @param ccdFrame the ccdFrame containing the FITS filename 
	 * @param writeToFile if true should write to a file if it does not exist
	 */
	public byte[] loadPng(CcdFrame ccdFrame, boolean writeToFile) throws Exception {
		// TODO: clean up loadPng usage.  This method should never write to a file. Those methods that call this and really need to write to file should use generatePng()


		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");
		String path = frameFolder + File.separator + ccdFrame.getFitsFilename();

		path = path.substring(0, path.length() - 3) + "png";

		File pngFile = new File(path);
		
		try {
			byte[] falseColorPng = FileUtils.readFileToByteArray(pngFile);
			return falseColorPng;
		} catch (Exception e) {
			FalseColorProcessor falseColorer = new FalseColorProcessor();
			byte[] falseColorPng = falseColorer.createImage(ccdFrame.getRawFrame());

			if (writeToFile) {
				FileUtils.writeByteArrayToFile(pngFile, falseColorPng);
			}
			return falseColorPng;
		}
	}
	
	/**
	 * Generates a png file and writes to a file if writeToFile flag is set
	 * @param ccdFrame the ccdFrame supplying the raw frame and fits filename to this method
	 * @param writeToFile if true, also write the png image to file
	 * @return the pmg byte array
	 */
	public byte[] generatePng(CcdFrame ccdFrame, boolean writeToFile) throws Exception {
		
		FalseColorProcessor falseColorer = new FalseColorProcessor();
		byte[] falseColorPng = falseColorer.createImage(ccdFrame.getRawFrame());

		if (writeToFile) {
		
			String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");
			String path = frameFolder + File.separator + ccdFrame.getFitsFilename();
	
			path = path.substring(0, path.length() - 3) + "png";
	
			File pngFile = new File(path);

			FileUtils.writeByteArrayToFile(pngFile, falseColorPng);
		}
		return falseColorPng;

	}

	/**
	 * Builds maps of FITS files for file browsing tree structures
	 * @return an object tuple of tree structures for browsing FITS files in the frame tools user interface 
	 */
	public FitsFilesMaps generateFitsFilesMaps() throws Exception {

		// search folder for fits files
		Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits = new HashMap<Integer, Map<Date, List<FitsFilename>>>();

		Map<String, List<FitsFilename>> type2Fits = new HashMap<String, List<FitsFilename>>();

		
		List<FitsFilename> fitsFileList = findAllFitsFiles();

		for (FitsFilename fitsFile : fitsFileList) {

			try {
				Map<Date, List<FitsFilename>> telescopeFitsMap = telescope2Fits.get(new Integer(fitsFile.getTelescope()));
				if (telescopeFitsMap == null) {
					telescopeFitsMap = new TreeMap<Date, List<FitsFilename>>();
					telescope2Fits.put(new Integer(fitsFile.getTelescope()), telescopeFitsMap);
				}

				// logger.debug("map get filename = " + fitsFile.getFileName());
				// logger.debug("map get dateString = " + fitsFile.getDate());

				List<FitsFilename> dateFitsList = telescopeFitsMap.get(fitsFile.getDate());
				if (dateFitsList == null) {
					dateFitsList = new ArrayList<FitsFilename>();
					telescopeFitsMap.put(fitsFile.getDate(), dateFitsList);
				}
				dateFitsList.add(fitsFile);

				String procedureTypeCd = fitsFile.getProcedureTypeCd() == null ? "Other" : fitsFile.getProcedureTypeCd();
				
				
				List<FitsFilename> typeFitsList = type2Fits.get(procedureTypeCd);
				if (typeFitsList == null) {
					typeFitsList = new ArrayList<FitsFilename>();
					type2Fits.put(procedureTypeCd, typeFitsList);
				}
				typeFitsList.add(fitsFile);					

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
			}
		}
		
		return new FitsFilesMaps(telescope2Fits, type2Fits);
	}


}
