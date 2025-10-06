package ru.aston.intensive.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest({FallbackController.class})
class FallbackControllerIntegrationTest {

    private static final String URI_START = "/fallback";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void userServiceReadFallback_whenOk() {

        String serviceName = "User reader service";

        webTestClient.get().uri(URI_START + "/users/read")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.title").isEqualTo("Service Unavailable")
                .jsonPath("$.serviceName").isEqualTo(serviceName);
    }

    @Test
    void userServiceWriteFallback_whenOK() {

        String serviceName = "User writer service";

        webTestClient.post().uri(URI_START + "/users/write")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.title").isEqualTo("Service Unavailable")
                .jsonPath("$.serviceName").isEqualTo(serviceName);
    }

    @Test
    void notificationServiceFallback_whenOk() {

        String serviceName = "Notification service";

        webTestClient.post().uri(URI_START + "/emails")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.title").isEqualTo("Service Unavailable")
                .jsonPath("$.serviceName").isEqualTo(serviceName);
    }
}
