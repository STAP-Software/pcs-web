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
 * Utility functions to encode and decode lists of FloatPoints from a string representation to Lists and Arrays.
 * Also includes functions to encode and decode lists of x coordinates only and lists of y coordinates only.
 * Includes list manipulation functions specific to floating point coordinates.
 * Includes functions to extract x and y coordinates from lists and arrays of FloatPoints
 * 
 * @author smichaels
 *
 */
public class FloatPointListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * Decodes a string representation of a FloatPoint list to a List of FloatPoints
	 * @param encodedList the string encoded list
	 * @return a List of decoded FloatPoints
	 */
	public static List<FloatPoint> decodeList(String encodedList) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new ArrayList<FloatPoint>();
		}

		// list is encoded as x1,y1,x2,y2, etc
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<FloatPoint> pointList = new ArrayList<FloatPoint>();
		for (int i=0; i<items.size()/2; i++) {
			FloatPoint point = new FloatPoint(new Float(items.get(i*2).trim()), new Float(items.get((i*2)+1).trim()));
			pointList.add(point);
		}
		return pointList;
	}

	/**
	 * Encodes a list of FloatPoints to a String representation
	 * @param pointList the list of float points to encode
	 * @return the encoded list of float points
	 */
	public static String encodeList(List<FloatPoint> pointList) {
		
		StringBuffer buf = new StringBuffer();
		for (FloatPoint point : pointList) {
			buf.append(point.x + "," + point.y + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	/**
	 * Encodes the x-coordinates of an array of FloatPoints to a String representation
	 * @param pointArray the array of float points to encode x-coordinates from
	 * @return the encoded list of float points
	 */
	public static String encodeXList(FloatPoint[] pointArray) {
		return encodeXList(Arrays.asList(pointArray));
	}
	
	/**
	 * Encodes the x-coordinates of a list of FloatPoints to a String representation
	 * @param pointList the list of float points to encode x-coordinates from
	 * @return the encoded list of float points
	 */
	public static String encodeXList(List<FloatPoint> pointList) {
		StringBuffer buf = new StringBuffer();
		if (pointList == null) return null;
		for (FloatPoint point : pointList) {
			buf.append(point.x + ",");
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();

	}
	
	/**
	 * Encodes the y-coordinates of an array of FloatPoints to a String representation
	 * @param pointArray the array of float points to encode y-coordinates from
	 * @return the encoded list of float points
	 */
	public static String encodeYList(FloatPoint[] pointArray) {
		return encodeYList(Arrays.asList(pointArray));
	}
	
	/**
	 * Encodes the y-coordinates of a list of FloatPoints to a String representation
	 * @param pointList the list of float points to encode y-coordinates from
	 * @return the encoded list of float points
	 */
	public static String encodeYList(List<FloatPoint> pointList) {
		StringBuffer buf = new StringBuffer();
		if (pointList == null) return null;
		for (FloatPoint point : pointList) {
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
	public static List<FloatPoint> removePoint(List<FloatPoint> pointList, FloatPoint point) {
		
		for (Iterator<FloatPoint> it = pointList.iterator(); it.hasNext(); ) {
			FloatPoint candidate = it.next();
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
	public static List<FloatPoint> addPoint(List<FloatPoint> pointList, FloatPoint point) {
		pointList.add(point);
		return pointList;
	}
	
	/**
	 * Multiplies all points in an array by a factor
	 * @param pointList the array to multiply to
	 * @param factor the factor to multiply the array by
	 * @return a list of FloatPoints equal to the array of input points multiplied by factor
	 */
	public static List<FloatPoint> multiplyPoints(FloatPoint[] pointList, float factor) {
		return multiplyPoints(Arrays.asList(pointList), factor);
	}
	
	/**
	 * Multiplies all points in a list by a factor
	 * @param pointList the list to multiply to
	 * @param factor the factor to multiply the array by
	 * @return a list of FloatPoints equal to the array of input points multiplied by factor
	 */
	public static List<FloatPoint> multiplyPoints(List<FloatPoint> pointList, float factor) {
		
		List<FloatPoint> newList = new ArrayList<FloatPoint>();
		for (FloatPoint floatPoint : pointList ) {
			newList.add(floatPoint.prod(factor));
		}
		return newList;
	}
	
	/**
	 * Multiplies all points in a list by a factor
	 * @param pointList the list to multiply to
	 * @param factor the factor to multiply the array by
	 * @return a list of FloatPoints equal to the array of input points multiplied by factor
	 */
	public static List<FloatPoint> multiplyIntPoints(List<Point> pointList, float factor) {
		
		List<FloatPoint> newList = new ArrayList<FloatPoint>();
		for (Point point : pointList ) {
			newList.add(point.prod(factor));
		}
		return newList;
	}
	

	
	/**
	 * Extract the x-coordinates from a list of points
	 * @param pointList the list to extract from
	 * @return an array of float containing the extracted x-coordinates
	 */
	public static float[] extractXArray(List<FloatPoint> pointList) {
		
		if (pointList == null) return null;
	
		float[] result = new float[pointList.size()];
		
		for (int i=0; i<pointList.size(); i++) {
			result[i] = pointList.get(i).x;
		}
		
		return result;

	}
	
	/**
	 * Extract the y-coordinates from a list of points
	 * @param pointList the list to extract from
	 * @return an array of float containing the extracted y-coordinates
	 */
	public static float[] extractYArray(List<FloatPoint> pointList) {
		
		if (pointList == null) return null;

		float[] result = new float[pointList.size()];
		
		for (int i=0; i<pointList.size(); i++) {
			result[i] = pointList.get(i).y;
		}
		
		return result;

	}
	
	/**
	 * Converts a list of FloatPoints into a 2-dimensional array where the second dimension is of length 2, which contains the x and y coordinates respectively.
	 * @param pointList the list to extract from
	 * @return a two dimensional array where the first dimension is the coordinate, the second is of length 2, containing x and y coordinates respectively.
	 */
	public static float[][] convertToNby2Array(List<FloatPoint> pointList) {
		int size = pointList.size();
		
		float[][] result = new float[size][2];
		for (int i=0; i<size; i++) {
			result[i][0] = pointList.get(i).x;
			result[i][1] = pointList.get(i).y;
		}
		return result;
	}
	
	/**
	 * Converts a 2-dimensional array where the second dimension is of length 2, which contains the x and y coordinates respectively to a list of FloatPoint coordinates
	 * @param pointArray the 2-dimensional array to convert
	 * @return a list of FloatPoints
	 * @see org.tmt.aps.peas.common.FloatPointListEncoder#convertToNby2Array(List)
	 */
	public static List<FloatPoint> convertFromNby2Array(float[][] pointArray) {
		int size = pointArray.length;
		
		List<FloatPoint> result = new ArrayList<FloatPoint>();
		for (int i=0; i<size; i++) {
			result.add(new FloatPoint(pointArray[i][0], pointArray[i][1]));
		}
		return result;
	}
	
	/**
	 * Constructs a list of FloatPoint coordinates from an array of x-coordinates and an array of y-coordinates.
	 * @param xArray the array of x-coordinates
	 * @param yArray the array of y-coordinates
	 * @return a list of FloatPoint coordinate pairs.
	 */
	public static List<FloatPoint> constructFromXandY(float[] xArray, float[] yArray) {
		
		List<FloatPoint> resultList = new ArrayList<FloatPoint>();
		
		for (int i=0; i<xArray.length; i++) {
			resultList.add(new FloatPoint(xArray[i], yArray[i]));
		}
		
		return resultList;
	}
	
	/**
	 * Constructs a list of FloatPoint coordinates from a list of x-coordinates and list of y-coordinates.
	 * @param xList the list of x-coordinates
	 * @param yList the list of y-coordinates
	 * @return a list of FloatPoint coordinate pairs.
	 */
	public static List<FloatPoint> constructFromXandY(List<Float> xList, List<Float> yList) {
		
		List<FloatPoint> resultList = new ArrayList<FloatPoint>();
		
		for (int i=0; i<xList.size(); i++) {
			resultList.add(new FloatPoint(xList.get(i), yList.get(i)));
		}
		
		return resultList;
	}
	
	/**
	 * Constructs a list of integer Point coordinates from a list of FloatPoint coordinates by rounding each x and y coordinate to the nearest integer.
	 * @param fpList the list of FloatPoint coordinates to convert
	 * @return a list of integer Point coordinate pairs.
	 */
    public static List<Point> roundToPoint(List<FloatPoint> fpList) {
    	
    	List<Point> pList = new ArrayList<Point>();
    	
    	for (FloatPoint fp : fpList) {
    		Point p = new Point((int)fp.x, (int)fp.y);
    		pList.add(p);
    	}
    	
    	return pList;
    }

    /**
     * Extracts x-coordinates from 2 dimensional FloatPoint coordinate array
     * @param input 2-dimensional FloatPoint array to extract from
     * @return a 2-dimensional float array containing the x-coordinates
     */
    public static float[][] extractXfrom2dFloatPoint(FloatPoint[][] input) {
    	float[][] result = new float[input.length][input[0].length];
    	for(int i=0; i<input.length; i++) {
    		for (int j=0; j<input[i].length; j++) {
    			result[i][j] = input[i][j].x;
    		}
    	}
    	return result;
    }

    /**
     * Extracts y-coordinates from 2 dimensional FloatPoint coordinate array
     * @param input 2-dimensional FloatPoint array to extract from
     * @return a 2-dimensional float array containing the y-coordinates
     */
    public static float[][] extractYfrom2dFloatPoint(FloatPoint[][] input) {
    	float[][] result = new float[input.length][input[0].length];
    	for(int i=0; i<input.length; i++) {
    		for (int j=0; j<input[i].length; j++) {
    			result[i][j] = input[i][j].y;
    		}
    	}
    	return result;
    }

}
