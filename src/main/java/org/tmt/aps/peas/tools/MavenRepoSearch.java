package org.tmt.aps.peas.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Stand-alone Java program that searches a Maven repository for versions of an artifact.
 * 
 * @author smichaels
 *
 */
public class MavenRepoSearch {

	/**
	 * Main program entry point
	 * 
	 * @param args
	 *            two elements are required, the first is the switch and the second is the directory path to search switch values are: -l
	 *            lists all versions found -v returns the current version number -i returns the next version number available
	 * 
	 */
	public static void main(String[] args) throws Exception {
		// read EdgeData

		File file = new File("/home/smichaels/Desktop/EdgeData"); // 
		FileReader fr = new FileReader(file); // reads the file
		BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
		StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
		String line;
		int count = 0;
		while ((line = br.readLine()) != null) {
			
			String[] elements = line.trim().split("\\s+");
			
			if (elements[0].equals("")) continue;
			
			if (count++ % 2 == 0) {
						
				sb.append("{subimage: " + elements[0] + ", posX: " + elements[1] + ", posY: " + elements[2]);
			
			} else {
			
				sb.append(", offsetX: " + elements[1] + ", offsetY: " + elements[2] + "}," );
				System.out.println(sb.toString());
				sb = new StringBuffer();
			
			}

			//sbX.append(String.format("%.4f", valX) + ", "); // appends line to string buffer
			//sbY.append(String.format("%.4f", valY) + ", "); // appends line to string buffer
			
			
			
		}
		fr.close(); // closes the stream and release the resources
		//System.out.println("Contents of File: ");
		//System.out.println(sbX.toString()); // returns a string that textually represents the object
		//System.out.println(sbY.toString()); // returns a string that textually represents the object

	}
}
