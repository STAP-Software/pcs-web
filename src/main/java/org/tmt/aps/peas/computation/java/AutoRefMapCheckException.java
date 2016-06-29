package org.tmt.aps.peas.computation.java;

import org.tmt.aps.peas.common.MessageGenerator;

/**
 * Exception class thrown when the most recent reference beam centroid map is checked for suitability to be used in a procedure.  
 * This check is performed in {@link org.tmt.aps.peas.computation.java.JavaComputations#autoRefMapCheck(org.tmt.aps.peas.config.model.AutoRefMapConfig, org.tmt.aps.peas.common.Point, org.tmt.aps.peas.common.Point, float, int, java.util.Date, org.tmt.aps.peas.refBeamMap.model.RefBeamMap)}.  The exception contains a message bundle key and arguments for output to the logs 
 * and potentially to the user in order to make decisions whether to take a new reference beam centroid map or not. 
 * 
 * @author smichaels
 *
 */
public class AutoRefMapCheckException extends Exception {

	/**
	 * Constructor using key and two arguments
	 * @param key the message bundle key
	 * @param arg1 the message text argument <code>{0}</code>
	 * @param arg2 the message text argument <code>{1}</code>
	 */
	public AutoRefMapCheckException(String key, Object arg1, Object arg2) {
		this.key = key;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	/**
	 * Constructor using key and one argument
	 * @param key the message bundle key
	 * @param arg1 the message text argument <code>{0}</code>
	 */
	public AutoRefMapCheckException(String key, Object arg1) {
		this.key = key;
		this.arg1 = arg1;
		this.arg2 = null;
	}
	private String key;
	private Object arg1;
	private Object arg2;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public Object getArg1() {
		return arg1;
	}
	public void setArg1(Object arg1) {
		this.arg1 = arg1;
	}
	public Object getArg2() {
		return arg2;
	}
	public void setArg2(Object arg2) {
		this.arg2 = arg2;
	}
	
	/**
	 * @return generated text using message bundle
	 */
	public String getText() {
		if (arg2 == null) {
			return MessageGenerator.generateMessage(key, arg1);
			
		} else {
			return MessageGenerator.generateMessage(key, arg1, arg2);
		}
	}
	
}
