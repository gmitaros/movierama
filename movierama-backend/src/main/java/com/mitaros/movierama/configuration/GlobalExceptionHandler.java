package com.mitaros.movierama.configuration;

import com.mitaros.movierama.dto.ErrorResponse;
import com.mitaros.movierama.exceptions.InvalidTokenException;
import com.mitaros.movierama.exceptions.MovieramaGenericException;
import com.mitaros.movierama.exceptions.OperationNotPermittedException;
import com.mitaros.movierama.exceptions.TokenAlreadyValidatedException;
import com.mitaros.movierama.exceptions.TokenExpiredException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashSet;
import java.util.Set;

import static com.mitaros.movierama.dto.enums.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(INVALID_TOKEN.getCode())
                        .errorDescription(INVALID_TOKEN.getDescription())
                        .build()
                );
    }

    @ExceptionHandler(MovieramaGenericException.class)
    public ResponseEntity<ErrorResponse> handleMovieramaGenericException(MovieramaGenericException ex) {
        return ResponseEntity
                .status(ex.getHttpStatusCode())
                .body(ErrorResponse.builder()
                        .errorCode(ex.getCode())
                        .errorDescription(ex.getDescription())
                        .build()
                );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(TOKEN_EXPIRED.getCode())
                        .errorDescription(TOKEN_EXPIRED.getDescription())
                        .build()
                );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(BAD_CREDENTIALS.getCode())
                        .errorDescription(BAD_CREDENTIALS.getDescription())
                        .error("Email and / or Password is incorrect")
                        .build()
                );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .errorCode(ACCOUNT_LOCKED.getCode())
                        .errorDescription(ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ACCOUNT_DISABLED.getCode())
                                .errorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .errorCode(BAD_CREDENTIALS.getCode())
                                .errorDescription(BAD_CREDENTIALS.getDescription())
                                .error("Email and / or Password is incorrect")
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(TokenAlreadyValidatedException.class)
    public ResponseEntity<ErrorResponse> handleException(TokenAlreadyValidatedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ErrorResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .errorDescription("Internal error, please contact the admin")
                                .error(exp.getMessage())
                                .build()
                );
    }
}
