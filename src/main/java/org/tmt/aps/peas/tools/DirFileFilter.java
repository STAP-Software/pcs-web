package org.tmt.aps.peas.tools;

import java.io.File;
import java.io.FileFilter;

public class DirFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		return file.isDirectory();
	}

}
