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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.HDU;
import nom.tam.fits.Header;
import nom.tam.fits.PrimaryHDU;
import nom.tam.util.BufferedDataOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.business.ComputationContext;
import org.tmt.aps.peas.computation.business.ComputationLibrary;
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
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

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
	private ComputationContext computationContext;


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
			return null;
		}
	}

	public List<ProcedureCcdFrame> getFramesForProcedure(Long procedureId) {

		TypedQuery<ProcedureCcdFrame> query = em.createNamedQuery("findAllFramesForProcedure", ProcedureCcdFrame.class);
		query.setParameter("procedureId", procedureId);

		return query.getResultList();

	}

	public void saveCcdFrame(ProcedureCcdFrame procedureCcdFrame) throws Exception {
		// determine FITS file name
		FitsFilename fitsFilename = new FitsFilename(procedureCcdFrame.getProcedure().getTelescope().getTelescopeId(), procedureCcdFrame
				.getProcedure().getProcedureType().getProcedureTypeCd(), procedureCcdFrame.getProcedure().getProcedureNumber(),
				procedureCcdFrame.getProcedureIterationNumber(), procedureCcdFrame.getProcedure().getProcedureConfig().getUfsSegment(),
				procedureCcdFrame.getProcedure().getProcedureConfig().getSufsGroup(), procedureCcdFrame.getPhasingStepNumber());

		// save the frame to a FITS file
		CcdFrame ccdFrame = procedureCcdFrame.getCcdFrame();
		ccdFrame.setFitsFilename(fitsFilename.generateFileName());
		ccdFrame.setInstrumentId(procedureCcdFrame.getProcedure().getInstrument().getInstrumentId());
		saveFitsFrame(ccdFrame);

		// save the Ccd record with the fits file name
		em.persist(ccdFrame);

		associateCcdFrame(procedureCcdFrame);
	}

	// manual Ccd frame save
	// FITS file name TBD
	public void saveCcdFrame(CcdFrame ccdFrame, Long telescopeId, Long instrumentId, String procedureTypeCd, int procedureNumber) throws Exception {
		
		String newName = new FitsFilename(telescopeId, procedureTypeCd, procedureNumber, 0).generateFileName();

		// determine 'iteration' number if multiple frames of this mask taken today
		int iterationNumber = findMatchingFitsFiles(newName.substring(0, newName.length()-8) + "*").size();
		
		FitsFilename fitsFilename = new FitsFilename(telescopeId, procedureTypeCd, procedureNumber, iterationNumber);

		ccdFrame.setFitsFilename(fitsFilename.generateFileName());
		ccdFrame.setInstrumentId(instrumentId);
		saveFitsFrame(ccdFrame);

		
	}

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
				em.persist(cameraState);
			}
			
			em.persist(ccdFrame);
		}
		procedureCcdFrame.setCcdFrame(ccdFrame); // now the ccdFrame has a primary key

		// perform the association
		em.persist(procedureCcdFrame);
	}

	private CcdFrame readFrameFromCcd(double exposureTime, ProcedureConfig procedureConfig, ProcedureType procedureType, int procedureNumber, List<Rect> badPixelList, boolean removeBadPixels) throws Exception {
		//try {

			// get the frame from CCD or from file, depending on the called type
			ccdMgmt.fastWipeCcd();
			int[][] frame = ccdMgmt.getImage(exposureTime * 1000.0, true);
			
			// TODO: does this need to be done in parallel with getting the exposure?
			// get the telescope status
			telescopeMgmt.refreshStatus();
			
			if (removeBadPixels && badPixelList != null && badPixelList.size() > 0) {
				ComputationLibrary computationLibrary = computationContext.getComputationLibrary();
				frame = computationLibrary.removeBadPixels(frame, badPixelList);
			}
			
			short[][] rawFrame = new short[frame.length][frame[0].length];
			for (int i = 0; i < frame.length; i++) {
				for (int j = 0; j < frame[i].length; j++) {
					rawFrame[i][j] = (short) frame[j][i];
				}
			}

			CcdFrame ccdFrame = new CcdFrame();
			ccdFrame.setAxes1(1024);
			ccdFrame.setAxes2(1024);
			ccdFrame.setRawFrame(rawFrame);
			ccdFrame.setCreateDate(new Date());
			ccdFrame.setNoOfAxes(2);

			// create the png
			FalseColorProcessor falseColorer = new FalseColorProcessor();
			byte[] falseColorPng = falseColorer.createImage(ccdFrame.getRawFrame());
			ccdFrame.setFalseColorPng(falseColorPng);

			// save the camera state when the ccd frame was taken
			Instrument instrument = physicalModel.getInstrument();
			CameraState cameraState = new CameraState(instrument);
			ccdFrame.setCameraState(cameraState);
			ccdFrame.setInstrumentId(instrument.getInstrumentId());

			// generate filename and store into the FITS file
			saveCcdFrame(ccdFrame, procedureConfig.getTelescope().getTelescopeId(), procedureConfig.getInstrument().getInstrumentId(), 
					procedureType.getProcedureTypeCd(), procedureNumber);			
			
			return ccdFrame;
			
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	return null;
		//}

	}
	
	public ProcedureCcdFrame getProcedureCcdFrame(ProcedureConfig procedureConfig, ProcedureType procedureType, int procedureNumber, 
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

		System.out.println("ccdFrame = " + ccdFrame);
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
		
		Camera camera = physicalModel.getInstrument().getCamera();
		Telescope telescope = physicalModel.getTelescope();
		if (procedureExecutionState.getCurrentProcedure() != null) {
			ProcedureConfig procedureConfig = procedureExecutionState.getCurrentProcedure().getProcedureConfig();
			myFits.getHDU(0).getHeader().addFloatValue("INT_TIME", procedureConfig.getIntegrationTime(), "Integration Time (sec)");
			if (procedureConfig.getSufsGroup() != null) {
				myFits.getHDU(0).getHeader().addIntValue("SUFS_GRP", procedureConfig.getSufsGroup(), "SUFS Group Number");
			}
			myFits.getHDU(0).getHeader().addIntValue("PROC_NUM", procedureExecutionState.getCurrentProcedure().getProcedureNumber(), "Procedure Number");
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

	public byte[] loadPng(CcdFrame ccdFrame, boolean writeToFile) {

		try {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}


}
