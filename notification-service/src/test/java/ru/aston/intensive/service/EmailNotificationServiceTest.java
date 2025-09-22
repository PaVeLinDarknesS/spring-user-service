package ru.aston.intensive.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.resolver.EmailMessageResolver;
import ru.aston.intensive.service.impl.EmailNotificationServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private EmailMessageResolver messageResolver;

    @InjectMocks
    private EmailNotificationServiceImpl notificationService;

    @Test
    public void notifyUser_whenOk() {
        String expectedSubject = "expected subject";
        String expectedText = "expected text";
        UserEvent userEvent = new UserEvent(UserStatus.CREATED, "test@example.com");

        BDDMockito.given(messageResolver.resolveSubject(userEvent.action())).willReturn(expectedSubject);
        BDDMockito.given(messageResolver.resolveText(userEvent.action())).willReturn(expectedText);

        notificationService.notifyUser(userEvent);

        verify(messageResolver).resolveSubject(userEvent.action());
        verify(messageResolver).resolveText(userEvent.action());

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getSubject()).isEqualTo(expectedSubject);
        assertThat(sentMessage.getText()).isEqualTo(expectedText);
    }
}
