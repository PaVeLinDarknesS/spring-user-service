package ru.aston.intensive.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;

public interface UserEventConsumer {
    void consumeUserEvent(ConsumerRecord<UserStatus, UserEvent> record);
}
