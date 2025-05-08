package Exceptions.InternalErrors;

/**
 * AN error at the frontend, such as malformed JSONs and invalid IDs.
 */
public class ClientSideError extends RuntimeException {
    public ClientSideError (String message) {
        super(message);
    }
}
