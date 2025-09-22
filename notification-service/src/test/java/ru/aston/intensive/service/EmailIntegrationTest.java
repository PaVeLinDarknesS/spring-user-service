package ru.aston.intensive.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.resolver.EmailMessageResolver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
public class EmailIntegrationTest {

    @MockitoBean
    private EmailMessageResolver messageResolver;

    @Autowired
    private EmailNotificationService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("test@example.com", "username", "password"))
            .withPerMethodLifecycle(true);

    @Test
    void notifyUser_whenCorrectRecipient_message() throws Exception {
        UserEvent userEvent = new UserEvent(UserStatus.CREATED, "recipient@example.com");
        String expectedBody = "Пользователь успешно создан";

        given(messageResolver.resolveSubject(UserStatus.CREATED)).willReturn("Создание пользователя");
        given(messageResolver.resolveText(UserStatus.CREATED)).willReturn("Пользователь успешно создан");

        emailService.notifyUser(userEvent);

        assertTrue(greenMail.waitForIncomingEmail(5000, 1));

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        MimeMessage receivedMessage = messages[0];
        assertEquals(userEvent.email(), receivedMessage.getAllRecipients()[0].toString());
        assertEquals(expectedBody, receivedMessage.getContent());
    }

    @Test
    void notifyUser_whenIncorrectRecipient_throwMailException() throws Exception {
        UserEvent userEvent = new UserEvent(UserStatus.CREATED, "example.com");

        assertThrows(MailException.class, () -> emailService.notifyUser(userEvent));
        assertFalse(greenMail.waitForIncomingEmail(5000, 1));
    }
}
