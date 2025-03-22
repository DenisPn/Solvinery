package Exceptions.InternalErrors.ModelExceptions.Parsing;

import Exceptions.InternalErrors.ModelExceptions.ModelException;

/**
 * Thrown to indicate that an error occurred while parsing a Zimpl file inside the Model package.
 * And is a specialized subclass of {@link ModelException}, and can be further extended for specific parsing exceptions, if needed.
 */
public class ParsingException extends ModelException {

    /**
     * Constructs a new ParsingException with the specified detail message.
     * This exception is thrown to indicate that an error occurred while parsing a Zimpl file.
     *
     * @param message the detail message, which provides additional information about the parsing error.
     */
    public ParsingException (String message) {
        super(message);
    }
}
