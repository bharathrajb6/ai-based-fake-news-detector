package com.example.source_credibility_service.controller;

import com.example.source_credibility_service.dto.request.EvaluateSourceRequest;
import com.example.source_credibility_service.dto.response.SourceCredibilityResponse;
import com.example.source_credibility_service.service.SourceCredibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credibility")
@RequiredArgsConstructor
@Slf4j
public class SourceCredibilityController {

    private final SourceCredibilityService sourceCredibilityService;

    @PostMapping("/evaluate")
    public ResponseEntity<SourceCredibilityResponse> evaluate(@RequestBody EvaluateSourceRequest request) {
        log.info("Evaluating source credibility: {}", request);
        return ResponseEntity.ok(sourceCredibilityService.evaluate(request));
    }
}

