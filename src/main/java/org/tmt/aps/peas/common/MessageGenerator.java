package org.tmt.aps.peas.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.tmt.aps.peas.lang.interop.RetVal;

/**
 * Utility class to generate messsages using the resource bundle 'messages.properties'
 * @author smichaels
 *
 */
public class MessageGenerator {

	/**
	 * Generates a message from key: key
	 * @param key the key to the message bundle text
	 * @return the generated message string
	 */
	public static String generateMessage(String key) {
		String pattern = ResourceBundle.getBundle("messages").getString(key);
		Object[] args = new Object[0];
		return MessageFormat.format(pattern, args);
	}
	
	/**
	 * Generates a message from key: key, and variable number of Object values.  Values are substituted into the message bundle key text parameter list.
	 * @param key the key to the message bundle text
	 * @param vals the values to substitute into the message bundle key text: e.g. '{0}', '{1}' etc
	 * @return the generated message string
	 */	
	public static String generateMessage(String key, Object ... vals) {
		
		String pattern = ResourceBundle.getBundle("messages").getString(key);
		return MessageFormat.format(pattern, vals);	
	}
	
	/**
	 * Generates a message from key: key, and a Point value
	 * @param key the key to the message bundle text
	 * @param p the point to use as values to substitute into the message bundle key text, x = {0}, y = {1}
	 * @return the generated message string
	 */
	public static String generateMessage(String key, Point p) {
		return generateMessage(key, p.x, p.y);
	}
	
	/**
	 * Generates a message from key: key, and a FloatPoint value
	 * @param key the key to the message bundle text
	 * @param fp the float point to use as values to substitute into the message bundle key text, x = {0}, y = {1}
	 * @return the generated message string
	 */
	public static String generateMessage(String key, FloatPoint fp) {
		return generateMessage(key, fp.x, fp.y);
	}
	
	/**
	 * Generates PCS Camera error messages from Fortran error codes, in resource bundle: 'errorCodes.properties'
	 * @param retVal returned value from Fortran call
	 * @return the generated message string
	 */
	public static String generateErrorMessage(RetVal retVal) {
		String key = "E" + String.format("%05d", retVal.getCode());
		String pattern = ResourceBundle.getBundle("errorCodes").getString(key);
		
		
		Double[] args = new Double[10];
		args[0] = retVal.getArg0();
		args[1] = retVal.getArg1();
		args[2] = retVal.getArg2();
		args[3] = retVal.getArg3();
		args[4] = retVal.getArg4();
		args[5] = retVal.getArg5();
		args[6] = retVal.getArg6();
		args[7] = retVal.getArg7();
		args[8] = retVal.getArg8();
		args[9] = retVal.getArg9();
				
		String message = MessageFormat.format(pattern, (Object[])args);
		
		return message;

	}		
	
}
