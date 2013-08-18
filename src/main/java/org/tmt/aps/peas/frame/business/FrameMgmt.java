package org.tmt.aps.peas.frame.business;

import java.io.File;
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
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.frame.ui.FalseColorProcessor;
import org.tmt.aps.peas.procedure.business.ProcedureExecutionState;
import org.tmt.aps.peas.procedure.model.Procedure;

@Stateless
public class FrameMgmt {

	@PersistenceContext
	private EntityManager em;

	@EJB
	PeasProperties peasProperties;
	@EJB
	FrameSimulator frameSimulator;
	@EJB
	ProcedureExecutionState procedureExecutionState;

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
		saveFitsFrame(ccdFrame);

		// save the Ccd record with the fits file name
		em.persist(ccdFrame);

		associateCcdFrame(procedureCcdFrame);
	}

	// manual Ccd frame save
	// FITS file name TBD
	public void saveCcdFrame(CcdFrame ccdFrame) {
	}

	public void associateCcdFrame(ProcedureCcdFrame procedureCcdFrame) {
		// create a ProcedureCcdRecord

		// the passed CcdFrame will only have a filename
		// we need to read from the DB to get the real record

		CcdFrame ccdFrame = findCcdFrame(procedureCcdFrame.getCcdFrame().getFitsFilename());

		if (ccdFrame == null) {
			// we have to save it for the first time ourselves. This is how we avoid having to
			// populate the database with legacy values using a script, just do it as needed.
			ccdFrame = new CcdFrame();
			ccdFrame.setCreateDate(new Date());
			ccdFrame.setFitsFilename(procedureCcdFrame.getCcdFrame().getFitsFilename());
			em.persist(ccdFrame);
		}
		procedureCcdFrame.setCcdFrame(ccdFrame); // now the ccdFrame has a primary key

		// perform the association
		em.persist(procedureCcdFrame);
	}

	public ProcedureCcdFrame getProcedureCcdFrame(int frameSource, int iteration, int frameNumber) {

		if (frameSource == Constants.FRAME_SOURCE_CCD) {
			// get the frame from CCD or from file, depending on the called type

			// if we get from CCD, store into a file

		}

		CcdFrame ccdFrame = frameSimulator.getFrame(frameNumber);
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

		System.out.println("frame folder = " + frameFolder);

		// read in and parse each frame and build up
		File folder = new File(frameFolder);

		List<FitsFilename> fitsFileList = new ArrayList<FitsFilename>();

		for (File fileEntry : folder.listFiles()) {

			String filename = fileEntry.getName();
			
			if (filename.toLowerCase().endsWith(".fts")) {

				FitsFilename fitsFile = new FitsFilename(filename);

				fitsFileList.add(fitsFile);
				
				// one time only conversion - UNCOMMENT TO GENERATE PNG FILES FOR ALL FITS FILES
				//System.out.println("file: " + filename);
				//CcdFrame ccdFrame = loadFitsFrame(filename);
				//loadPng(ccdFrame);
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

				System.out.println("hdu.class = " + hdu.getClass());

				PrimaryHDU imhdu = (PrimaryHDU) hdu;
				// imhdu.info();

				Data data = imhdu.getData();

				int leng = (int) data.getTrueSize(); // VS PADDED
				System.out.println("Length=" + leng);
				System.out.println("Data=" + data);
				int[] axes = imhdu.getAxes();

				System.out.println("data.getData: " + data.getData());

				short[][] shortArray = (short[][]) data.getData();

				System.out.println(imhdu.getBitPix() + " bits per pixel");
				System.out.println("Data = " + data.getData().getClass());

				int bpix = (int) imhdu.getBitPix();

				fb.setBitPix(bpix);

				fb.setNoOfAxes(imhdu.getHeader().getIntValue("NAXIS"));

				fb.setAxes1(axes[1]);

				fb.setAxes2(axes[0]);

				fb.setResult(shortArray);

				// fb.setObsDate(imhdu.getHeader().getStringValue("DATE-OBS"));

				Header header = hdu.getHeader();

				System.out.println("header = " + header);

			}

		}
		return fb;
	}

	// FIXME: built without an example. This may not work
	public void saveFitsFrame(CcdFrame ccdFrame) throws Exception {

		Fits myFits;
		// First create a null FITS object.
		myFits = new Fits();

		// Now create three extensions.
		myFits.addHDU(HDU.create(ccdFrame.getResult()));

		java.io.FileOutputStream fo = new java.io.FileOutputStream(ccdFrame.getFitsFilename());
		BufferedDataOutputStream o = new BufferedDataOutputStream(fo);
		myFits.write(o);

	}

	public byte[] loadPng(CcdFrame ccdFrame) {

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
				byte[] falseColorPng = falseColorer.createImage(ccdFrame.getResult());

				FileUtils.writeByteArrayToFile(pngFile, falseColorPng);
				return falseColorPng;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
