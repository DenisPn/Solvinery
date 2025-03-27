package Exceptions.InternalErrors.ModelExceptions;

/**
 * This exception is thrown to indicate an issue with the input provided to a model.
 * Examples of such inputs may include invalid parameters, malformed data, or
 * unexpected input types that aren't allowed.
 */
public class InvalidModelInputException extends ModelException {
    public InvalidModelInputException (String message) {
        super(message);
    }
}
