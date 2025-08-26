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
     * Listens to the "claims-verified" topic, and whenever a message is consumed,
     * it updates the news details and sends an email with the result.
     *
     * @param claimData the claim data to be processed
     */
    @KafkaListener(topics = "claims-verified", groupId = "java-service", containerFactory = "claimDataKafkaListenerContainerFactory")
    public void consumeClaimMessage(ClaimData claimData) {
        log.info("claim - verified messages - {}", claimData);
        newsCheckService.updateNewsDetails(claimData);
        emailService.sendEmailInfo(claimData);
    }


    @KafkaListener(topics = "fact-checked-data", groupId = "fact-check-service",containerFactory = "factCheckResponseKafkaListenerContainerFactory")
    public void consumeFactCheckedData(FactCheckResponse factCheckResponse) {
        log.info("claim - verified messages - {}", factCheckResponse);
    }
}
