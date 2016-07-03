package org.tmt.aps.peas.tools;

import java.io.File;
import java.io.FileFilter;

/**
 * Directory file filter, used by {@link VersionTool}
 * @author smichaels
 *
 */
public class DirFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}

}
