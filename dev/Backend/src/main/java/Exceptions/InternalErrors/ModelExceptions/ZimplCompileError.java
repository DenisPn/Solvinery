package Exceptions.InternalErrors.ModelExceptions;

import Exceptions.ErrorData.ZimplErrorType;

/**
 * Represents an exception thrown when a compiler error occurs
 * in the ZIMPL language.
 * <p>
 * Has two fields: an error code/message pair enum {@link ZimplErrorType} and the message received in runtime.
 * @see ZimplErrorType
 */
public class ZimplCompileError extends Exception {
    private final ZimplErrorType error;

    /**
     * Constructs a new ZimplCompileError with the specified error structure and message.
     *
     * @param error   the error structure representing the specific ZIMPL compilation issue
     * @param message the detailed error message describing the compilation problem, received from the compiler
     */
    public ZimplCompileError(ZimplErrorType error, String message) {
        super(message);
        this.error=error;
    }

    public ZimplErrorType getErrorCode() {
        return error;
    }
}
