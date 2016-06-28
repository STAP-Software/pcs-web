/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;

/**
 * An integer point coordinate class.  PEAS uses an abundance of x,y pairs.  This class encapsulates a single coordinate.
 * This class should have been named Coordinate.
 * 
 * @author smichaels
 *
 */
public class Point {
	
	
    /**
     * The X coordinate of this <code>Point</code>.
     * If no X coordinate is set it will default to 0.
     */
    public int x;

    /**
     * The Y coordinate of this <code>Point</code>.
     * If no Y coordinate is set it will default to 0.
     */
    public int y;

 
    /**
     * Constructs and initializes a point at the origin
     * (0,&nbsp;0) of the coordinate space.
      */
    public Point() {
        this(0, 0);
    }

    /**
     * Constructs and initializes a point with the same location as
     * the specified <code>Point</code> object.
     */
    public Point(Point p) {
        this(p.x, p.y);
    }

    /**
     * Constructs and initializes a point at the specified x and y coordinates
      */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}


	/**
	 * 
	 * @return true if the Point is {@code (0,0)}
	 */
    public boolean isZero() {
    	return this.x == 0 && this.y == 0;
    }
    
    /**
     * Determines whether or not two points are equal. Two instances of
     * <code>Point</code> are equal if the values of their
     * <code>x</code> and <code>y</code> member fields, representing
     * their position in the coordinate space, are the same.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point pt = (Point)obj;
            return (x == pt.x) && (y == pt.y);
        }
        return super.equals(obj);
    }

    /**
     * Returns a string representation of this point and its location
     * in the {@code (x,y)} coordinate space. 
     */
    public String toString() {
        return x + "," + y;
    }

	/**
	 * Adds two passed coordinates 
	 * @param p1 coordinate to add
	 * @param p2 coordinate to add
	 * @return a new Point that is the sum of this Point and the passed Point
	 */
	public static Point add(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}
	
	/**
	 * Takes the product of a passed value and the passed Point
	 * @param p1 coordinate to multiply
	 * @param val value to multiply by
	 * @return a new Point that is the product of the passed Point and the passed scalar value
	 */
	public static Point multiply(Point p1, int val) {
		return new Point(p1.x * val, p1.y * val);
	}

}
