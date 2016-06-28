/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;

/**
 * A rectangle represented by two points
 * @author smichaels
 *
 */
public class Rect {
	
	
    /**
     * The first point in the rectangle
     */
    public Point p1;

    /**
     * The second point in the rectangle
     */
    public Point p2;

 
    /**
     * Constructs a rectangle from another rectangle of the same coordinate set.
     */
    public Rect(Rect p) {
        this(p.p1, p.p2);
    }

    /**
     * Constructs a rectangle with null points
     */
    public Rect() {
    	
    }
    
    /**
     * Constructs and initializes the rectangle with the specified set of points
      */
    public Rect(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Constructs and initializes the rectangle with the specified coordinate set
      */
    public Rect(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }


	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

    /**
     * Determines whether or not two rectangles are equal. Two instances of
     * <code>Rect</code> are equal if the values of their
     * <code>p1</code> and <code>p1</code> member fields, representing
     * each coordinate are the same.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Rect) {
            Rect rect = (Rect)obj;
            return (p1.equals(rect.p1)) && (p2.equals(rect.p2));
        }
        return super.equals(obj);
    }

    /**
     * Returns a string representation of this rectangle
     */
    public String toString() {
        return "[[" + p1 + "],[" + p2 + "]]";
    }
    
    /**
     * Sets all coordinates of this rectangle to zero
     */
    public void reset() {
    	p1 = new Point(0,0);
    	p2 = new Point(0,0);
    }
}
