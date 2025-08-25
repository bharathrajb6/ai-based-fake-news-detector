package com.example.news_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsCheckResponse {
    private String id;
    private String headline;
    private boolean isFake;
    private Long credibilityScore;
}
