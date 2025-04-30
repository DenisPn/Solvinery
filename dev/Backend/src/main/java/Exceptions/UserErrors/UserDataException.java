package Exceptions.UserErrors;

public class UserDataException extends RuntimeException {
    public UserDataException (String message) {
        super(message);
    }
}
