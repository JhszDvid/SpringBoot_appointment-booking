package com.jddev.crmapp.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jddev.crmapp.enums.ResponseType;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {
    @ExceptionHandler(value={UserAlreadyExistsException.class, CustomerAlreadyExistsException.class})
    public ResponseEntity<Object> handleConflictException(RuntimeException ex)
    {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.CONFLICT)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }

    @ExceptionHandler(value={UserNotFoundException.class, PropertyReferenceException.class, IllegalArgumentException.class, AppointmentNotAvailableException.class, InvalidConfirmationException.class})
    public ResponseEntity<Object> handleBadRequestException(RuntimeException ex)
    {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.BAD_REQUEST)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }

    @ExceptionHandler(value={BadCredentialsException.class})
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex)
    {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.BAD_REQUEST)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }

    @ExceptionHandler(value={LockedException.class, DisabledException.class, AccountExpiredException.class})
    public ResponseEntity<Object> handleAccountLockedException(Exception ex)
    {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.LOCKED)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }

    @ExceptionHandler(value={TokenExpiredException.class})
    public ResponseEntity<Object> handleTokenExceptions(Exception ex)
    {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.UNAUTHORIZED)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }

    @ExceptionHandler(value = {InvalidStateTransitionException.class})
    public ResponseEntity<?> handleInvalidArgumentManual(InvalidStateTransitionException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return new APIResponseObject.Builder()
                .withMessage("VALIDATION")
                .withStatusCode(HttpStatus.BAD_REQUEST)
                .withResponseType(ResponseType.ERROR)
                .withObject(errorMap)
                .buildResponse();
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException ex)
    {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return new APIResponseObject.Builder()
                .withMessage("VALIDATION")
                .withStatusCode(HttpStatus.BAD_REQUEST)
                .withResponseType(ResponseType.ERROR)
                .withObject(errorMap)
                .buildResponse();

    }

    @ExceptionHandler(value={UnexpectedRollbackException.class})
    public ResponseEntity<Object> handleUnexpectedRollback(UnexpectedRollbackException ex)
    {
        APIErrorObject err = new APIErrorObject(
                HttpStatus.BAD_REQUEST,
                "Transaction had to rollback! Check for duplicates.",
                new Date()
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={InternalErrorException.class})
    public ResponseEntity<Object> handleInternalError(InternalErrorException ex) {
        return new APIResponseObject.Builder()
                .withMessage(ex.getMessage())
                .withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .withResponseType(ResponseType.ERROR)
                .buildResponse();
    }
}
