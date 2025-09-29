package ru.aston.intensive.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import ru.aston.intensive.enumerated.UserStatus;

public record UserEvent(

        @NotNull(message = "Событие для пользователя не может быть пустым")
        UserStatus action,

        @Email(message = "Проверьте email на корректность")
        String email
) {
}
