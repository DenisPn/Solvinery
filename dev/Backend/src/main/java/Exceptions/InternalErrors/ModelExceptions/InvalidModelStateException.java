package Exceptions.InternalErrors.ModelExceptions;

/**
 * This exception is thrown to indicate that the state of a model is invalid.
 * Example of cases for invalid states:
 * different types of structure issues for sets or params, invalid set names (a set that doesn't exist in zpl code?),
 * no value for code elements, nulls at places they shouldn't be and so on.
 */
public class InvalidModelStateException extends ModelException {
    public InvalidModelStateException (String message) {
        super(message);
    }
}
