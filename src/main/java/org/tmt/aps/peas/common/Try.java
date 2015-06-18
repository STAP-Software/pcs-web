package org.tmt.aps.peas.common;

public class Try<T> {
	
	private T t;
	private Exception e;
	
	public Try(T t) {
		this.t = t;
	}
	
	public Try(Exception e) {
		this.e = e;
	}
	
	public boolean isException() {
		return e != null;
	}
    
	public T getValue() { 
    	return t; 
    }
    
    public Exception getException() { 
    	return e; 
    }
    
}

