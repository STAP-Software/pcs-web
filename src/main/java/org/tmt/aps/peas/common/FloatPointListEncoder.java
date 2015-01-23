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

public class FloatPointListEncoder {

	Logger logger = Logger.getLogger(this.getClass());
	
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
	
	public static List<FloatPoint> addPoint(List<FloatPoint> pointList, FloatPoint point) {
		pointList.add(point);
		return pointList;
	}
	
	public static float[] extractXArray(List<FloatPoint> pointList) {
		
		if (pointList == null) return null;
	
		float[] result = new float[pointList.size()];
		
		for (int i=0; i<pointList.size(); i++) {
			result[i] = pointList.get(i).x;
		}
		
		return result;

	}
	
	public static float[] extractYArray(List<FloatPoint> pointList) {
		
		if (pointList == null) return null;

		float[] result = new float[pointList.size()];
		
		for (int i=0; i<pointList.size(); i++) {
			result[i] = pointList.get(i).y;
		}
		
		return result;

	}
	
	public static float[][] convertToNby2Array(List<FloatPoint> pointList) {
		int size = pointList.size();
		
		float[][] result = new float[size][2];
		for (int i=0; i<size; i++) {
			result[i][0] = pointList.get(i).x;
			result[i][1] = pointList.get(i).y;
		}
		return result;
	}
	public static List<FloatPoint> convertFromNby2Array(float[][] pointArray) {
		int size = pointArray.length;
		
		List<FloatPoint> result = new ArrayList<FloatPoint>();
		for (int i=0; i<size; i++) {
			result.add(new FloatPoint(pointArray[i][0], pointArray[i][1]));
		}
		return result;
	}
	
	public static List<FloatPoint> constructFromXandY(float[] xArray, float[] yArray) {
		
		List<FloatPoint> resultList = new ArrayList<FloatPoint>();
		
		for (int i=0; i<xArray.length; i++) {
			resultList.add(new FloatPoint(xArray[i], yArray[i]));
		}
		
		return resultList;
	}
	
    public static List<Point> roundToPoint(List<FloatPoint> fpList) {
    	
    	List<Point> pList = new ArrayList<Point>();
    	
    	for (FloatPoint fp : fpList) {
    		Point p = new Point((int)fp.x, (int)fp.y);
    		pList.add(p);
    	}
    	
    	return pList;
    }

}
