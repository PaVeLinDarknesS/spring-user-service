package ru.aston.intensive.util.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.aston.intensive.dto.UserEvent;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private KafkaProperty kafkaProperty;

    @Bean
    public ConsumerFactory<String, UserEvent> consumerFactory(
            ObjectMapper objectMapper
    ) {

        Map<String, Object> propMap = new HashMap<>();
        propMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperty.getBootstrapServers());
        propMap.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperty.getConsumerGroupId());
        propMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperty.getAutoOffset());

        JsonDeserializer<UserEvent> jsonDeserializer =
                new JsonDeserializer<>(UserEvent.class, objectMapper);

        return new DefaultKafkaConsumerFactory<>(
                propMap,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, UserEvent> consumerFactory
    ) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, UserEvent>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
