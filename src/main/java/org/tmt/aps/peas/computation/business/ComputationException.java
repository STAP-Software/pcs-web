/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

/**
 * Exception thrown when a computation fails.  Thrown by {@link org.tmt.aps.peas.computation.business.ComputationLibraryImpl} methods and {@link org.tmt.aps.peas.computation.java.JavaComputations} methods.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl
 * @see org.tmt.aps.peas.computation.java.JavaComputations
 */
public class ComputationException extends Exception {
	
	private static final long serialVersionUID = -2741048348398071514L;
	
	String errorCode;
	/**
	 * Constructor with message and error code
	 * @Deprecated
	 */
	public ComputationException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Constructor with message string
	 * @param message the message string 
	 */
	public ComputationException(String message) {
		super(message);
	}


}
