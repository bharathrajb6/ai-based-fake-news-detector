package com.example.news_management_service.controller;

import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
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


    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public String postNewsForFakeCheck(@RequestHeader(value = "X-Username", required = false) String username,
                                       @RequestBody NewsCheckRequest request) {
        return newsCheckService.checkIfNewsIsFake(username, request);
    }


    @RequestMapping(value = "/getAllNews", method = RequestMethod.GET)
    public Page<NewsCheckResponse> getAllNewsForTheUser(@RequestHeader(value = "X-Username", required = false) String username, Pageable pageable) {
        return newsCheckService.getAllNewsForTheUser(username,pageable);
    }

    public NewsCheckResponse getNewsByHeadline(@RequestHeader(value = "X-Username", required = false) String username, @RequestParam String headline){
        return newsCheckService.getNewsDetails(headline,username);
    }
}
