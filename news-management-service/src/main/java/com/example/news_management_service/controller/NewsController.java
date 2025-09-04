package com.example.news_management_service.controller;

import com.example.news_management_service.dto.request.NewsCheckRequest;
import com.example.news_management_service.dto.response.NewsCheckResponse;
import com.example.news_management_service.service.NewsCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsCheckService newsCheckService;


    /**
     * Checks if a news article is fake or not.
     *
     * @param username the username of the user making the request
     * @param request  the news article to be checked
     * @return a string indicating whether the news article is fake or not
     */
    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public NewsCheckResponse postNewsForFakeCheck(@RequestHeader(value = "X-Username", required = false) String username,
                                                  @RequestBody NewsCheckRequest request) {
        return newsCheckService.checkIfNewsIsFake(username, request);
    }


    /**
     * Retrieves all news articles checked by the given user.
     *
     * @param username the username of the user
     * @param pageable the pagination object
     * @return a page of news articles checked by the user
     */
    @RequestMapping(value = "/getAllNews", method = RequestMethod.GET)
    public Page<NewsCheckResponse> getAllNewsForTheUser(@RequestHeader(value = "X-Username", required = false) String username, Pageable pageable) {
        return newsCheckService.getAllNewsForTheUser(username, pageable);
    }

    /**
     * Retrieves a news article by its headline.
     *
     * @param username the username of the user making the request
     * @param headline the headline of the news article to retrieve
     * @return the news article with the given headline
     */
    @RequestMapping(value = "/getNewsByHeadline", method = RequestMethod.GET)
    public NewsCheckResponse getNewsByHeadline(@RequestHeader(value = "X-Username", required = false) String username, @RequestParam(name = "headline") String headline) {
        return newsCheckService.getNewsDetails(headline, username);
    }
}
