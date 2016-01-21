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
import java.util.List;

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
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FalseColorProcessor;
import org.tmt.aps.peas.instrument.business.CameraStateMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Camera;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.HDU;
import nom.tam.fits.Header;
import nom.tam.fits.PrimaryHDU;
import nom.tam.util.BufferedDataOutputStream;

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


	public CcdFrame getCcdFrame(String fitsFilename) throws Exception {

		CcdFrame ccdFrame = loadFitsFrame(fitsFilename);
		return ccdFrame;
	}

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

	public List<ProcedureCcdFrame> getFramesForProcedure(Long procedureId) {

		TypedQuery<ProcedureCcdFrame> query = em.createNamedQuery("findAllFramesForProcedure", ProcedureCcdFrame.class);
		query.setParameter("procedureId", procedureId);

		return query.getResultList();

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void saveCcdFrame(ProcedureCcdFrame procedureCcdFrame) throws Exception {
		// determine FITS file name
		FitsFilename fitsFilename = new FitsFilename(
				procedureCcdFrame.getProcedure().getTelescope().getTelescopeId(), 
				procedureCcdFrame.getProcedure().getProcedureType().getProcedureTypeCd(), 
				procedureCcdFrame.getProcedure().getProcedureNumber(),
				procedureCcdFrame.getProcedureIterationNumber(), 
				procedureCcdFrame.getProcedure().getProcedureConfigSet().getProcedureConfig().getUfsSegment(),
				procedureCcdFrame.getProcedure().getProcedureConfigSet().getProcedureConfig().getSufsGroup(), 
				procedureCcdFrame.getPhasingStepNumber());

		// save the frame to a FITS file
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
		ccdFrame.setFitsFilename(fitsFilename.generateFileName());
		ccdFrame.setInstrumentId(procedureCcdFrame.getProcedure().getInstrument().getInstrumentId());
		saveFitsFrame(ccdFrame);

		// save the Ccd record with the fits file name
		//logger.info(MessageGenerator.generateMessage("record.create", "ccdFrame"));
		//em.persist(ccdFrame);

		//associateCcdFrame(procedureCcdFrame);
		
		// create the png
		byte[] falseColorPng = loadPng(ccdFrame, true);
		ccdFrame.setFalseColorPng(falseColorPng);

	}

	// manual Ccd frame save
	// FITS file name TBD
	public void saveCcdFrame(CcdFrame ccdFrame, Long telescopeId, Long instrumentId, String procedureTypeCd, String procedureNumber) throws Exception {
		
		String newName = new FitsFilename(telescopeId, procedureTypeCd, procedureNumber, 0).generateFileName();

		
		// determine 'iteration' number if multiple frames of this mask taken today
		int iterationNumber = findMatchingFitsFiles(newName.substring(0, newName.length()-8) + "*").size();
		
		FitsFilename fitsFilename = new FitsFilename(telescopeId, procedureTypeCd, procedureNumber, iterationNumber);

		ccdFrame.setFitsFilename(fitsFilename.generateFileName());
		ccdFrame.setInstrumentId(instrumentId);
		saveFitsFrame(ccdFrame);

		// create the png
		byte[] falseColorPng = loadPng(ccdFrame, true);
		ccdFrame.setFalseColorPng(falseColorPng);
		
	}

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
			em.merge(procedureCcdFrame.getCentroidMap());  // FIXME: attach the detached object 
		}
		
		// perform the association
		logger.info(MessageGenerator.generateMessage("record.create", "procedureCcdFrame"));
		em.persist(procedureCcdFrame);
	}

	private CcdFrame readFrameFromCcd(double exposureTime, ProcedureConfig procedureConfig, ProcedureType procedureType, String procedureNumber, List<Rect> badPixelList, boolean removeBadPixels) throws Exception {
		
		
		// if this is using a simulator for ccdMgmt, lets get a real frame for use depending on procedureType
		boolean ccdSimulator = !extInfConfigState.getExtInfConnectConfig().isCameraEnabled();
		
		// get the frame from CCD or from file, depending on the called type
		ccdMgmt.fastWipeCcd();
		
		// FIXME: do not use instrument interface and wait for a sec between shutter close and read.
		//int[][] frame = ccdMgmt.getImage(exposureTime * 1000.0, true);
		// TODO: write a JIRA bug that this was a workaround for
		
		cameraMgmt.commandCcdShutterExposure((int)(exposureTime * 1000.0));
		
		Thread.sleep(1000);
		
		int[][] frame = ccdMgmt.getImage();
		
		CcdFrame ccdFrame = null;
		
		if (ccdSimulator) {
			// here we make a better frame than the external package simulator can
			// FIXME: determine if this should be put in the simulator.  Will require a change in app packaging.
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
			
			// simulate camera state too
			Instrument instrument = physicalModel.getInstrument();
			CameraState cameraState = new CameraState(instrument);
			//cameraState.setCcdTemp(44.4f);
			//cameraState.setSteeringMirrorX(234);
			//cameraState.setSteeringMirrorY(2);
			//cameraState.setTiltPlateX(35);
			//cameraState.setTiltPlateY(-7);
			ccdFrame.setCameraState(cameraState);
			ccdFrame.setInstrumentId(instrument.getInstrumentId());

			telescopeMgmt.refreshStatus();
			
			// store telescope information with frame when it is taken
			Telescope telescope = physicalModel.getTelescope();
			ccdFrame.setAvgMirrorTemp((float)telescope.getMirrorTemp());
			ccdFrame.setSecondaryAct1((float)telescope.getM2Position()[0]);
			ccdFrame.setSecondaryAct2((float)telescope.getM2Position()[1]);
			ccdFrame.setSecondaryAct3((float)telescope.getM2Position()[2]);
			ccdFrame.setTelescopeAz(telescope.getTelPosition().x);
			ccdFrame.setTelescopeEl(telescope.getTelPosition().y);


		} else {
		
			// TODO: does this need to be done in parallel with getting the exposure?
			// get the telescope status
			telescopeMgmt.refreshStatus();
			
			int[][] swapFrame = new int[frame.length][frame[0].length];
			for (int i = 0; i < frame.length; i++) {
				for (int j = 0; j < frame[i].length; j++) {
					swapFrame[i][j] = frame[j][i];
				}
			}
			
			
			if (removeBadPixels && badPixelList != null && badPixelList.size() > 0) {
				//removeBadPixels works on "swaped" frame, X is columns, Y is rows.
				swapFrame = computationLibrary.removeBadPixels(swapFrame, badPixelList);
			}
			
			short[][] rawFrame = new short[frame.length][frame[0].length];
			for (int i = 0; i < frame.length; i++) {
				for (int j = 0; j < frame[i].length; j++) {
					rawFrame[i][j] = (short) swapFrame[i][j];
				}
			}

			ccdFrame = new CcdFrame();
			ccdFrame.setAxes1(1024);
			ccdFrame.setAxes2(1024);
			ccdFrame.setRawFrame(rawFrame);
			ccdFrame.setCreateDate(new Date());
			ccdFrame.setNoOfAxes(2);


			// save the camera state when the ccd frame was taken
			Instrument instrument = physicalModel.getInstrument();
			CameraState cameraState = new CameraState(instrument);
			ccdFrame.setCameraState(cameraState);
			ccdFrame.setInstrumentId(instrument.getInstrumentId());
			Telescope telescope = physicalModel.getTelescope();

			// store telescope information with frame when it is taken
			ccdFrame.setAvgMirrorTemp((float)telescope.getMirrorTemp());
			ccdFrame.setSecondaryAct1((float)telescope.getM2Position()[0]);
			ccdFrame.setSecondaryAct2((float)telescope.getM2Position()[1]);
			ccdFrame.setSecondaryAct3((float)telescope.getM2Position()[2]);
			ccdFrame.setTelescopeAz(telescope.getTelPosition().x);
			ccdFrame.setTelescopeEl(telescope.getTelPosition().y);
			
		}

		return ccdFrame;
	}
	
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
		procedureCcdFrame.setProcedureIterationNumber(iteration);
		if (procedureType.isPhasing()) {
			procedureCcdFrame.setProcedureIterationNumber(1); // FIXME: Normal Phasing implementation will require this be generalized
			procedureCcdFrame.setPhasingStepNumber(iteration+1);
		}
		
		if (procedureConfig.getFrameSource() == Constants.FRAME_SOURCE_CCD) {
			// generate filename and store into the FITS file
			saveCcdFrame(procedureCcdFrame);			
		}


		// add it to the procedure
		procedure.addProcedureCcdFrame(procedureCcdFrame);

		return procedureCcdFrame;
	}

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

				fitsFileList.add(fitsFile);

				// one time only conversion - UNCOMMENT TO GENERATE PNG FILES FOR ALL FITS FILES
				// logger.info("file: " + filename);
				// CcdFrame ccdFrame = loadFitsFrame(filename);
				// loadPng(ccdFrame, true);
			}
		}

		return fitsFileList;
	}

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

	public CcdFrame loadFitsFrame(InputStream is, String filename) throws Exception {
		Fits fitsFile = new Fits(is);
		return loadFitsFrame(fitsFile, filename);
	}

	public CcdFrame loadFitsFrame(String fitsFilename) throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		String path = frameFolder + File.separator + fitsFilename;
		Fits fitsFile = new Fits(path);

		return loadFitsFrame(fitsFile, fitsFilename);
	}

	public CcdFrame loadFitsFrame(Fits fitsFile, String fitsFilename) throws Exception {

		BasicHDU[] bhdus = fitsFile.read();
		CcdFrame fb = new CcdFrame();
		fb.setFitsFilename(fitsFilename);

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

				fb.setBitPix(bpix);

				fb.setNoOfAxes(imhdu.getHeader().getIntValue("NAXIS"));

				fb.setAxes1(axes[1]);

				fb.setAxes2(axes[0]);

				short[][] rawFrame = new short[shortArray[0].length][shortArray.length];

				for (int i = 0; i < shortArray[0].length; i++) {
					for (int j = 0; j < shortArray.length; j++) {
						rawFrame[i][j] = shortArray[j][i];
					}
				}

				fb.setRawFrame(rawFrame);

				// fb.setObsDate(imhdu.getHeader().getStringValue("DATE-OBS"));

				Header header = hdu.getHeader();

				logger.debug("header = " + header);
			}

		}
		return fb;
	}

	public void saveFitsFrame(CcdFrame ccdFrame) throws Exception {

		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");

		logger.debug("ccdFrame = " + ccdFrame);
		String path = frameFolder + File.separator + ccdFrame.getFitsFilename();

		// First create a null FITS object.
		Fits myFits = new Fits();

		// Now create three extensions.
		// reverse the frame to match legacy frames
		short[][] reversedFrame = new short[ccdFrame.getRawFrame().length][ccdFrame.getRawFrame()[0].length];
		for (int i = 0; i < ccdFrame.getRawFrame().length; i++) {
			for (int j = 0; j < ccdFrame.getRawFrame()[i].length; j++) {
				reversedFrame[i][j] = ccdFrame.getRawFrame()[j][i];
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
		if (procedureExecutionState.getCurrentProcedure() != null) {
			ProcedureConfig procedureConfig = procedureExecutionState.getCurrentProcedure().getProcedureConfigSet().getProcedureConfig();
			myFits.getHDU(0).getHeader().addFloatValue("INT_TIME", procedureConfig.getIntegrationTime(), "Integration Time (sec)");
			if (procedureConfig.getSufsGroup() != null) {
				myFits.getHDU(0).getHeader().addIntValue("SUFS_GRP", procedureConfig.getSufsGroup(), "SUFS Group Number");
			}
			myFits.getHDU(0).getHeader().addStringValue("PROC_NUM", procedureExecutionState.getCurrentProcedure().getProcedureNumber(), "Procedure Number");
		}
		
		myFits.getHDU(0).getHeader().addStringValue("FILTER", camera.getFilterWheel().getSelectedFilter().getFilterName(), "Filter Name");
		myFits.getHDU(0).getHeader().addStringValue("MASK", camera.getPupilWheel().getSelectedPupilMask().getMaskName(), "Mask Name");
		myFits.getHDU(0).getHeader().addStringValue("INSTRUME", physicalModel.getInstrument().getInstrumentName(), "Instrument Name");
		myFits.getHDU(0).getHeader().addStringValue("TELESCOP", telescope.getTelescopeName(), "Telescope Name");
		myFits.getHDU(0).getHeader().addFloatValue("AZ", telescope.getTelPosition().x, "Telescope Az");
		myFits.getHDU(0).getHeader().addFloatValue("EL", telescope.getTelPosition().y, "Telescope El");
		

		
		java.io.FileOutputStream fo = new java.io.FileOutputStream(path);
		BufferedDataOutputStream o = new BufferedDataOutputStream(fo);
		myFits.write(o);
		

	}

	public byte[] loadPng(CcdFrame ccdFrame, boolean writeToFile) throws Exception {

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


}
