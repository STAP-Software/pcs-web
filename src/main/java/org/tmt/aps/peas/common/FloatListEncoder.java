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

public class FloatListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	

	
	
	public static List<Float> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return null;
		}

		// list is encoded as num1, num2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Float> numberList = new ArrayList<Float>();
		for (String item : items) {
			Float number = new Float(item);
			numberList.add(number);
		}
		return numberList;
	}
	
	public static String encodeList(List<Float> numberList) {
		
		StringBuffer buf = new StringBuffer();
		for (Float number : numberList) {
			buf.append(number + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	public static List<Float> removeNumber(List<Float> numberList, Float number) {
		
		numberList.remove(number);
		return numberList;
	}
	
	// TODO: do we need to retain ordering?
	public static List<Float> addNumber(List<Float> numberList, Float number) {
		numberList.add(number);
		return numberList;
	}
	
}
