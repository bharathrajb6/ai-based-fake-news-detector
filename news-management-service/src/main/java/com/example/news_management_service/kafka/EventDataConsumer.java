package com.example.news_management_service.kafka;

import com.example.news_management_service.dto.response.ClaimData;
import com.example.news_management_service.dto.response.FactCheckResponse;
import com.example.news_management_service.service.EmailService;
import com.example.news_management_service.service.NewsCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventDataConsumer {

    private final EmailService emailService;
    private final NewsCheckService newsCheckService;

    /**
     * Listens to the "claims-verified" topic and processes verified claim data.
     * Updates the news data store and sends an email notification.
     *
     * @param claimData the consumed verified claim data payload
     */
    /**
     * Listens to the "claims-verified" topic, and whenever a message is consumed,
     * it updates the news details and sends an email with the result.
     *
     * @param claimData the claim data to be processed
     */
    @KafkaListener(topics = "claims-verified", groupId = "java-service", containerFactory = "claimDataKafkaListenerContainerFactory")
    public void consumeClaimMessage(ClaimData claimData) {
        log.info("Received ClaimData from topic 'claims-verified': {}", claimData);
        try {
            newsCheckService.updateNewsDetails(claimData);
            emailService.sendEmailInfo(claimData);
        } catch (Exception ex) {
            log.error("Error processing ClaimData: {}", claimData, ex);
        }
    }


    /**
     * Consumes the Google Fact Check API response published by fact-check-service
     * on topic "fact-checked-data".
     *
     * @param response the fact check response payload
     */
    @KafkaListener(topics = "fact-checked-data", groupId = "java-service", containerFactory = "factCheckResponseKafkaListenerContainerFactory")
    public void consumeFactCheckResponse(FactCheckResponse response) {
        log.info("Consumed FactCheckResponse from topic 'fact-checked-data': {}", response);
    }
}
