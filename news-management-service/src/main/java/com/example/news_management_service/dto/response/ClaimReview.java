package com.example.news_management_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimReview {
    private Publisher publisher;
    private String url;
    private String title;
    private String textualRating;
    private String languageCode;
}
