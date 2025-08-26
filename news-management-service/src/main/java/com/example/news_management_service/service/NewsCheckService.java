package com.example.news_management_service.service;

import com.example.news_management_service.dto.response.ClaimData;
import com.example.news_management_service.dto.rrequest.NewsCheckRequest;
import com.example.news_management_service.dto.response.NewsCheckResponse;
import com.example.news_management_service.exception.NewsException;
import com.example.news_management_service.kafka.EventProducer;
import com.example.news_management_service.kafka.EventTopics;
import com.example.news_management_service.mapper.NewsMapper;
import com.example.news_management_service.model.News;
import com.example.news_management_service.repo.NewsRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsCheckService {

    private final NewsRepo newsRepo;
    private final NewsMapper newsMapper;
    private final EventProducer eventProducer;

    /**
     * Checks if a news article is fake or not.
     *
     * @param username the username of the user making the request
     * @param request  the news article to be checked
     * @return a string indicating whether the news article is fake or not
     */
    public String checkIfNewsIsFake(String username, NewsCheckRequest request) {
        if (request.getHeadline() != null && !request.getHeadline().isBlank()) {
            log.info("Checking news headline: {}", request.getHeadline());
            News news = newsMapper.toNews(request);
            news.setId(UUID.randomUUID().toString());
            news.setUsername(username);
            try {
                log.info("Saving news to database");
                newsRepo.save(news);
                String jsonData = "{" +
                        "\"claim\":\"" + news.getHeadline() + "\"," +
                        "\"username\":\"" + news.getUsername() +
                        "\"}";
                log.info("Sending news headline to kafka topic for verification");
                eventProducer.sendTopic(EventTopics.claims, jsonData);
                eventProducer.sendTopic(EventTopics.fact_check, jsonData);
                return "Saved successfully. Will let u know the check is done";
            } catch (Exception exception) {
                log.error("Error occurred while saving news", exception);
                throw new NewsException("Error occurred while saving news", exception);
            }
        }
        throw new NewsException("Invalid news");
    }


    /**
     * Retrieves a news article by its headline, but only if it was posted by the given user.
     *
     * @param headline the headline of the news article to retrieve
     * @param username the username of the user who posted the news article
     * @return the news article with the given headline, or null if not found
     */
    public NewsCheckResponse getNewsDetails(String headline, String username) {
        if (headline != null && !headline.isBlank()) {
            log.info("Retrieving news article with headline: {}", headline);
            Optional<News> news = newsRepo.findByHeadline(headline);
            if (news.isPresent() && news.get().getUsername().equals(username)) {
                log.info("Found news article with headline: {}", headline);
                return newsMapper.toNewsCheckResponse(news.get());
            }
            log.warn("No news article with headline: {}", headline);
        }
        return null;
    }

    /**
     * Updates the news details for a given claim headline and username.
     * If the news is not found, throws a NewsException.
     *
     * @param claimData the claim data containing the headline, username, result and evidence
     * @throws NewsException if the news is not found
     */
    public void updateNewsDetails(ClaimData claimData) {
        if (claimData.getClaim() != null) {
            log.info("Updating news headline: {}", claimData.getClaim());
            News news = newsRepo.findByHeadLineAndUsername(claimData.getClaim(), claimData.getUsername());
            if (news != null) {
                try {
                    newsRepo.updateNewsDetails(claimData.getResult(), claimData.getEvidence(), claimData.getClaim(), claimData.getUsername());
                    log.info("Updated news headline: {}", claimData.getClaim());
                } catch (Exception exception) {
                    log.error("Error occurred while updating news headline: {}", claimData.getClaim(), exception);
                    throw new NewsException(exception.getMessage());
                }
            } else {
                log.warn("No news headline found: {}", claimData.getClaim());
                throw new NewsException("News not found");
            }
        }
    }

    /**
     * Retrieves all news articles for a given user.
     *
     * @param username the username of the user
     * @param pageable the pagination object
     * @return a page of news articles checked by the user
     */
    public Page<NewsCheckResponse> getAllNewsForTheUser(String username, Pageable pageable) {
        log.info("Retrieving all news articles for user: {}", username);
        Page<News> newsPage = newsRepo.findAllNewsByUsername(username, pageable);
        log.info("Retrieved {} news articles for user: {}", newsPage.getTotalElements(), username);
        return newsMapper.toPageNewsCheckResponse(newsPage);
    }
}
