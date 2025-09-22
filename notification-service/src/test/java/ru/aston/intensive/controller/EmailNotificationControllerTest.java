package ru.aston.intensive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.service.EmailNotificationService;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(EmailNotificationController.class)
class EmailNotificationControllerTest {

    private static final String URI = "/api/notification-service/emails";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailNotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void sentEmail_whenOk() throws Exception {

        UserEvent requestEvent = new UserEvent(UserStatus.CREATED, "test@test.ru");

        BDDMockito.willDoNothing().given(notificationService).notifyUser(any(UserEvent.class));

        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestEvent)))
                .andExpect(status().isOk())
                .andExpect(content().string("Email отправлен, проверьте почту"));
    }

    @Test
    void sentEmail_invalidUserEvent_status400BadRequestAndErrors() throws Exception {
        UserEvent invalidEvent = new UserEvent(null, "invalidEmail");

        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.action", matchesPattern(".+")))
                .andExpect(jsonPath("$.email", matchesPattern(".+")));

        verify(notificationService, never()).notifyUser(any(UserEvent.class));
    }

    @Test
    void sentEmail_errorInSendingEmail_status400BadRequestAndErrors() throws Exception {
        UserEvent invalidEvent = new UserEvent(null, "invalidEmail");

        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.action", matchesPattern(".+")))
                .andExpect(jsonPath("$.email", matchesPattern(".+")));

        verify(notificationService, never()).notifyUser(any(UserEvent.class));
    }

    @Test
    void update_whenServiceThrowMailException_status418() throws Exception {
        UserEvent invalidEvent = new UserEvent(UserStatus.DELETED, "any@email.com");

        BDDMockito.willThrow(new MailSendException("MailException"))
                .given(notificationService).notifyUser(any(UserEvent.class));

        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isIAmATeapot())
                .andExpect(content()
                        .string("Не удалось отправить Email: проверьте адрес получателя и повторите попытку позже"));

        verify(notificationService).notifyUser(any(UserEvent.class));
    }
}
