package ru.aston.intensive.exception;

import lombok.Builder;

@Builder
public record ApiError(
        String title,
        int status,
        String message
) {
}
