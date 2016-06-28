/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Utility functions to encode and decode lists and arrays of integer numbers to a string representation.
 * 
 * @author smichaels
 *
 */
public class IntegerListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Decodes a string representation of an integer list to a List of Integers
	 * @param encodedList string encoded list representing the list
	 * @return the decoded list as a List of Integers
	 */
	public static List<Integer> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new ArrayList<Integer>();
		}

		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Integer> numberList = new ArrayList<Integer>();
		for (String item : items) {
			Integer number = new Integer(item.trim());
			numberList.add(number);
		}
		return numberList;
	}
	
	/**
	 * Decodes a string representation of an integer list to an array of ints
	 * @param encodedList string encoded list representing the list
	 * @return the decoded array of ints
	 */
	public static int[] decodeListToArray(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new int[0];
		}
		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		int[] result = new int[items.size()];
		int i=0;
		for (String item : items) {
			Integer number = new Integer(item.trim());
			result[i++] = number;
		}
		return result;
	}
	
	/**
	 * Decodes a string representation of an integer list to an array of Integer objects
	 * @param encodedList string encoded list representing the list
	 * @return the decoded array of Integer objects
	 */
	public static Integer[] decodeListToObjectArray(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new Integer[0];
		}
		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		Integer[] result = new Integer[items.size()];
		int i=0;
		for (String item : items) {
			Integer number = new Integer(item.trim());
			result[i++] = new Integer(number);
		}
		return result;
	}
	
	/**
	 * Encodes a List of Integer objects to a string representation
	 * @param numberList the input array to encode
	 * @return the encoded string
	 */
	public static String encodeList(List<Integer> numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (Integer number : numberList) {
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	/**
	 * Encodes a one-dimensional array of integer primitives to a string representation
	 * @param numberList the input array to encode
	 * @return the encoded string
	 */
	public static String encodeList(int[] numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i<numberList.length; i++) {
			int number = numberList[i];
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	/**
	 * Encodes a one-dimensional array of Integer objects to a string representation
	 * @param numberList the input Integer array to encode
	 * @return the encoded string
	 */
	public static String encodeList(Integer[] numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i<numberList.length; i++) {
			int number = numberList[i];
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	

	
}
