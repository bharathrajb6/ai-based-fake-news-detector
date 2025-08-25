package com.example.news_management_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClaimConsumer {

    @KafkaListener(topics = "claims-verified", groupId = "java-service")
    public void consume(String message) {
        System.out.println("Verified claim received: " + message);
    }
}
