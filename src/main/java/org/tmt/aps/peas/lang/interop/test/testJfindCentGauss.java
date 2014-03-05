package org.tmt.aps.peas.lang.interop.test;

import org.tmt.aps.peas.lang.interop.JfindCentGauss;
import org.tmt.aps.peas.lang.interop.RetVal;

public class testJfindCentGauss {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JfindCentGauss jfindCentGauss = new JfindCentGauss();
		RetVal  retVal = new RetVal();
		int irad = 10;
		int imargin = 2;
		int i_init = 512;
		int j_init = 100;
		int itermax = 10;
		int nspot_type = 1;
		int ngauss = 0;
		
		float[][] ccd = new float[1024][1024];
		for (int i=0; i<1024; i++) {
			for (int j=0; j<1025; j++) {
				ccd[i][j] = i*1025 + j;
			}
		}
		
		
		Object[] result = jfindCentGauss.jfindCentGauss(retVal,
				ccd, irad, imargin, i_init, j_init, itermax, nspot_type, ngauss);
		
		System.out.println("X Cent: " + result[0]);
		System.out.println("Y Cent: " + result[1]);
		System.out.println("Good or bad: " + result[3]);
		
	}

}
