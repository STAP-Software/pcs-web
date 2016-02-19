package org.tmt.aps.peas.tools;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


public class VersionTool {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a z");
		
		String path = args[1];
		
		File dir = new File(path);
		FileFilter filter = new DirFileFilter();
		File[] versionDirs = dir.listFiles(filter);
		
		Arrays.sort(versionDirs, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
		    } });
		
		// if args[0] == "-l"
		// list them
		if (args[0].equals("-l")) {
			System.out.println("\n");
			for (File versionDir : versionDirs) {
				Date date = new Date(versionDir.lastModified());
				System.out.println("version: " + versionDir.getName() + "   " + sdf.format(date));
			}
			System.out.println("\n");
		}
		
		// if args[0] == "-i"
		// return an incremented number
		if (args[0].equals("-i")) {
			String latest = versionDirs[0].getName();
			String majorRelease = latest.substring(0, latest.indexOf("."));
			String minorRelease = latest.substring(latest.indexOf(".")+1);
			int minorInt = new Integer(minorRelease);
			System.out.println(majorRelease + "." + (++minorInt));
		}
		
	}

}
