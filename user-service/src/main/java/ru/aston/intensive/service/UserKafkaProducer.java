package ru.aston.intensive.service;

import ru.aston.intensive.dto.UserEvent;

public interface UserKafkaProducer {

    void sendUserEvent(UserEvent event);
}
