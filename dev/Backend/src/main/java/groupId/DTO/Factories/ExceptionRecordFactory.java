package groupId.DTO.Factories;

import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Exceptions.SolverExceptions.ValidationException;
import Exceptions.UserErrors.UserDataException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import groupId.DTO.Records.Requests.Responses.ExceptionDTO;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExceptionRecordFactory {
    private static final String ohNo= "An extra extra fatal occurred while handling an error";

    //TODO A singular exceptionDTO factory, made with the purpose of having a central fatal/non fatal error logging class,
    // need to implement logger. Remove informative errors messages from user side, for network/internal errors.
    @NonNull
    public static ExceptionDTO makeDTO(Exception exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        //return new ExceptionDTO("An unknown error occurred, see log for details, or contract the developer");
        return new ExceptionDTO("An uncaught error occurred- this is a bug and should not happen.\n " +
                "App functionality will probably break, and user is now authorized to slap the developers.");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull RuntimeException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
//        return new ExceptionDTO("An unexpected fatal error occurred. See log for details, or contract the developer");
        return new ExceptionDTO("Full error ahead, this is temporary and should not be shown to end users: "+ exception.getMessage());

    }

    @NonNull
    public static ExceptionDTO makeDTO(@NonNull IOException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("An error occurred while trying to access the file system.\n" +
                "See log for details, or contract the developer (" +exception.getMessage() + ")"  );
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull ZimplCompileError exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO(String.format("An error occurred while compiling zimpl code.\nError code:%s\nError message:%s",
                exception.getErrorCode(),exception.getMessage()));
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull HttpRequestMethodNotSupportedException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO(String.format("A server communication error occurred, HTTP request is invalid. Current method:%s\n" +
                "Supported methods:%s",exception.getMethod(), Arrays.toString(exception.getSupportedMethods())));
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull HttpMediaTypeNotSupportedException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("A server communication error occurred, HTTP content media type not supported." +exception.getMessage() + ")");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull HttpMessageNotReadableException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("A server communication error occurred, HTTP request content type is invalid: " +exception.getMessage() + ")");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull InvalidFormatException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("A server communication error occurred. HTTP request payload parsing failed, invalid format: " +exception.getMessage() + ")");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull NestedRuntimeException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("An unhandled server communication occurred. (" +exception.getMessage() + ")");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull NoResourceFoundException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO("An unhandled server communication occurred. (" +exception.getMessage() + ")");
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull UserDataException exception) {
        Objects.requireNonNull(exception,ohNo);
        return new ExceptionDTO(exception.getMessage());
    }
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull ValidationException exception) {
        Objects.requireNonNull(exception,ohNo);
        //TODO: LOG
        return new ExceptionDTO(exception.getMessage());
    }
    //kinda proud of this one
    @NonNull
    public static ExceptionDTO makeDTO(@NonNull MethodArgumentNotValidException exception) {
        Objects.requireNonNull(exception,ohNo);
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                // Map each FieldError to its error message
                .map(fieldError -> {
                    if(fieldError.getDefaultMessage() != null) {
                        return fieldError.getField()+" was rejected. reason: " +
                                fieldError.getDefaultMessage()+ " provided value: " +
                                fieldError.getRejectedValue();
                    }
                        else {
                            return "Validation error message generation occurred";
                    }
                })
                .collect(Collectors.joining(", "));

            //TODO: LOG
            return new ExceptionDTO("Error in DTO validation: "+ errorMessage);
    }
}
