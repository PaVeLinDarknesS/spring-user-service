package ru.aston.intensive.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import ru.aston.intensive.exception.ApiError;

import java.time.LocalDateTime;

@Log4j2
@RequestMapping("/fallback")
@RestController
public class FallbackController {

    private final static String MESSAGE_PART = " is temporarily unavailable";

    @GetMapping("/users/read")
    public ResponseEntity<ApiError> userServiceReadFallback(
            ServerWebExchange exchange) {

        String serviceName = "User reader service";
        String requestPath = exchange.getRequest().getPath().value();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackBody(serviceName, requestPath));
    }

    @RequestMapping(
            value = "/users/write",
            method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
    )
    public ResponseEntity<ApiError> userServiceWriteFallback(
            ServerWebExchange exchange) {

        String serviceName = "User writer service";
        String requestPath = exchange.getRequest().getPath().value();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackBody(serviceName, requestPath));
    }

    @RequestMapping(
            value = "/emails",
            method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
    )
    public ResponseEntity<ApiError> notificationServiceFallback(ServerWebExchange exchange) {

        String serviceName = "Notification service";
        String requestPath = exchange.getRequest().getPath().value();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackBody(serviceName, requestPath));
    }

    private ApiError createFallbackBody(String serviceName, String requestPath) {

        log.warn("Circuit Breaker blocked the {}. Path: {}", serviceName, requestPath);

        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .serviceName(serviceName)
                .title("Service Unavailable")
                .message(serviceName + MESSAGE_PART)
                .requestPath(requestPath)
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
    }
}
