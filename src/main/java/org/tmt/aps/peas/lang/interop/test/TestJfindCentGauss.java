package org.tmt.aps.peas.lang.interop.test;

import java.io.BufferedReader;
import java.io.FileReader;

import org.tmt.aps.peas.lang.interop.JfindCentGauss;
import org.tmt.aps.peas.lang.interop.RetVal;

public class TestJfindCentGauss {

	public static void main(String[] args) {
		

		JfindCentGauss jfindCentGauss = new JfindCentGauss();
		RetVal retVal = new RetVal();

		// pass in filename, irad, imargin, i_init, j_init, itermax, nspot_type and ngauss

		if (args.length != 8) {
			System.out.println("usage: java org.tmt.aps.peas.lang.interop.test.TestJfindCentGauss filename, irad, imargin, i_init, j_init, itermax, nspot_type, ngauss");
			System.exit(1);
		}

		String filename = args[0];
		float[][] ccd = new float[1024][1024];

		int irad = Integer.valueOf(args[1]);
		int imargin = Integer.valueOf(args[2]);
		int i_init = Integer.valueOf(args[3]);
		int j_init = Integer.valueOf(args[4]);
		int itermax = Integer.valueOf(args[5]);
		int nspot_type = Integer.valueOf(args[6]);
		int ngauss = Integer.valueOf(args[7]);

		FileReader fr = null;
		BufferedReader reader = null;
		try {
			fr = new FileReader(filename);
			reader = new BufferedReader(fr);
			String line = null;
			for (int i = 0; i < 1024; i++) {
				for (int j = 0; j < 1024; j++) {
					line = reader.readLine();
					if (line == null) {
						throw new Exception("Not enough 1024x1024 lines in file");
					}
					ccd[i][j] = Float.valueOf(line);
				}
			}

		} catch (Exception e) {
			System.out.println("" + e);
		} finally {
			try {
			reader.close();
			fr.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		Object[] result = jfindCentGauss.jfindCentGauss(retVal, ccd, irad, imargin, i_init, j_init, itermax, nspot_type, ngauss);

		System.out.println("X Cent: " + result[0]);
		System.out.println("Y Cent: " + result[1]);
	}

}
