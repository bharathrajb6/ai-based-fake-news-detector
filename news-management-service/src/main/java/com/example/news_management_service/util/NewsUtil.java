package com.example.news_management_service.util;

import com.example.news_management_service.dto.response.FactCheckResponse;
import org.springframework.stereotype.Component;

@Component
public class NewsUtil {

    public boolean isMostlyFake(FactCheckResponse response) {
        long fakeCount = response.getClaims().stream()
                .flatMap(c -> c.getClaimReview().stream())
                .filter(r -> "False".equalsIgnoreCase(r.getTextualRating()))
                .count();

        long realCount = response.getClaims().stream()
                .flatMap(c -> c.getClaimReview().stream())
                .filter(r -> !"False".equalsIgnoreCase(r.getTextualRating()))
                .count();

        return fakeCount > realCount;
    }
}
