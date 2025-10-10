package ru.aston.intensive.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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

    @Retry(name = "userEventsProducer", fallbackMethod = "sendMessageFallback")
    @CircuitBreaker(name = "userEventsProducer", fallbackMethod = "sendMessageFallback")
    @Override
    public void sendUserEvent(UserEvent event) {
        kafkaTemplate.send(kafkaProperty.getTopic(), event.action(), event);
        log.info("Order sent to kafka: action={}, email={}", event.action(), event.email());
    }

    private void sendMessageFallback(UserEvent userEvent, Throwable throwable) {
        log.error("Couldn't send message to Kafka for user {}.",
                userEvent.email(), throwable);
    }
}
