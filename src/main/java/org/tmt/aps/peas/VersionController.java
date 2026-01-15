/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;
import org.tmt.aps.peas.common.MessageGenerator;

/**
 * JSF Controller for handling version number
 * @author smichaels
 */
@Named
@SessionScoped
public class VersionController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	public final static String VERSION_FILENAME = "version.properties";

	private Properties properties = null;

	@PostConstruct
	public void init() {

		try {
			logger.debug("Reading Version");
			String propertiesPath = System.getProperty("org.tmt.aps.peas.peasPropertiesPath");

			properties = new Properties();
			FileInputStream fis = new FileInputStream(propertiesPath + File.separator + VERSION_FILENAME);

			properties.load(fis);
			fis.close();
		} catch (Throwable th) {
			logger.error(MessageGenerator.generateMessage("generic.error"), th);
		}
		
	}

	/**
	 * return the version number
	 */
	public String getVersion() {
		String version = properties.getProperty("version");
		if (version == null) {
			return "Unknown";
		}
		return properties.getProperty("version");
	}

}