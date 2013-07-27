package org.tmt.aps.peas.common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PointListEncoder {

	
	
	public static List<Point> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return null;
		}

		// list is encoded as x1,y1,x2,y2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Point> pointList = new ArrayList<Point>();
		for (int i=0; i<items.size()/2; i++) {
			Point point = new Point(new Integer(items.get(i*2)), new Integer(items.get((i*2)+1)));
			pointList.add(point);
		}
		return pointList;
	}
	
	public static String encodeList(List<Point> pointList) {
		
		StringBuffer buf = new StringBuffer();
		for (Point point : pointList) {
			buf.append(point.x + "," + point.y + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	public static List<Point> removePoint(List<Point> pointList, Point point) {
		
		for (Iterator<Point> it = pointList.iterator(); it.hasNext(); ) {
			Point candidate = it.next();
			if (candidate.x == point.x && candidate.y == point.y) {
				it.remove();
				break;
			}
		}
		return pointList;
	}
	
	public static List<Point> addPoint(List<Point> pointList, Point point) {
		pointList.add(point);
		return pointList;
	}
	
	
}
