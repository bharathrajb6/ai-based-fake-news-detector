package com.example.fact_check_service.service;

import com.example.fact_check_service.dto.request.ClaimData;
import com.example.fact_check_service.dto.response.FactCheckResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FactCheckService {

    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String API_KEY = "AIzaSyBk1dW-2JTVO82SlQJf_Ce4nZrNIXk0_18";
    private static final String BASE_URL = "https://factchecktools.googleapis.com/v1alpha1/claims:search";

    /**
     * Consumes ClaimData from topic "fact-check" and queries the Google Fact Check API.
     * Publishes the resulting {@link com.example.fact_check_service.dto.response.FactCheckResponse}
     * to topic "fact-checked-data" and updates the news data.
     *
     * @param claimData the claim payload to be checked
     * @throws JsonProcessingException if there is a problem with JSON processing
     */
    @KafkaListener(topics = "fact-check", groupId = "fact-check-service", containerFactory = "claimDataKafkaListenerContainerFactory")
    public void checkClaim(ClaimData claimData) throws JsonProcessingException {
        if (claimData == null) {
            log.warn("ClaimData in checkClaim is null");
            return;
        }

        log.info("Received ClaimData to check: {}", claimData);
        log.info("Checking claim text: {}", claimData.getClaim());
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL).queryParam("query", "{claim}").queryParam("languageCode", "en").queryParam("pageSize", 3).queryParam("key", API_KEY).build(false)  // disables encoding
                .expand(claimData.getClaim()).toUriString();

        log.info("Querying Google Fact Checking API with URL: {}", url);
        try {
            FactCheckResponse response = restTemplate.getForObject(url, FactCheckResponse.class);
            response.setHeadline(claimData.getClaim());
            log.info("Response from Google Fact Checking API: {}", response);

            if (response != null) {
                String factCheckResponse = new ObjectMapper().writeValueAsString(response);
                log.info("Publishing fact check response to topic: fact-checked-data");
                updateNewsData(response);
                log.info("Successfully published and updated news data for headline: {}", response.getHeadline());
            } else {
                log.warn("Response from Google Fact Checking API is null");
            }
        } catch (Exception exception) {
            log.error("Error while querying Google Fact Checking API", exception);
        }
    }


    /**
     * Publishes the fact check response to the "fact-checked-data" topic.
     *
     * @param factCheckResponse the fact check response payload
     */
    public void updateNewsData(FactCheckResponse factCheckResponse) {
        log.info("Publishing FactCheckResponse to topic 'fact-checked-data': {}", factCheckResponse);
        kafkaTemplate.send("fact-checked-data", factCheckResponse);
        log.info("Published fact check response to topic: fact-checked-data");
    }
}
