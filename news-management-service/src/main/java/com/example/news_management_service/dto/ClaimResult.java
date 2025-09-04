package com.example.news_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimResult {
    private String claim;
    private boolean isFake;
    private List<String> proofs;
}
