package Exceptions.UserErrors;

public class ZimplCompileError extends Exception {
    final int errorCode;
    public ZimplCompileError(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
