package com.example.source_credibility_service.kafka;

import com.example.source_credibility_service.dto.request.EvaluateSourceRequest;
import com.example.source_credibility_service.dto.response.SourceCredibilityResponse;
import com.example.source_credibility_service.service.SourceCredibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CredibilityConsumer {

    private final SourceCredibilityService sourceCredibilityService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "credibility-check", groupId = "source-credibility-service", containerFactory = "evaluateSourceRequestKafkaListenerContainerFactory")
    public void consume(EvaluateSourceRequest request) {
        log.info("Consumed EvaluateSourceRequest: {}", request);
        SourceCredibilityResponse response = sourceCredibilityService.evaluate(request);
        kafkaTemplate.send("credibility-checked-data", response);
        log.info("Published SourceCredibilityResponse for site {}", response.getSite());
    }
}

