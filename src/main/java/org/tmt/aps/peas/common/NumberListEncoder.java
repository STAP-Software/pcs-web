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

public class NumberListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	
	public static List<Integer> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return null;
		}

		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Integer> numberList = new ArrayList<Integer>();
		for (String item : items) {
			Integer number = new Integer(item);
			numberList.add(number);
		}
		return numberList;
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
	
	public static List<Integer> removeNumber(List<Integer> numberList, Integer number) {
		
		numberList.remove(number);
		return numberList;
	}
	
	// TODO: do we need to retain ordering?
	public static List<Integer> addNumber(List<Integer> numberList, Integer number) {
		numberList.add(number);
		return numberList;
	}
	
	
}
