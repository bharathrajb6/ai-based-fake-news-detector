package com.example.news_management_service.kafka;

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

    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "java-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.news_management_service.dto");
        return props;
    }


    // eneric ConsumerFactory
    public <T> ConsumerFactory<String, T> consumerFactory(Class<T> targetType) {
        return new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                new JsonDeserializer<>(targetType, false) // false = disable type headers
        );
    }

    // Generic KafkaListenerContainerFactory
    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> kafkaListenerContainerFactory(Class<T> targetType) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(targetType));
        return factory;
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClaimData> claimDataKafkaListenerContainerFactory() {
        return kafkaListenerContainerFactory(ClaimData.class);
    }

}