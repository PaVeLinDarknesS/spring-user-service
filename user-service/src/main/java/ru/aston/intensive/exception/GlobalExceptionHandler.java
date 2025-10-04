package ru.aston.intensive.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExistingException.class)
    public ResponseEntity<ApiError> handleExistEmail(EmailExistingException exception) {
        ApiError apiError = ApiError.builder()
                .title("Email exist")
                .status(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception) {
        ApiError apiError = ApiError.builder()
                .title("Resource not found")
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        ApiError apiError = ApiError.builder()
                .title("Validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .message(exception.getAllErrors()
                        .stream()
                        .map(error -> String.format("%s: %s", ((FieldError) error).getField(), error.getDefaultMessage()))
                        .collect(Collectors.joining("; ")))
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handlePathVariableValidationException(ConstraintViolationException exception) {
        ApiError apiError = ApiError.builder()
                .title("Path variable validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .message(exception.getConstraintViolations()
                        .stream()
                        .map(violation -> {
                            String path = violation.getPropertyPath().toString();
                            String parameterName = path.contains(".")
                                    ? path.substring(path.lastIndexOf(".") + 1)
                                    : path;
                            return String.format("%s: %s", parameterName, violation.getMessage());
                        })
                        .collect(Collectors.joining("; ")))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}
