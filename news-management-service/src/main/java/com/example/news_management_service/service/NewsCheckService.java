package com.example.news_management_service.service;

import com.example.news_management_service.dto.response.ClaimData;
import com.example.news_management_service.dto.request.NewsCheckRequest;
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
    public NewsCheckResponse checkIfNewsIsFake(String username, NewsCheckRequest request) {
        if (request == null || request.getHeadline() == null || request.getHeadline().isBlank()) {
            throw new NewsException("Invalid news");
        }

        log.info("Checking news headline: {}", request.getHeadline());
        News news = newsMapper.toNews(request);
        news.setId(UUID.randomUUID().toString());
        news.setUsername(username);

        try {
            log.info("Saving news to database");
            News savedNews = newsRepo.save(news);
            if (savedNews == null) {
                throw new NewsException("Error occurred while saving news");
            }

            String jsonData = "{" + "\"claim\":\"" + savedNews.getHeadline() + "\"," + "\"username\":\"" + savedNews.getUsername() + "\"}";
            log.info("Sending news headline to kafka topic for verification");
            eventProducer.sendTopic(EventTopics.claims, jsonData);
            eventProducer.sendTopic(EventTopics.fact_check, jsonData);

            // Source credibility evaluation (Kafka): publish request for evaluation
            String site = request.getSourceSite();
            if (site != null && !site.isBlank()) {
                String credibilityPayload = "{" +
                        "\"site\":\"" + site + "\"," +
                        "\"headline\":\"" + savedNews.getHeadline() + "\"," +
                        "\"username\":\"" + savedNews.getUsername() + "\"}";
                log.info("Sending source site for credibility evaluation to kafka topic '{}' with payload: {}", EventTopics.credibility_check, credibilityPayload);
                eventProducer.sendTopic(EventTopics.credibility_check, credibilityPayload);
            } else {
                log.warn("No sourceSite provided in request; skipping source credibility evaluation");
            }
            return newsMapper.toNewsCheckResponse(news);
        } catch (Exception exception) {
            log.error("Error occurred while saving news", exception);
            throw new NewsException("Error occurred while saving news", exception);
        }
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

    /**
     * Updates the source credibility fields for a specific news record identified by username and headline.
     *
     * @param username   the username who created the news record
     * @param headline   the headline of the news record
     * @param site       the source site/domain
     * @param trustScore the computed trust score (0..1)
     */
    public void updateSourceCredibility(String username, String headline, String site, Double trustScore) {
        if (headline == null || username == null) {
            log.warn("Cannot update source credibility due to missing headline/username. headline={}, username={}", headline, username);
            return;
        }
        try {
            newsRepo.updateSourceCredibility(trustScore, site, headline, username);
            log.info("Updated source credibility for headline='{}', username='{}' with site='{}', trustScore={}", headline, username, site, trustScore);
        } catch (Exception ex) {
            log.error("Failed to update source credibility for headline='{}', username='{}'", headline, username, ex);
            throw new NewsException("Failed to update source credibility", ex);
        }
    }
}
