package com.example.news_management_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsCheckResponse {
    private String id;
    private String username;
    private String headline;
    private String author;
    private String result;
    private Long credibilityScore;
    private String evidence;
    private String sourceSite;
    private Double sourceTrustScore;
}
