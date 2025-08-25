package com.example.news_management_service.service;

import com.example.news_management_service.dto.ClaimData;
import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
import com.example.news_management_service.exception.NewsException;
import com.example.news_management_service.kafka.EventProducer;
import com.example.news_management_service.kafka.EventTopics;
import com.example.news_management_service.mapper.NewsMapper;
import com.example.news_management_service.model.News;
import com.example.news_management_service.repo.NewsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsCheckService {

    private final NewsRepo newsRepo;
    private final NewsMapper newsMapper;
    private final EventProducer eventProducer;

    public String checkIfNewsIsFake(String username, NewsCheckRequest request) {
        if (request.getHeadline() != null && !request.getHeadline().isBlank()) {
            News news = newsMapper.toNews(request);
            news.setId(UUID.randomUUID().toString());
            news.setUsername(username);
            try {
                newsRepo.save(news);
                String jsonData = "{" +
                        "\"claim\":\"" + news.getHeadline() + "\"," +
                        "\"username\":\"" + news.getUsername() +
                        "\"}";
                eventProducer.sendTopic(EventTopics.claims, jsonData);
                return "Saved successfully. Will let u know the check is done";
            } catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
        throw new RuntimeException("Invalid news");
    }


    public NewsCheckResponse getNewsDetails(String headline, String username) {
        if (headline != null && !headline.isBlank()) {
            Optional<News> news = newsRepo.findByHeadline(headline);
            if (news.isPresent() && news.get().getUsername().equals(username)) {
                return newsMapper.toNewsCheckResponse(news.get());
            }
        }
        return null;
    }

    public void updateNewsDetails(ClaimData claimData) {
        if (claimData.getClaim() != null) {
            News news = newsRepo.findByHeadLineAndUsername(claimData.getClaim(), claimData.getUsername());
            if (news != null) {
                try {
                    newsRepo.updateNewsDetails(claimData.getResult(), claimData.getEvidence(), claimData.getClaim(), claimData.getUsername());
                } catch (Exception exception) {
                    throw new NewsException(exception.getMessage());
                }
            }
            throw new NewsException("News not found");
        }
    }

    public Page<NewsCheckResponse> getAllNewsForTheUser(String username, Pageable pageable) {
        Page<News> newsPage = newsRepo.findAllNewsByUsername(username, pageable);
        return newsMapper.toPageNewsCheckResponse(newsPage);
    }
}
