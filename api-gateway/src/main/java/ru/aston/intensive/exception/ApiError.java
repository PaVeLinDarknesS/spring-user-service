package ru.aston.intensive.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApiError(
        int status,
        String title,
        String message,
        String serviceName,
        String requestPath,
        LocalDateTime timestamp
) {
}
