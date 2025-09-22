package ru.aston.intensive.resolver;

import ru.aston.intensive.enumerated.UserStatus;

public interface EmailMessageResolver {

    String resolveSubject(UserStatus status);

    String resolveText(UserStatus status);
}
