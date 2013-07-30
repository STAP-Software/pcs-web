package org.tmt.aps.peas.common;

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
     * Constructs and initializes a point at the specified
      */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     */
    public int getX() {
        return x;
    }

    /**
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the location of this point.
     * This method is included for completeness, to parallel the
     * <code>getLocation</code> method of <code>Component</code>.
     */
    public Point getLocation() {
        return new Point(x, y);
    }

    /**
     * Sets the location of the point to the specified location.
     * This method is included for completeness, to parallel the
     * <code>setLocation</code> method of <code>Component</code>.
     */
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }


    /**
     * Sets the location of this point to the specified double coordinates.
     * The double values will be rounded to integer values.
     * Any number smaller than <code>Integer.MIN_VALUE</code>
     * will be reset to <code>MIN_VALUE</code>, and any number
     * larger than <code>Integer.MAX_VALUE</code> will be
     * reset to <code>MAX_VALUE</code>.
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Translates this point, at location {@code (x,y)},
     * by {@code dx} along the {@code x} axis and {@code dy}
     * along the {@code y} axis so that it now represents the point
     * {@code (x+dx,y+dy)}.
     */
    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Determines whether or not two points are equal. Two instances of
     * <code>Point2D</code> are equal if the values of their
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
     * in the {@code (x,y)} coordinate space. This method is
     * intended to be used only for debugging purposes, and the content
     * and format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     */
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + "]";
    }
}
