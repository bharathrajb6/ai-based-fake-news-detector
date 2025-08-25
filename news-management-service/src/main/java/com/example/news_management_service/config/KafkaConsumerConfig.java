package com.example.news_management_service.config;

import com.example.news_management_service.dto.ClaimData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    /**
     * Return a {@link Map} of common properties shared by all Kafka listeners.
     *
     * @return a {@link Map} with common Kafka listener properties
     */
    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "java-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.news_management_service.dto");
        return props;
    }


    /**
     * Returns a {@link ConsumerFactory} suitable for the given {@code targetType}.
     *
     * @param targetType the target type for deserialization
     * @return a {@link ConsumerFactory} that can create a consumer for the given type
     */
    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(targetType, false) // false = disable type headers
        );
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} that can be used to create listeners that consume
     * Kafka messages of the given type.
     *
     * @param targetType the type of the payloads in the Kafka messages
     * @param <T>        the type of the payloads in the Kafka messages
     * @return a {@link ConcurrentKafkaListenerContainerFactory} that can be used to create listeners that consume
     * Kafka messages of the given type
     */
    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        return factory;
    }


    /**
     * Returns a {@link ConcurrentKafkaListenerContainerFactory} suitable for consuming "claims-verified" Kafka messages.
     *
     * @return a {@link ConcurrentKafkaListenerContainerFactory} that can be used to create listeners that consume
     * "claims-verified" Kafka messages
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClaimData> claimDataKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(ClaimData.class);
    }

}