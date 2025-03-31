package Exceptions.InternalErrors;
@Deprecated
// Function needs to be more specific, or a specific use case needs to be given to this.
// Became deprecated on Exception hierarchy refactoring
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public static void requireNotNull(Object object, String message) {
        if(object==null){
            throw new BadRequestException(message);
        }
    }
}
