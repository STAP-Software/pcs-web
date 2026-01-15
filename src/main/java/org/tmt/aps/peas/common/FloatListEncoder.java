/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.logging.Logger;

/**
 * Utility functions to encode and decode lists and one and two dimensional arrays of floating point numbers to a string representation.
 * 
 * @author smichaels
 *
 */
public class FloatListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	

	/**
	 * Decodes a string representation of a list of floating point numbers to a List of Floats
	 * @param encodedList the string encoded list
	 * @return a List of decoded floating point numbers
	 */
	public static List<Float> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return null;
		}

		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.trim().split("\\s*,\\s*"));
		List<Float> numberList = new ArrayList<Float>();
		for (String item : items) {
			Float number = new Float(item.trim());
			numberList.add(number);
		}
		return numberList;
	}
	
	/**
	 * Encodes a one-dimensional array of floating point numbers to a string representation
	 * @param numberList the input array to encode
	 * @return the encoded string
	 */
	public static String encodeList(float[] numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (Float number : numberList) {
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	/**
	 * Encodes a two-dimensional array of floating point numbers to a string representation
	 * @param numberList the input array to encode
	 * @return the encoded string
	 */
	public static String encodeList(float[][] numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (float[] numberArray : numberList) {
			for (float number : numberArray) {
				buf.append(number + ",");
			}
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	/**
	 * Encodes a List of floating point numbers to a string representation
	 * @param numberList the input list to encode
	 * @return the encoded string
	 */
	public static String encodeList(List<Float> numberList) {
		
		if (numberList.isEmpty()) {
			return "";
		}
		
		StringBuffer buf = new StringBuffer();
		for (Float number : numberList) {
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	

}
