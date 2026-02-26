package org.tmt.aps.peas.procedure.exception;

/**
 * Exception thrown when a user chooses to abort a procedure
 * @author smichaels
 *
 */
public final class AbortProcedureException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public AbortProcedureException(String message) {
        super(message, null, false, false); // no stack trace, no suppression
    }
}
