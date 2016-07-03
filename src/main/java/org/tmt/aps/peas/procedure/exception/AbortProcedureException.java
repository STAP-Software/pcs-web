package org.tmt.aps.peas.procedure.exception;

/**
 * Exception thrown when a user chooses to abort a procedure
 * @author smichaels
 *
 */
public class AbortProcedureException extends Exception {

	public AbortProcedureException(String message) {
		super(message);
	}
	
}
