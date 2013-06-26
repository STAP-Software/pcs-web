package org.tmt.aps.peas.frame.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.PrimaryHDU;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.frame.model.FitsFrame;
import org.tmt.aps.peas.frame.model.ImageFrame;
import org.tmt.aps.peas.frame.model.PcsFitsFile;

@Stateless
public class FrameMgmt {

	
	@EJB
	PeasProperties peasProperties;
	

	
	public ImageFrame getCorrectedFrame(int frameSource) {
		
		if (frameSource == Constants.FRAME_SOURCE_CCD) {
			// get the frame from CCD or from file, depending on the called type
			
			// if we get from CCD, store into a file
			
		}
		
		// get the frame from the file and return it
		
		return null;
	}
	
	public List<PcsFitsFile> findAllFitsFiles() throws Exception {
		
		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");
		
		System.out.println("frame folder = " + frameFolder);
		
		// read in and parse each frame and build up 
		File folder = new File(frameFolder);

		
		List<PcsFitsFile> fitsFileList = new ArrayList<PcsFitsFile>();
		
	    for (File fileEntry : folder.listFiles()) {
	    	
	    	String filename = fileEntry.getName();
	    	
	    	PcsFitsFile fitsFile = new PcsFitsFile(filename);

	    	fitsFileList.add(fitsFile);
	    }
	    
	    return fitsFileList;
	}

	
	public FitsFrame loadFitsFrame(String fitsFilename) throws Exception {
	
		
		String frameFolder = peasProperties.getProp("org.tmt.aps.peas.fitsRepositoryPath");
		
		String path = frameFolder + File.separator + fitsFilename;
		
		Fits fitsFile = new Fits(path);
		BasicHDU[] bhdus = fitsFile.read();
		FitsFrame fb = new FitsFrame();

		System.out.println("bhdus = " + bhdus.length);

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
				
				short[][] shortArray = (short[][])data.getData();

				System.out.println(imhdu.getBitPix() + " bits per pixel");
				System.out.println("Data = " + data.getData().getClass());

				int bpix = (int) imhdu.getBitPix();

				fb.setBitPix(bpix);

				fb.setNoOfAxes(imhdu.getHeader().getIntValue("NAXIS"));

				fb.setAxes1(axes[1]);

				fb.setAxes2(axes[0]);
				
				fb.setResult(shortArray);

				fb.setObsDate(imhdu.getHeader().getStringValue("DATE-OBS"));

				Header header = hdu.getHeader();

				System.out.println("header = " + header);

			}

		}
		return fb;
	}
	
}
