/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;

@Singleton
@Startup
@Lock(LockType.READ)
public class PeasProperties {

	Logger logger = Logger.getLogger(this.getClass());

	public final static String PROPERTIES_FILENAME = "peas.properties";

	private Properties properties = null;

	@PostConstruct
	@Schedule(minute = "*/1", hour = "*", persistent = false)
	@Lock(LockType.WRITE)
	public void init() {

		try {
			logger.debug("Updating PeasProperties");
			String propertiesPath = System.getProperty("org.tmt.aps.peas.peasPropertiesPath");

			properties = new Properties();

			FileInputStream fis = new FileInputStream(propertiesPath + File.separator + PROPERTIES_FILENAME);

			properties.load(fis);
			fis.close();
		} catch (Throwable th) {
			logger.error(MessageGenerator.generateMessage("generic.error"), th);
		}
	}

	/**
	 * Static method will read from the peas.properties file of the current server context.
	 * @param prop String property to look up
	 */
	public String getProp(String prop) throws Exception {
		String propValue = properties.getProperty(prop);
		
		StringBuffer value = new StringBuffer(propValue);

		// 1. search for a '${' for a possible environment variable
		int posStart = -1;
		int posEnd = -1;
		while ((posStart = value.indexOf("${")) != -1) {
			// 2. search for the closing '}'
			posEnd = value.indexOf("}", posStart + 2);

			// 3. evaluate the environment var
			String envVar = value.substring(posStart + 2, posEnd);
			String envValue = System.getenv(envVar);

			// 4. substitute values
			value.replace(posStart, posEnd + 1, envValue);
		}

		return value.toString();
	}

}