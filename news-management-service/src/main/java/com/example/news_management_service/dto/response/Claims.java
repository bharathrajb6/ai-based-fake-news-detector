package com.example.news_management_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claims {
    private String text;
    private String claimant;
    private Timestamp claimDate;
    private List<ClaimReview> claimReview;
}
