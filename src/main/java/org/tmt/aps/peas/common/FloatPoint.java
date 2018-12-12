/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;

/**
 * A floating point coordinate class.  PEAS uses an abundance of x,y pairs.  This class encapsulates a single coordinate.
 * This class should have been named FloatCoordinate.
 * 
 * @author smichaels
 * 
 */
public class FloatPoint {
	
	
    /**
     * The X coordinate of this <code>FloatPoint</code>.
     * If no X coordinate is set it will default to 0.
     */
    public float x;

    /**
     * The Y coordinate of this <code>FloatPoint</code>.
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
     * the specified <code>FloatPoint</code> object.
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

    /**
     * returns the FloatPoint as a two element double precision array.
     * @return a two element double precision array
     */
    public double[] asDoubleArray() {
    	double[] array = new double[2];
    	array[0] = x;
    	array[1] = y;
    	return array;
    }

    /**
     * returns a Point that is the rounded value of the FloatPoint
     * @return the rounded FloatPoint as an integer Point
     */
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
	
	/**
	 * @return the magnitude of the coordinate
	 */
	public float mag() {
		return (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * Subtracts a passed coordinate from this coordinate
	 * @param other coordinate to subtract
	 * @return a new FloatPoint that is the difference between this FloatPoint and the passed FloatPoint
	 */
	public FloatPoint subtract(FloatPoint other) {
		return new FloatPoint(this.x - other.x, this.y - other.y);
	}
	
	/**
	 * Adds a passed coordinate to this coordinate
	 * @param other coordinate to add
	 * @return a new FloatPoint that is the sum of this FloatPoint and the passed FloatPoint
	 */
	public FloatPoint add(FloatPoint other) {
		return new FloatPoint(this.x + other.x, this.y + other.y);
	}
	
	/**
	 * Takes the quotient of this FloatPoint and the passed FloatPoint
	 * @param other coordinate to divide by
	 * @return a new FloatPoint that is the quotient of this FloatPoint and the passed FloatPoint
	 */
	public FloatPoint quot(double other) {
		return new FloatPoint((float)(this.x / other), (float)(this.y / other));
	}
	
	/**
	 * Takes the procduct of this FloatPoint and the passed scalar
	 * @param other coordinate to multiply by
	 * @return a new FloatPoint that is the product of this FloatPoint and the passed FloatPoint
	 */
	public FloatPoint prod(double other) {
		return new FloatPoint((float)(this.x * other), (float)(this.y * other));
	}

	/**
	 * Takes the procduct of this FloatPoint and the passed FloatPoint
	 * @param other coordinate to multiply by
	 * @return a new FloatPoint that is the product of this FloatPoint and the passed FloatPoint
	 */
	public FloatPoint prod(FloatPoint other) {
		return new FloatPoint((float)(this.x * other.x), (float)(this.y * other.y));
	}

	
	/**
     * Returns a string representation of this point and its location
     * in the {@code (x,y)} coordinate space. 
     */
    public String toString() {
        return  x + "," + y ;
    }
    
}
