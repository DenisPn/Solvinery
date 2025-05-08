package Exceptions.UserErrors;

/**
 * An error at the user side such as, like logically wrong input, illegal user actions, etc.
 * Is not a server error-Server execution is valid when this exception is thrown.
 */
public abstract class UserInputException extends RuntimeException {
    public UserInputException (String message) {
        super(message);
    }
}
