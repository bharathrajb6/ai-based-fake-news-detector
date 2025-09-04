package com.example.source_credibility_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceCredibilityResponse {
    private String site;
    private long totalArticles;
    private long fakeByAi;
    private long fakeByHuman;
    private double externalReputation; // 0..1
    private double trustScore;         // 0..1
    private String headline;           // echo back for correlation
    private String username;           // echo back for correlation
}

