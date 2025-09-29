package ru.aston.intensive.exception;

public class EmailExistingException extends RuntimeException {
    public EmailExistingException(String message) {
        super(message);
    }
}
