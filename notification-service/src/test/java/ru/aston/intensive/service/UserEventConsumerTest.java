package ru.aston.intensive.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.service.impl.UserEventConsumerImpl;
import ru.aston.intensive.util.config.KafkaProperty;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        classes = {
                ValidationAutoConfiguration.class,
                EmailNotificationService.class,
                UserEventConsumerImpl.class
        }
)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.kafka.enabled=false"
})
class UserEventConsumerTest {

    @MockitoBean
    private KafkaProperty kafkaProperty;

    @Autowired
    private Validator validator;

    @MockitoBean
    private EmailNotificationService emailService;

    @Autowired
    private UserEventConsumerImpl userEventConsumer;


    @Test
    public void consumeUserEvent_whenOk() {
        UserEvent userEvent = new UserEvent(UserStatus.CREATED, "created@email.com");
        ConsumerRecord<UserStatus, UserEvent> record
                = new ConsumerRecord<>("topic", 0, 0L, userEvent.action(), userEvent);

        BDDMockito.willDoNothing().given(emailService).notifyUser(any(UserEvent.class));

        userEventConsumer.consumeUserEvent(record);

        verify(emailService).notifyUser(any(UserEvent.class));
    }

    @Test
    public void consumeUserEvent_whenInvalidUserEvent_throwConstraintViolationException() {
        UserEvent userEvent = new UserEvent(UserStatus.CREATED, "wrongemail.com");
        ConsumerRecord<UserStatus, UserEvent> record
                = new ConsumerRecord<>("topic", 0, 0L, userEvent.action(), userEvent);

        BDDMockito.willDoNothing().given(emailService).notifyUser(any(UserEvent.class));

        assertThrows(ConstraintViolationException.class, () -> userEventConsumer.consumeUserEvent(record));

        verify(emailService, never()).notifyUser(any(UserEvent.class));
    }
}
