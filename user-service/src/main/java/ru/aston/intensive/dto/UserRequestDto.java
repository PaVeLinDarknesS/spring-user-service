package ru.aston.intensive.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotBlank(message = "Имя не должно быть пустым")
        @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
        String name,

        @Email(message = "Проверьте email на корректность")
        String email,

        @Min(value = 16, message = "Пользователь должен быть старше 16")
        @Max(value = 100, message = "Пользователь должен быть младше 100")
        Integer age
) {
}
