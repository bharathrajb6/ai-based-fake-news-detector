package com.example.news_management_service.config;

import com.example.news_management_service.dto.response.ClaimData;
import com.example.news_management_service.dto.response.FactCheckResponse;
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

    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "java-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // ✅ Allow JSON deserialization without headers
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.news_management_service.dto.response");

        // ✅ Make sure consumer reads from beginning if no offset is present
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    @Bean
    public ConsumerFactory<String, ClaimData> claimDataConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(ClaimData.class, false)
        );
    }

    @Bean
    public ConsumerFactory<String, FactCheckResponse> factCheckResponseConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(FactCheckResponse.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClaimData> claimDataKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ClaimData> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(claimDataConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FactCheckResponse> factCheckResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FactCheckResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(factCheckResponseConsumerFactory());
        return factory;
    }
}