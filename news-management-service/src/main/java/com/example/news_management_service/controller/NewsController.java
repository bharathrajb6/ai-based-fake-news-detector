package com.example.news_management_service.controller;

import com.example.news_management_service.dto.NewsCheckRequest;
import com.example.news_management_service.dto.NewsCheckResponse;
import com.example.news_management_service.service.NewsCheckService;
import lombok.RequiredArgsConstructor;
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


    @RequestMapping(value = "/getNewsDetails", method = RequestMethod.GET)
    public NewsCheckResponse getNewsDetails(@RequestParam(name = "headline") String headline) {
        return newsCheckService.getNewsDetails(headline);
    }
}
