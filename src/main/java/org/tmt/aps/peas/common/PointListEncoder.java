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

/**
 * Utility functions to encode and decode lists of Points from a string representation to Lists and Arrays.
 * Also includes functions to extract lists of x coordinates and lists of y coordinates.
 * Includes list manipulation functions specific to coordinates.
 * 
 * @author smichaels
 *
 */
public class PointListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Decodes a string representation of a point list to a List of Points
	 * @param encodedList the string encoded list
	 * @return a List of decoded Points
	 */
	public static List<Point> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new ArrayList<Point>();
		}

		// list is encoded as x1,y1,x2,y2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<Point> pointList = new ArrayList<Point>();
		for (int i=0; i<items.size()/2; i++) {
			Point point = new Point(new Integer(items.get(i*2).trim()), new Integer(items.get((i*2)+1).trim()));
			pointList.add(point);
		}
		return pointList;
	}
	
	/**
	 * Encodes a list of Points to a String representation
	 * @param pointList the list of points to encode
	 * @return the encoded list of points
	 */
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
	
	/**
	 * Encodes only the x coordinates of a given list of Points to a string representation of the list
	 * @param pointList the list of coordinates to encode x coordinates from
	 * @return the encoded list of x coordinates 
	 */
	public static String encodeXList(List<Point> pointList) {
		StringBuffer buf = new StringBuffer();
		if (pointList == null) return null;
		for (Point point : pointList) {
			buf.append(point.x + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();

	}
	
	/**
	 * Encodes only the y coordinates of a given list of Points to a string representation of the list
	 * @param pointList the list of coordinates to encode y coordinates from
	 * @return the encoded list of y coordinates 
	 */	
	public static String encodeYList(List<Point> pointList) {
		StringBuffer buf = new StringBuffer();
		if (pointList == null) return null;
		for (Point point : pointList) {
			buf.append(point.y + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();

	}
	
	/**
	 * Removes a point from the list.  The first point matching the passed point in x and y will be removed from the list.
	 * @param pointList the list to modify
	 * @param point the point to remove from the list
	 * @return the list with the point removed, if it was in the list
	 */
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
	
	/**
	 * Adds a point to the list
	 * @param pointList the list to modify
	 * @param point the point to add
	 * @return the modified list
	 */
	public static List<Point> addPoint(List<Point> pointList, Point point) {
		pointList.add(point);
		return pointList;
	}
	
	
}
