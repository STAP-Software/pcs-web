/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;


public class FloatPoint {
	
	
    /**
     * The X coordinate of this <code>Point</code>.
     * If no X coordinate is set it will default to 0.
     */
    public float x;

    /**
     * The Y coordinate of this <code>Point</code>.
     * If no Y coordinate is set it will default to 0.
     */
    public float y;

 
    /**
     * Constructs and initializes a point at the origin
     * (0,&nbsp;0) of the coordinate space.
      */
    public FloatPoint() {
        this(0, 0);
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified <code>Point</code> object.
     */
    public FloatPoint(FloatPoint p) {
        this(p.x, p.y);
    }

    /**
     * Constructs and initializes a point at the specified
      */
    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double[] asDoubleArray() {
    	double[] array = new double[2];
    	array[0] = x;
    	array[1] = y;
    	return array;
    }

    public Point asPoint() {
    	Point point = new Point(Math.round(x), Math.round(y));
    	return point;
    }

    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public float mag() {
		return (float)Math.sqrt(x * x + y * y);
	}

	public FloatPoint subtract(FloatPoint other) {
		return new FloatPoint(this.x - other.x, this.y - other.y);
	}
	public FloatPoint add(FloatPoint other) {
		return new FloatPoint(this.x + other.x, this.y + other.y);
	}
	
	public FloatPoint quot(double other) {
		return new FloatPoint((float)(this.x / other), (float)(this.y / other));
	}
	
	public FloatPoint prod(double other) {
		return new FloatPoint((float)(this.x * other), (float)(this.y * other));
	}

	
	/**
     * Returns a string representation of this point and its location
     * in the {@code (x,y)} coordinate space. This method is
     * intended to be used only for debugging purposes, and the content
     * and format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     */
    public String toString() {
        return  x + "," + y ;
    }
    
}
