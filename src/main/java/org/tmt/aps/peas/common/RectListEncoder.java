/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class RectListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	public static List<Rect> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new ArrayList<Rect>();
		}

		// list is encoded as x1,y1,x2,y2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Rect> rectList = new ArrayList<Rect>();
		for (int i=0; i<items.size()/4; i++) {
			Point p1 = new Point(new Integer(items.get(i*4).trim()), new Integer(items.get((i*4)+1).trim()));
			Point p2 = new Point(new Integer(items.get((i*4)+2).trim()), new Integer(items.get((i*4)+3).trim()));
			rectList.add(new Rect(p1, p2));
		}
		return rectList;
	}
	
	public static String encodeList(List<Rect> rectList) {
		
		StringBuffer buf = new StringBuffer();
		for (Rect rect : rectList) {
			buf.append(rect.p1.x + "," + rect.p1.y + ",");
			buf.append(rect.p2.x + "," + rect.p2.y + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	public static List<Rect> removeRect(List<Rect> rectList, Rect rect) {
		
		for (Iterator<Rect> it = rectList.iterator(); it.hasNext(); ) {
			Rect candidate = it.next();
			if (candidate.p1.x == rect.p1.x && candidate.p1.y == rect.p1.y && candidate.p2.x == rect.p2.x && candidate.p2.y == rect.p2.y
					) {
				it.remove();
				break;
			}
		}
		return rectList;
	}
	
	public static List<Rect> addRect(List<Rect> rectList, Rect rect) {
		rectList.add(rect);
		return rectList;
	}
	
	
}
