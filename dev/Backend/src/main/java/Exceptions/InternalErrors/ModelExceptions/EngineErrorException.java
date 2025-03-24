package Exceptions.InternalErrors.ModelExceptions;

/**
 * This exception is thrown when an error occurs during the execution or initialization
 * of the SCIP engine.
 */
public class EngineErrorException extends ModelException {
    public EngineErrorException (String message) {
        super(message);
    }
}
