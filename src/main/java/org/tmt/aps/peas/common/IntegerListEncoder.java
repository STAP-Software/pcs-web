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

public class IntegerListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	
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
