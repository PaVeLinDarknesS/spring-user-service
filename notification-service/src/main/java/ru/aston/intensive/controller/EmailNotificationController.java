package ru.aston.intensive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.service.EmailNotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailNotificationController {

    private final EmailNotificationService emailNotificationService;

    @PostMapping
    public ResponseEntity<String> sentEmail(@RequestBody @Valid UserEvent userEvent) {

        emailNotificationService.notifyUser(userEvent);

        return ResponseEntity.ok().build();
    }
}
