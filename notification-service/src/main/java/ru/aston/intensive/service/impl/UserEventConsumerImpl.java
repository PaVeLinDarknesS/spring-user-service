package ru.aston.intensive.service.impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.service.EmailNotificationService;
import ru.aston.intensive.service.UserEventConsumer;

import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserEventConsumerImpl implements UserEventConsumer {

    private final Validator validator;

    private final EmailNotificationService emailService;

    @Override
    @KafkaListener(topics = "user-events", groupId = "notification-user-group")
    public void consumeUserEvent(ConsumerRecord<UserStatus, UserEvent> record) {
        log.info(
                "Received order: order={}, key={}, partition={}",
                record.value(),
                record.key(),
                record.partition()
        );

        UserEvent event = record.value();
        Set<ConstraintViolation<UserEvent>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            log.error("Error validation UserEvent - {}", event);
            throw new ConstraintViolationException("Validation failed for UserEvent", violations);
        }

        emailService.notifyUser(event);
    }
}
