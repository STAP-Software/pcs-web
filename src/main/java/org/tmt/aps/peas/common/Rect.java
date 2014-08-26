/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.common;


public class Rect {
	
	
    /**
     * The X coordinate of this <code>Point</code>.
     * If no X coordinate is set it will default to 0.
     */
    public Point p1;

    /**
     * The Y coordinate of this <code>Point</code>.
     * If no Y coordinate is set it will default to 0.
     */
    public Point p2;

 
    /**
     * Constructs and initializes a point with the same location as
     * the specified <code>Point</code> object.
     */
    public Rect(Rect p) {
        this(p.p1, p.p2);
    }

    public Rect() {
    	
    }
    
    /**
     * Constructs and initializes a point at the specified
      */
    public Rect(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
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
     * Determines whether or not two points are equal. Two instances of
     * <code>Point2D</code> are equal if the values of their
     * <code>x</code> and <code>y</code> member fields, representing
     * their position in the coordinate space, are the same.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Rect) {
            Rect rect = (Rect)obj;
            return (p1.equals(rect.p1)) && (p2.equals(rect.p2));
        }
        return super.equals(obj);
    }

    /**
     * Returns a string representation of this point and its location
     * in the {@code (x,y)} coordinate space. This method is
     * intended to be used only for debugging purposes, and the content
     * and format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     */
    public String toString() {
        return "[[" + p1 + "],[" + p2 + "]]";
    }
    
    public void reset() {
    	p1 = new Point(0,0);
    	p2 = new Point(0,0);
    }
}
