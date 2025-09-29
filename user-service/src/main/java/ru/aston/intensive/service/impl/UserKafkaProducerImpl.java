package ru.aston.intensive.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.service.UserKafkaProducer;
import ru.aston.intensive.util.config.KafkaProperty;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserKafkaProducerImpl implements UserKafkaProducer {

    private final KafkaProperty kafkaProperty;

    private final KafkaTemplate<UserStatus, UserEvent> kafkaTemplate;

    @Override
    public void sendUserEvent(UserEvent event) {
        kafkaTemplate.send(kafkaProperty.getTopic(), event.action(), event);
        log.info("Order sent to kafka: action={}, email={}", event.action(), event.email());
    }
}
