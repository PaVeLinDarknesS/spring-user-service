package ru.aston.intensive.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.entity.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class UserRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    UserRepository userRepository;

    @Test
    void findByEmail_whenEmailExist() {
        String email = "joshua.bloch@example.com";

        Optional<UserEntity> findUser = userRepository.findByEmail(email);

        assertTrue(findUser.isPresent());
        assertEquals(email, findUser.get().getEmail());
    }

    @Test
    void findByEmail_whenEmailNotExist_emptyOptional() {
        String email = "notExist@email.com";

        Optional<UserEntity> findUser = userRepository.findByEmail(email);

        assertTrue(findUser.isEmpty());
    }
}
