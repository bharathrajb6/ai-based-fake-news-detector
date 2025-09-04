package com.example.news_management_service.config;

import com.example.news_management_service.dto.response.ClaimData;
import com.example.news_management_service.dto.response.FactCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for news-management-service.
 * <p>
 * Configures tolerant JSON deserialization and a common error handler to skip malformed records
 * for topics consumed by this service.
 */
@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    /**
     * Builds the base consumer configuration shared by all listener container factories.
     *
     * @return map of base Kafka consumer properties
     */
    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "java-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Wrap JSON deserializer with error handling to skip malformed records
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Allow JSON deserialization without type headers and trust local DTO package
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.news_management_service.dto.response");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // Read from beginning if no offset
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        log.info("Initialized base Kafka consumer properties for news-management-service");
        return props;
    }

    @Bean
    /**
     * ConsumerFactory for ClaimData messages consumed from Kafka.
     *
     * @return a configured consumer factory for ClaimData
     */
    public ConsumerFactory<String, ClaimData> claimDataConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(ClaimData.class, false)
        );
    }

    @Bean
    /**
     * ConsumerFactory for FactCheckResponse messages consumed from Kafka.
     *
     * @return a configured consumer factory for FactCheckResponse
     */
    public ConsumerFactory<String, FactCheckResponse> factCheckResponseConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(FactCheckResponse.class, false)
        );
    }

    @Bean
    /**
     * Listener container factory for ClaimData payloads.
     * Sets a common error handler that skips bad records without retries.
     *
     * @return a configured listener container factory
     */
    public ConcurrentKafkaListenerContainerFactory<String, ClaimData> claimDataKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ClaimData> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(claimDataConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        log.info("Configured ClaimData Kafka listener container factory with error handler");
        return factory;
    }

    @Bean
    /**
     * Listener container factory for FactCheckResponse payloads.
     * Sets a common error handler that skips bad records without retries.
     *
     * @return a configured listener container factory
     */
    public ConcurrentKafkaListenerContainerFactory<String, FactCheckResponse> factCheckResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FactCheckResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(factCheckResponseConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        log.info("Configured FactCheckResponse Kafka listener container factory with error handler");
        return factory;
    }
}