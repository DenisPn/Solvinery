package Exceptions.InternalErrors.ModelExceptions;

/**
 * An undefined exception thrown from within the Model Package.
 * Should be extended with specific exceptions for specific issues.
 */
public abstract class ModelException extends RuntimeException {

    /**
     * Constructs a new ModelException with the specified detail message.
     *
     * @param message the detail message, which provides additional information about the exception.
     */
    public ModelException (String message) {
        super(message);
    }
}
