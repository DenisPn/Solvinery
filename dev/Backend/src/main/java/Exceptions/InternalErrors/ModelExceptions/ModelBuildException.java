package Exceptions.InternalErrors.ModelExceptions;

/**
 * This exception is thrown to indicate a failure during the model-building process.
 * Thrown when an error occurs on the initial parsing and building of the model object, and its parts.
 */
public class ModelBuildException extends ModelException {
    public ModelBuildException (String message) {
        super(message);
    }
}
