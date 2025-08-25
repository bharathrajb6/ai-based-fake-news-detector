package com.example.news_management_service.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendTopic(String topic, String data) {
        kafkaTemplate.send(topic, data);
        log.info("Claim sent to Kafka: {}", data);
    }
}
