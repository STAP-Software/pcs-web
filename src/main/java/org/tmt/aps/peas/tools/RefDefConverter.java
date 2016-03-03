package org.tmt.aps.peas.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefDefConverter {

	public static void main(String[] args) {
		

		BufferedReader reader = null;
		List<String> values1 = null;
		List<String> values2 = null;
		try {

			for (int segment = 0; segment < 36; segment++) {

				String segmentStr = String.format("%02d", segment + 1);

				for (int telescope = 1; telescope < 3; telescope++) {

					// open and read a file
					File file = new File(
							"/home/smichaels/git/pcs-fortran-work/pcs_data/config/K" + telescope + "_SUFS_" + segmentStr + "_SPOTS.DAT");
					
					reader = new BufferedReader(new FileReader(file));
					String text = null;

					List<String> values = new ArrayList<String>();

					while ((text = reader.readLine()) != null) {
						// read in values one at a time
						String[] strValues = text.trim().split("\\s+");
						values.add(strValues[0]);
					}

					/*
					for (int i = 0; i < 1809 * 2; i += 2) {
						System.out.print(values.get(i) + "," + values.get(i + 1) + ", ");

						if ((i - 10) % 12 == 0) {
							System.out.print(" ' ||\n'");
						}
					}
					*/
					reader.close();
					
					
					if (segment == 0) {
						values1 = values;
					} else {
						values2 = values;
						boolean equals = true;
						for (int i=0; i<36; i++) {
							
							System.out.println("i = " + i + ", values = " + values2.get(i) + " : " + values1.get(i));
							
							if(!values2.get(i).equals(values1.get(i))) {
								equals = false;
							}
						}
						System.out.println("segment = " + (segment+1) + ", K1 vs K2 = " + equals);
						
					}
				}
				
				
				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

	}

}
