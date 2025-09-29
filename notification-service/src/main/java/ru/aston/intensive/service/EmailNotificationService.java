package ru.aston.intensive.service;

import ru.aston.intensive.dto.UserEvent;

public interface EmailNotificationService {

    void notifyUser(UserEvent userEvent);
}
