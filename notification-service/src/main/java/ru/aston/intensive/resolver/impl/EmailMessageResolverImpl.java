package ru.aston.intensive.resolver.impl;

import org.springframework.stereotype.Component;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.resolver.EmailMessageResolver;

import java.util.EnumMap;
import java.util.Map;

@Component
public class EmailMessageResolverImpl implements EmailMessageResolver {

    private final Map<UserStatus, String> emailSubject;
    private final Map<UserStatus, String> emailText;

    public EmailMessageResolverImpl() {
        emailSubject = new EnumMap<>(UserStatus.class);
        emailText = new EnumMap<>(UserStatus.class);

        emailSubject.put(UserStatus.CREATED, "Создание пользователя");
        emailText.put(UserStatus.CREATED, "Здравствуйте! Ваш аккаунт на сайте 'user-service' был успешно создан");

        emailSubject.put(UserStatus.DELETED, "Удаление пользователя");
        emailText.put(UserStatus.DELETED, "Здравствуйте! Ваш аккаунт на сайте 'user-service' был удалён.");
    }

    @Override
    public String resolveSubject(UserStatus status) {
        return emailSubject.get(status);
    }

    @Override
    public String resolveText(UserStatus status) {
        return emailText.get(status);
    }
}
