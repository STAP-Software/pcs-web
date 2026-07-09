package org.tmt.aps.peas.tools;

import java.util.Scanner;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * Stand-alone Java program that converts J2F headers to doxygen headers.
 * 
 * @author smichaels
 *
 */
public class Doxyfier {

	/**
	 * Main program entry point
	 * 
	 * @param args
	 *            two arguments are required, the directory path to the source files from and the directory path to place converted files
	 * 
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.out.println("usage: Doxyfier source-path dest-path");
		}
		String sourcePath = args[0];
		String destPath = args[1];

		File dir = new File(sourcePath);
		if (!dir.isDirectory()) {
			System.out.println("usage: Doxyfier source-path dest-path");
		}

		File[] directoryListing = dir.listFiles();
		for (File sourceFile : directoryListing) {
			// Do something with sourceFile
			if (sourceFile.isDirectory()) {
				continue;
			}
			
			// grab all lines starting with <function> and ending with </function>
			Scanner scanner = new Scanner(sourceFile);
			StringBuffer buf = new StringBuffer("<?xml version=\"1.0\"?>");
			boolean inComments = false;
			while (scanner.hasNextLine()) {
			   String line = scanner.nextLine();
			   // process the line
			   if (line.contains("<function") || inComments) {
				   buf.append(line.substring(1) + "\n");  // remove "!" and add back in newline
				   inComments = true;
			   }
			   if (line.contains("</function>")) {
				   inComments = false;
			   }
			   
			}
			scanner.close();
			
			if (buf.length() < 30) {
				continue;
			}
			System.out.println("filename = " + sourceFile.getName());
			
			// Now we parse the XML and extract the descriptions, args, etc
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream input = new ByteArrayInputStream(buf.toString().getBytes("UTF-8"));
			Document document = builder.parse(input);
			
			Element root = document.getDocumentElement();
			
			@SuppressWarnings("unused")
			String author = root.getElementsByTagName("author").item(0).getTextContent();
			@SuppressWarnings("unused")
			String shortDesc = root.getElementsByTagName("short-desc").item(0).getTextContent();
			String longDesc = root.getElementsByTagName("long-desc").item(0).getTextContent();
			
			StringBuffer headerbuf = new StringBuffer();
			
			//System.out.println("author = " + author + ", shortDesc = " + shortDesc + ", longDesc = " + longDesc);
			headerbuf.append("!> " + removeTabs(longDesc) + "\n");
			
			Node argsNode = root.getElementsByTagName("args").item(0);
			Element argsElement = (Element) argsNode;
			NodeList nodes = argsElement.getElementsByTagName("arg");
			
			
			for (int i=0; i< nodes.getLength(); i++) {
			
				Node argumentNode = nodes.item(i);
			    Element argument = (Element) argumentNode;
				
				String name = argument.getElementsByTagName("name").item(0).getTextContent();
				String argLongDesc = argument.getElementsByTagName("long-desc").item(0).getTextContent();
				String inOut = argument.getElementsByTagName("in-out").item(0).getTextContent();
				String units = argument.getElementsByTagName("units").item(0).getTextContent();
				
				StringBuffer argBuf = new StringBuffer();
				argBuf.append("!! @param[" + inOut.toLowerCase() + "] " + name);
				argBuf.append("\n!! \\parblock" + removeTabs(argLongDesc));
				
				if (units != null  && !units.isEmpty() && !units.equals("N/A")) {
					argBuf.append("units: (" + units + ")\n");
				} else {
					argBuf.append("\n");
				}
						
				argBuf.append("!! \\endparblock");
				
				headerbuf.append(argBuf + "\n");
			}
						
			System.out.println(headerbuf.toString());
			
			
			// Now we insert the doxygen header into the file.
			scanner = new Scanner(sourceFile);
			// get the module line
			boolean priorModule = true;
			StringBuffer newFileBuf = new StringBuffer();
			while (scanner.hasNextLine()) {
			   String line = scanner.nextLine();
			   // process the line
			   if (line.startsWith("MODULE") || priorModule) {
				   newFileBuf.append(line + "\n");
				  
				   if (line.startsWith("MODULE")) {
					   newFileBuf.append(headerbuf.toString() + "\n");
					   priorModule = false;
				   } 
			   } else {
				   newFileBuf.append(line + "\n");
			   }
			}
			scanner.close();
			
			File outputFile = new File(destPath, sourceFile.getName());
			
			// write newFileBuf to the new file in the dest dir
			PrintWriter out = new PrintWriter(outputFile);
			out.println(newFileBuf.toString());
			out.close();
			
			
		}

	}
	
	private static String removeTabs(String input) {
		
		String temp = input.replaceAll("[\\n]", "\n!!");

		return temp;
	}

}
