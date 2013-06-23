package org.tmt.aps.peas.frame.fits;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.fits.PrimaryHDU;

public class FitsReader {
	public FitsFrame readFits(String path) throws Exception {
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
