package com.example.news_management_service.kafka;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClaimProducer {
    private static final String TOPIC = "claims";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendClaim(String claim) {
        String json = "{\"claim\":\"" + claim + "\"}";
        kafkaTemplate.send(TOPIC, json);
    }
}
