package com.example.news_management_service.service;

import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
import com.example.news_management_service.kafka.ClaimConsumer;
import com.example.news_management_service.kafka.ClaimProducer;
import com.example.news_management_service.mapper.NewsMapper;
import com.example.news_management_service.model.News;
import com.example.news_management_service.repo.NewsRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsCheckService {

    private final NewsRepo newsRepo;
    private final NewsMapper newsMapper;
    private final ClaimProducer claimProducer;

    public String checkIfNewsIsFake(String username, NewsCheckRequest request) {
        if (request.getHeadline() != null && !request.getHeadline().isBlank()) {
            News news = newsMapper.toNews(request);
            news.setId(UUID.randomUUID().toString());
            news.setUsername(username);
            try {
                newsRepo.save(news);
                claimProducer.sendClaim(news.getHeadline());
                return "Saved successfully. Will let u know the check is done";
            } catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
        throw new RuntimeException("Invalid news");
    }


    public NewsCheckResponse getNewsDetails(String headline) {
        if (headline != null && !headline.isBlank()) {
            Optional<News> news = newsRepo.findByHeadline(headline);

            if (news.isPresent()) {
                return newsMapper.toNewsCheckResponse(news.get());
            }
        }
        return null;
    }
}
