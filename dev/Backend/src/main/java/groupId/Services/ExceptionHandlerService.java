package groupId.Services;

import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Exceptions.SolverExceptions.ValidationException;
import Exceptions.UserErrors.UserDataException;
import groupId.DTO.Factories.ExceptionRecordFactory;
import groupId.DTO.Records.Requests.Responses.ExceptionDTO;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Usage instructions: use some Record to make the DTO of the exception and return it.
 * Should have a method for each exception needing a custom or logging.
 * The message/logging itself should be in the factory, No logic is to be implemented here.
 */
@RestControllerAdvice
public class ExceptionHandlerService {

    @NonNull
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(500).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull RuntimeException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(500).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull MethodArgumentNotValidException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(500).body(errorResponse);
    }

    @NonNull
    @ExceptionHandler(ZimplCompileError.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull ZimplCompileError ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull HttpRequestMethodNotSupportedException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);

    }

    @NonNull
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull HttpMediaTypeNotSupportedException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }

    @NonNull
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull HttpMessageNotReadableException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }

    // Fallback for uncaught Spring-specific exceptions
    @NonNull
    @ExceptionHandler(NestedRuntimeException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull NestedRuntimeException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(UserDataException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull UserDataException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull NoResourceFoundException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }
    @NonNull
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionDTO> handleException(@NonNull ValidationException ex) {
        ExceptionDTO errorResponse = ExceptionRecordFactory.makeDTO(ex);
        return ResponseEntity.status(400).body(errorResponse);
    }
}