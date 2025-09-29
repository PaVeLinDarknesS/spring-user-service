package ru.aston.intensive.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errorsMap = exception.getAllErrors()
                .stream()
                .collect(Collectors
                        .toMap((error) -> ((FieldError) error).getField(),
                                ObjectError::getDefaultMessage));
        log.warn("Validation exception: {}", errorsMap);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorsMap);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<String> handleMailException(MailException exception) {

        log.error("Mail exception: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body("Не удалось отправить Email: проверьте адрес получателя и повторите попытку позже");
    }
}
