/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;


public class ComputationException extends Exception {
	
	private static final long serialVersionUID = -2741048348398071514L;
	
	String errorCode;
	
	public ComputationException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
}
