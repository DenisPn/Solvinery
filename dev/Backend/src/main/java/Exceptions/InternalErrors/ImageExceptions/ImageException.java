package Exceptions.InternalErrors.ImageExceptions;

/**
 * An undefined exception thrown from within the {@link Image} Package.
 * Should be extended with specific exceptions for specific issues.
 */
public abstract class ImageException extends RuntimeException {
    /**
     * Constructs a new ImageException with the specified detail message.
     *
     * @param message the detail message providing additional information about the exception
     */
    public ImageException (String message) {
        super(message);
    }
}
