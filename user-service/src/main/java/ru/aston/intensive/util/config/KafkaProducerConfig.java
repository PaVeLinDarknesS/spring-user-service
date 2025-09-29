package ru.aston.intensive.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.enumerated.UserStatus;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Autowired
    private KafkaProperty kafkaProperty;

    @Bean
    public ProducerFactory<UserStatus, UserEvent> producerFactory(
            ObjectMapper objectMapper
    ) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperty.getBootstrapServers());

        JsonSerializer<UserStatus> keySerializer = new JsonSerializer<>(objectMapper);
        keySerializer.setAddTypeInfo(false);
        JsonSerializer<UserEvent> valueSerializer = new JsonSerializer<>(objectMapper);
        valueSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                keySerializer,
                valueSerializer
        );
    }

    @Bean
    public KafkaTemplate<UserStatus, UserEvent> kafkaTemplate(
            ProducerFactory<UserStatus, UserEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
