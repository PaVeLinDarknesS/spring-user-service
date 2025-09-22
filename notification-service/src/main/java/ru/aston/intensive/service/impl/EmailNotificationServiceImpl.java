package ru.aston.intensive.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.resolver.EmailMessageResolver;
import ru.aston.intensive.service.EmailNotificationService;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender emailSender;
    private final EmailMessageResolver messageResolver;

    @Override
    public void notifyUser(UserEvent userEvent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEvent.email());
        message.setSubject(messageResolver.resolveSubject(userEvent.action()));
        message.setText(messageResolver.resolveText(userEvent.action()));

        emailSender.send(message);
    }
}
